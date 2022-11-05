package com.xqq.myradar.task;

import com.xqq.myradar.radar.Buffer.TrajBuffer;
import com.xqq.myradar.radar.Model.CarplateV3Model;
import com.xqq.myradar.radar.Model.TrajectoryModelLine;
import com.xqq.myradar.redis.JedisUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class carplateBind {
    public static void carIdConnect(long timeStamp){

        int timeGap = 20*1000;
        //直接对缓冲区进行牌照绑定，绑定数据为全部的牌照数据；//其中牌照设定为数据库存储10s；数据量不大；
        TrajBuffer trajBuffer = TrajBuffer.getS();//获取轨迹缓冲区的数据
        List<CarplateV3Model> carplateV3Model10Secs= new ArrayList<>();//获取到当前的牌照数据
        JedisUtil.selectPic10Sec(carplateV3Model10Secs);
        long realTimeStamp  = timeStamp;
        timeStamp = (timeStamp/1000)*1000+1000;
        List<CarplateV3Model> cV3ListThisSec = new ArrayList<>(); //存储当前时间的牌照数据
        List<CarplateV3Model> cV3List5Sec = new ArrayList<>();      //存储当前时间往前5s的牌照数据
        List<CarplateV3Model> rmCarPlate = new ArrayList<>();  //要删除的牌照数据
        for (CarplateV3Model carplateV3Model : carplateV3Model10Secs) {
            if (carplateV3Model.getGlobalTimeStamp()== timeStamp){
                cV3ListThisSec.add(carplateV3Model);
            }else if (timeStamp- carplateV3Model.getGlobalTimeStamp() <= 5000&&
                    timeStamp - carplateV3Model.getGlobalTimeStamp() > 0 ){
                cV3List5Sec.add(carplateV3Model);
            }
        }

        for (CarplateV3Model carplateV3Model : cV3ListThisSec) {//同时间绑定
            int trajPrior = -1; //绑定牌照的轨迹下标
            double trajPriorNum = 999;//计算参数
            for (int trajBufferIndex = 0; trajBufferIndex < trajBuffer.trajectoryModelLineArrayList.size(); trajBufferIndex++) {
                int latFram = trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).trajectoryModels.size()-1;//最后一帧

                if (carplateV3Model.getRoadDirect().equals(trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).trajectoryModels.get(latFram).getRoadDirect() == 1 ? "1" : "2")&&//方向相同
                        carplateV3Model.getStart() <= trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).trajectoryModels.get(latFram).getFrenetx()&&//位置相近
                        carplateV3Model.getEnd() >= trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).trajectoryModels.get(latFram).getFrenetx()&&//位置相近
                        timeStamp - trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).carIdTimestamp >=timeGap&&//距离上一次绑定的时间要超过20s
                        trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).state != 0
                ){
                    double carId = (carplateV3Model.getStart()+carplateV3Model.getEnd())/2;
                    if (Math.abs(trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).trajectoryModels.get(latFram).getFrenetx()-carId) < trajPriorNum){
                        trajPriorNum = Math.abs(trajBuffer.trajectoryModelLineArrayList.get(trajBufferIndex).trajectoryModels.get(latFram).getFrenetx()-carId);
                        trajPrior = trajBufferIndex;
                    }//距离最接近中间的

                }
            }
            if (trajPrior == -1) //未匹配成功，跳过
                continue;

            int latFram = trajBuffer.trajectoryModelLineArrayList.get(trajPrior).trajectoryModels.size()-1;
            trajBuffer.trajectoryModelLineArrayList.get(trajPrior).carIdTimestamp = timeStamp;
            trajBuffer.trajectoryModelLineArrayList.get(trajPrior).trajectoryModels.get(latFram).setCarId(carplateV3Model.getPicLicense());
            trajBuffer.trajectoryModelLineArrayList.get(trajPrior).trajectoryModels.get(latFram).setLicenseColor(carplateV3Model.getLicenseColor());
            rmCarPlate.add(carplateV3Model);//绑定成功，把这个牌照要删除

        }
        /**
         * 模糊查询问题
         */
        if (cV3List5Sec.size() > 0){//5秒之前还有数据没有处理，拿旧牌照匹配新轨迹
            System.out.println("进入牌照绑定模糊查询");
            for (TrajectoryModelLine trajectoryModelLine : trajBuffer.trajectoryModelLineArrayList) {
                int latFram = trajectoryModelLine.trajectoryModels.size()-1;
                int priorIndex = -1;
                double deltV = 999;//表示速度的求解计算
                for (int cV3Index = 0; cV3Index < cV3List5Sec.size(); cV3Index++) {

                    if ((trajectoryModelLine.trajectoryModels.get(latFram).getRoadDirect() == 1 ?"1" : "2").equals(cV3List5Sec.get(cV3Index).getRoadDirect())&&
                            trajectoryModelLine.trajectoryModels.get(latFram).getFrenetx() <= cV3List5Sec.get(cV3Index).getEnd()+20&&
                            trajectoryModelLine.trajectoryModels.get(latFram).getFrenetx() >= cV3List5Sec.get(cV3Index).getStart()-20&&
                            timeStamp - trajectoryModelLine.carIdTimestamp>= timeGap
                    ){
                        double carIdLocation = (cV3List5Sec.get(cV3Index).getStart()+cV3List5Sec.get(cV3Index).getEnd())/2;
                        double cv3timeGap = realTimeStamp - cV3List5Sec.get(cV3Index).getGlobalTimeStamp();
                        cv3timeGap=cv3timeGap/1000;
                        double thisDeltV = Math.abs(carIdLocation - trajectoryModelLine.trajectoryModels.get(latFram).getFrenetx())/cv3timeGap - Math.abs(trajectoryModelLine.trajectoryModels.get(latFram).getSpeedx());
                        thisDeltV = Math.abs(thisDeltV);//找最接近的
                        if (thisDeltV < deltV ){
                            deltV = thisDeltV;
                            priorIndex = cV3Index;
                        }
                    }

                }
                if (priorIndex != -1){
                    trajectoryModelLine.carIdTimestamp = timeStamp;
                    trajectoryModelLine.trajectoryModels.get(latFram).setCarId(cV3List5Sec.get(priorIndex).getPicLicense());
                    trajectoryModelLine.trajectoryModels.get(latFram).setLicenseColor(cV3List5Sec.get(priorIndex).getLicenseColor());
                    rmCarPlate.add(cV3List5Sec.get(priorIndex));
                }
            }
        }
        if (rmCarPlate.size()>0)
            log.warn("删除牌照数据");
        JedisUtil.deleteRedisCarId(rmCarPlate);

    }
}
