package com.xqq.myradar.task.Timely;

import com.alibaba.fastjson.JSONObject;
import com.xqq.myradar.radar.Buffer.RealDataBuffer;
import com.xqq.myradar.radar.Buffer.SectionBuffer;
import com.xqq.myradar.radar.Buffer.TrajBuffer;
import com.xqq.myradar.radar.Model.Trajectory;
import com.xqq.myradar.radar.Model.TrajectoryModel;
import com.xqq.myradar.radar.Model.TrajectoryModelLine;
import com.xqq.myradar.radar.Model.VehicleModel;
import com.xqq.myradar.redis.JedisUtil;

import com.xqq.myradar.task.carplateBind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class dataFusion {
    static  long flagTimeStamp = 0; //

//    @Scheduled(initialDelay = 2000,fixedRate = 200)//延迟2秒开始数据融合，每200ms一次
    public void dataProcess(){
        int calculateTime = 5*60*1000; //统计交通流参数的时间

        long timeStamp = JedisUtil.getDedupTime();//拿到数据去重后的数据
        if (timeStamp == 0)
            return;
        while (timeStamp != 0){

            long getDataTimeStamp = timeStamp ;//当前进行融合计算的时间

            //开始计算断面的数据
            SectionBuffer sectionBuffer = SectionBuffer.getS();
            if (sectionBuffer.CurrentCalcuTime == 0){
                sectionBuffer.CurrentCalcuTime = getDataTimeStamp;
                sectionBuffer.sectionListModel(getDataTimeStamp);//初始化
            }else {
                if (getDataTimeStamp - sectionBuffer.CurrentCalcuTime >= calculateTime)
                    sectionBuffer.sectionData2Database(getDataTimeStamp);
            }

            List<VehicleModel> vm = new LinkedList<>();
            JedisUtil.getCurrentData(getDataTimeStamp,vm);
            trajFusionTest(vm,getDataTimeStamp);

            timeStamp = JedisUtil.getDedupTime();
        }
    }
    public static void trajFusionTest(List<VehicleModel> Data,long nowtimeStamp) {
        Map<String,String> trajectoryList2Redis = new HashMap<>(); //存储往Redis发送的数据
        List<Trajectory> trajectoryList2DataBase = new LinkedList<>();  //存储往数据库发送的数据
        int Timeinter = 200;
        int delX = 50;
        int delY = 8;
        TrajBuffer trajBuffer = TrajBuffer.getS();
        List<TrajectoryModelLine> trajectoryModelLineArrayList = trajBuffer.trajectoryModelLineArrayList;
        if (Data.size() > 0) {//如果实时数据集为空退出轨迹融合
            Data.sort(Comparator.comparing(VehicleModel::getFrenetx)); //根据frenet排序由小到大排序
            for (VehicleModel datum : Data) {
                //此处为补充未匹配轨迹和匹配轨迹相同赋值·
/*            if (datum.getFrenetx() == 0)
                continue;*/
                int frontCar = -999;
                //新轨迹或者匹配成功帧数均来自实时数据部分赋值
                TrajectoryModel newFrametrajectoryModel = new TrajectoryModel();    //给要补充帧的数据添加空间
                newFrametrajectoryModel.setType(datum.getType());  //类型为文字。
                newFrametrajectoryModel.setFrenetx(datum.getFrenetx());
                newFrametrajectoryModel.setFrenety(datum.getFrenety());
                newFrametrajectoryModel.setSpeedx(datum.getSpeedx());
                newFrametrajectoryModel.setSpeedy(datum.getSpeedy());
                newFrametrajectoryModel.setHeadingAngle(datum.getHeadingAngle());
                newFrametrajectoryModel.setLongitude(datum.getLongitude());
                newFrametrajectoryModel.setLatitude(datum.getLatitude());
                newFrametrajectoryModel.setMercatorx((float) datum.getMercatorx());
                newFrametrajectoryModel.setMercatory((float) datum.getMercatory());
                newFrametrajectoryModel.setRoadDirect(datum.getRoadDirect());
                newFrametrajectoryModel.setLane(datum.getLane());
                newFrametrajectoryModel.setRawId(datum.getId()); //雷达分配给车的id
                newFrametrajectoryModel.setTimeStamp(nowtimeStamp);
                newFrametrajectoryModel.setCarId(datum.getCarId());
//                newFrametrajectoryModel.setLicenseColor(datum.getLiscenseColor());
                if (datum.getLiscenseColor() == -1)
                    newFrametrajectoryModel.setLicenseColor(0);
                else
                    newFrametrajectoryModel.setLicenseColor(datum.getLiscenseColor());  //配置车牌颜色
                //找前车
                if (datum.getRoadDirect() == 1) { //在道路右侧
                    for (int i = 0; i < Data.size(); i++) {
                        if (datum.getRoadDirect() == Data.get(i).getRoadDirect())//同方向
                            if (datum.getFrenetx() > Data.get(i).getFrenetx() && datum.getLane() == Data.get(i).getLane()) {//同车道
                                frontCar = i;    //找到前车
                                break;
                            }

                    }
                }
                if (datum.getRoadDirect() == 2) { //在道路左侧  逆序找第一个比他小的
                    for (int i = Data.size() - 1; i >= 0; i--) {
                        if (datum.getRoadDirect() == Data.get(i).getRoadDirect())
                            if (datum.getFrenetx() < Data.get(i).getFrenetx() && datum.getLane() == Data.get(i).getLane()) {
                                frontCar = i;
                                break;
                            }

                    }
                }
                // System.out.println("匹配前车");
                if (frontCar != -999) {                        //如果存在前车

                    double DHW = Math.abs(datum.getFrenetx() - Data.get(frontCar).getFrenetx());
                    double THW = 0;
                    double TTC = 0;
                    if (datum.getSpeedx() != 0)
                        THW = DHW / Math.abs(datum.getSpeedx());
                    if (DHW != 0)
                        TTC = (datum.getSpeedx() - Data.get(frontCar).getSpeedx())/DHW;
                    newFrametrajectoryModel.setDHW((float) DHW);
                    newFrametrajectoryModel.setTHW((float) THW);
                    newFrametrajectoryModel.setTTC((float) TTC);

                }
                //计算两个预测值
                double frenetxPrediction = datum.getFrenetx() + datum.getSpeedx() * Timeinter / 1000; //计算帧的值
                double frenetyPrediction = datum.getFrenety();
                newFrametrajectoryModel.setFrenetxPrediction(frenetxPrediction);//下一帧可能出现的位置
                newFrametrajectoryModel.setFrenetyPrediction(frenetyPrediction);

                int matchFlag = 0;
                int matchPriorIndex = -1; //存储匹配光纤的下标
                double matchPriorCount = 9999;//存储优先级计算指数,设置一个很大的数，满足条件就可以更新
                for (int bufferIndex = 0;bufferIndex < trajectoryModelLineArrayList.size();bufferIndex++){
                    int lastFram = trajectoryModelLineArrayList.get(bufferIndex).trajectoryModels.size() - 1;//轨迹的最后一帧
                    //设置光纤·ID匹配优先级最高
                    if (trajectoryModelLineArrayList.get(bufferIndex).trajectoryModels.get(lastFram).getRawId() == datum.getId()){
                        matchFlag = 1;
                        matchPriorIndex = bufferIndex;
                        break;
                    }

                    if (trajectoryModelLineArrayList.get(bufferIndex).state == 0 &&//未匹配轨迹
                            trajectoryModelLineArrayList.get(bufferIndex).trajectoryModels.get(lastFram).getRoadDirect() == datum.getRoadDirect() &&//方向相同
                            Math.abs(trajectoryModelLineArrayList.get(bufferIndex).trajectoryModels.get(lastFram).getFrenetxPrediction() - datum.getFrenetx()) <= delX &&//误差范围
                            Math.abs(trajectoryModelLineArrayList.get(bufferIndex).trajectoryModels.get(lastFram).getFrenetyPrediction() - datum.getFrenety()) <= delY) {//误差范围
                        //匹配轨迹成功
                        double frenetxDisparity = Math.abs(trajectoryModelLineArrayList.get(bufferIndex).trajectoryModels.get(lastFram).getFrenetxPrediction() - datum.getFrenetx());//x预测误差
                        double frenetyDisparity = Math.abs(trajectoryModelLineArrayList.get(bufferIndex).trajectoryModels.get(lastFram).getFrenetyPrediction() - datum.getFrenety());//y预测误差
                        if (calPriority(frenetxDisparity,frenetyDisparity) < matchPriorCount){
                            matchPriorIndex = bufferIndex;
                            matchPriorCount = calPriority(frenetxDisparity,frenetyDisparity);//更新最匹配最接近的
                        }

                        matchFlag = 1;
                    }
                }
                if (matchPriorIndex != -1){ //匹配成功，找到最优解
                    int lastFram = trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.size() - 1;
                    try{
                        //牌照更新为自编牌照数据
                        if (newFrametrajectoryModel.getCarId().equals("0")){
                            newFrametrajectoryModel.setCarId(trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.get(lastFram).getCarId()); //沿用车牌照
                            newFrametrajectoryModel.setLicenseColor(trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.get(lastFram).getLicenseColor());//沿用颜色

                        }
                    }
                    catch (Exception e){
                        log.warn("carID出错了，数据为空"+datum.toString());
                        log.warn("carID出错了，数据为空"+trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.get(lastFram).toString());
                    }

                    newFrametrajectoryModel.setTrajId(trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.get(lastFram).getTrajId());  //沿用轨迹id
                    float acc = (newFrametrajectoryModel.getSpeedx() - trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.get(lastFram).getSpeedx()) * 1000 / Timeinter;//计算加速度
                    trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.get(lastFram).setAccx(acc);
                    trajectoryModelLineArrayList.get(matchPriorIndex).trajectoryModels.add(newFrametrajectoryModel); //匹配成功后将数据加入到轨迹后一时刻
                    trajectoryModelLineArrayList.get(matchPriorIndex).state = 1;
                    trajectoryModelLineArrayList.get(matchPriorIndex).emptyFrameNum = 0;
                    trajectoryModelLineArrayList.get(matchPriorIndex).valiateFrameNum++;
                }

                //未找到匹配的轨迹处理
                if (matchFlag == 0) {
                    //匹配轨迹失败，新开轨迹
                    TrajectoryModelLine newTrajectoryModelLine = new TrajectoryModelLine();
                    newTrajectoryModelLine.state = 2;
                    newTrajectoryModelLine.emptyFrameNum = 0;
                    newTrajectoryModelLine.valiateFrameNum = 1;
                    long getTrajId = trajBuffer.getMaxTrajId();

                    getTrajId = getTrajId%1000;

                    long trajIdPattern = (datum.getTimestamp()/1000)*1000+getTrajId;

                    newFrametrajectoryModel.setTrajId(trajIdPattern); //赋予新的轨迹编号
                    //如果没有牌照数据，将设置牌照数据为A11+轨迹号后三位
                    if (newFrametrajectoryModel.getCarId().equals("0")){
                        String picLicense = "";
                        long ID = trajBuffer.getMaxTrajId();
                        ID = ID%1000;
                        if (ID < 10)
                            picLicense = "鄂A1100" + ID;
                        else if (ID >= 10&& ID < 100)
                            picLicense = "鄂A110" + ID;
                        else {
                            picLicense = "鄂A11" + ID;
                        }
                        newFrametrajectoryModel.setCarId(picLicense);
                        newFrametrajectoryModel.setLicenseColor(9);
                    }
                    newTrajectoryModelLine.trajectoryModels = new LinkedList<>();
                    newTrajectoryModelLine.trajectoryModels.add(newFrametrajectoryModel); //将新的帧添加到新的轨迹中
                    trajectoryModelLineArrayList.add(newTrajectoryModelLine);  //将新的轨迹添加到缓冲区

                }

            }
        }
        carplateBind.carIdConnect(nowtimeStamp);

        //对缓冲区未找到帧的轨迹补充帧
        for (TrajectoryModelLine trajectoryModelLine : trajectoryModelLineArrayList) {
            //System.out.println("处理缓冲区11111111");
            if (trajectoryModelLine.state == 0){
                TrajectoryModel newFrametrajectoryModel = new TrajectoryModel();    //给要补充帧的数据添加空间
                int lastFram = trajectoryModelLine.trajectoryModels.size() - 1;
                newFrametrajectoryModel.setTimeStamp(nowtimeStamp);
                newFrametrajectoryModel.setSpeedx(trajectoryModelLine.trajectoryModels.get(lastFram).getSpeedx());
                newFrametrajectoryModel.setSpeedy(trajectoryModelLine.trajectoryModels.get(lastFram).getSpeedy());
                double frenetxPrediction = newFrametrajectoryModel.getSpeedx()*Timeinter/1000+trajectoryModelLine.trajectoryModels.get(lastFram).getFrenetxPrediction();
                newFrametrajectoryModel.setFrenetxPrediction(frenetxPrediction);
                double frenetyPrediction = trajectoryModelLine.trajectoryModels.get(lastFram).getFrenety();
                //对补充的数据部分赋值
                newFrametrajectoryModel.setFrenetyPrediction(frenetyPrediction);
                newFrametrajectoryModel.setFrenetx(trajectoryModelLine.trajectoryModels.get(lastFram).getFrenetxPrediction());
                newFrametrajectoryModel.setFrenety(trajectoryModelLine.trajectoryModels.get(lastFram).getFrenetyPrediction());
                newFrametrajectoryModel.setLongitude(trajectoryModelLine.trajectoryModels.get(lastFram).getLongitude());
                newFrametrajectoryModel.setLatitude(trajectoryModelLine.trajectoryModels.get(lastFram).getLatitude());
                newFrametrajectoryModel.setRoadDirect(trajectoryModelLine.trajectoryModels.get(lastFram).getRoadDirect());
                newFrametrajectoryModel.setTrajId(trajectoryModelLine.trajectoryModels.get(lastFram).getTrajId());
                newFrametrajectoryModel.setCarId(trajectoryModelLine.trajectoryModels.get(lastFram).getCarId());
                newFrametrajectoryModel.setLicenseColor(trajectoryModelLine.trajectoryModels.get(lastFram).getLicenseColor());
                newFrametrajectoryModel.setLane(trajectoryModelLine.trajectoryModels.get(lastFram).getLane());

                ListIterator<TrajectoryModel> trajectoryModelIterator = trajectoryModelLine.trajectoryModels.listIterator();
                while (trajectoryModelIterator.hasNext()){
                    trajectoryModelIterator.next();
                }
                trajectoryModelIterator.add(newFrametrajectoryModel);

                //将轨迹出现的空帧数加一
                trajectoryModelLine.emptyFrameNum++;
            }
        }
        //以下实现对缓存区遍历，删除空轨迹和左移、将全部的state设置为零
        Iterator iterator = trajectoryModelLineArrayList.iterator();
        while (iterator.hasNext()){
            TrajectoryModelLine trajectoryModelLine = (TrajectoryModelLine) iterator.next();
//            log.warn("轨迹融合缓冲区的数据"+trajectoryModelLineArrayList.size());
            trajectoryModelLine.state = 0;
            if (trajectoryModelLine.trajectoryModels.size() == 4) {
                //这里计算timestamp时需要注意
                if (trajectoryModelLine.emptyFrameNum == 3){                    //去除三帧为空的轨迹和只出现一次的噪点
                    if (trajectoryModelLine.valiateFrameNum > 1) {
                        //记录牌照和轨迹的关系
                        //Data2Mysql.saveTrajCarplate(trajectoryModelLine.trajectoryModels.get(0));
                    }

                    iterator.remove();
                }

                else {
                    long trajId = trajectoryModelLine.trajectoryModels.get(0).getTrajId();
                    trajectoryList2DataBase.add(trajectoryModelLine.trajectoryModels.get(0).trajectoryModelToTrajectory());
                    trajectoryList2Redis.put(String.valueOf(trajId), JSONObject.toJSONString(trajectoryModelLine.trajectoryModels.get(0).trajectoryModelToTrajectory()));
                    //统计数据流量
                    SectionBuffer.getS().sectionStatic(trajectoryModelLine.trajectoryModels.get(0).trajectoryModelToTrajectory());

                    Iterator<TrajectoryModel> trajectoryModelIterator = trajectoryModelLine.trajectoryModels.iterator();
                    trajectoryModelIterator.next();
                    trajectoryModelIterator.remove(); //删除最左列
                }

            }
        }

        if (trajectoryList2Redis.size() > 0){

            //将数据写入到Redis中
            trajBuffer.insertBuffer2Redis(trajectoryList2Redis,trajectoryList2DataBase.get(0).getTimeStamp());
            //将写入数据库的操作变成异步线程
            RealDataBuffer.addTraj(trajectoryList2DataBase);
//            trajBuffer.insertBuffer2DataBase(trajectoryList2DataBase);   //注意这里是写入数据库


        }
    }
    public static float calPriority(double frenetx, double frenety){
        return (float) (0.3*frenetx+0.7*frenety);
    }



}
