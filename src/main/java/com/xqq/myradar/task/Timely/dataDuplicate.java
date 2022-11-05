package com.xqq.myradar.task.Timely;

import com.alibaba.fastjson.JSONObject;
import com.xqq.myradar.radar.Model.VehicleModel;
import com.xqq.myradar.redis.JedisUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class dataDuplicate {
    /**
     * 数据去重
     */

    static  long flagTimeStamp = 0; //
    static  long deduplicationTime = 0;// 用于记录数据去重上一时刻时间
//    @Scheduled(initialDelay = 6000,fixedRate = 200)//系统启动后延迟六秒执行，每200ms执行一次
//    @Async
    public void dataDedupli(){
        int delayTime = 5*1000; // 延迟的系统时间，用作轨迹融合时间
        long timeStamp = System.currentTimeMillis() - delayTime ;
        long remainder = timeStamp%1000;
        remainder = (long) Math.floor(remainder/200);
        timeStamp = (timeStamp/1000)*1000 + remainder*200;
        int index = 0;
        if (deduplicationTime != 0) {
            index = (int) ((timeStamp - deduplicationTime)/200);
        }else {
            index = 1;
            deduplicationTime = timeStamp - 200;
        }
        for (int i = 1; i <= index; i++) {
            timeStamp = deduplicationTime + 200 * i;
            List<VehicleModel> fiberData = new LinkedList<>();
            List<VehicleModel> microwaveData = new LinkedList<>();
            List<VehicleModel> lidarData = new LinkedList<>();

            JedisUtil.getFiberData(timeStamp,fiberData);
            JedisUtil.getMicrowaveData(timeStamp,microwaveData);
            JedisUtil.getLidarData(timeStamp,lidarData);

            List<VehicleModel> targetData = dataDeduplication(fiberData, microwaveData, lidarData);
            if (targetData.size() > 0){
                Map<String, String> targetDataMap = new HashMap<>();
                for (VehicleModel targetDatum : targetData) {
                    String vmJson = JSONObject.toJSONString(targetDatum);
                    targetDataMap.put(String.valueOf(targetDatum.getId()), vmJson);
                }

                //当使用数据时间时存储去重数据的时间戳
                JedisUtil.savaDedupTime(timeStamp);
                JedisUtil.saveDeduplicationData(targetDataMap,timeStamp);

            }

        }
        deduplicationTime = timeStamp;
    }



    /**
     *
     * @param fiberData 光纤数据
     * @param radarData 微波雷达数据
     * @param lidarData 激光雷达数据
     * @return
     */
    public static List<VehicleModel> dataDeduplication(List<VehicleModel> fiberData,List<VehicleModel> radarData,List<VehicleModel> lidarData) {
        //三次去重；微波雷达内部去重、微波和光纤去重、激光和光纤去重；

        int mcwX = 5;//微波frenetx允许误差
        int mcwY = 3;//微波frenety允许误差
        int mcwSpeed = 10;//微波speed允许误差
        int FibMcwX = 10; //光纤·微波有frenetx允许误差
        int FibMcwY = 3;  //光纤·微波有frenety允许误差
        radarData.sort(Comparator.comparing(VehicleModel::getFrenetx)); //根据frenet排序由小到大排序

        if (radarData.size() >= 2){   //当微波雷达一帧数据至少为2时才去重

            Iterator<VehicleModel> radarIterator = radarData.iterator();//同时间戳，位置相近的雷达数据去重
            VehicleModel radarDatumFront = radarIterator.next();
            while (radarIterator.hasNext()){
                VehicleModel radarDatumEnd = radarIterator.next();
                //主要集中在雷达设备的去重。  //frenetx误差允许5；frenety允许范围3m;车速允许10m/s
                if (radarDatumEnd.getFrenetx()-radarDatumFront.getFrenetx() <= mcwX
                        &&radarDatumEnd.getFrenety()-radarDatumFront.getFrenety() <= mcwY
                        &&radarDatumEnd.getSpeedx()-radarDatumFront.getSpeedx() <= mcwSpeed){
                    radarIterator.remove();
                }

            }

        }



        List<VehicleModel> targetData = new ArrayList<>();
        Set<Integer> fiberState = new HashSet<>();
        for (VehicleModel radarDatum : radarData) {
            int matchflag = 0;
            for (int i = 0;i < fiberData.size();i++){
                /**
                 * 车道不考虑
                 */
                if (            //radarDatum.getLane() == fiberData.get(i).getLane()     满足frentx,lane,roadDirect,state条件
                        Math.abs(radarDatum.getFrenety() - fiberData.get(i).getFrenety()) < FibMcwY
                                && Math.abs(radarDatum.getFrenetx()-fiberData.get(i).getFrenetx()) < FibMcwX
                                && radarDatum.getRoadDirect() == fiberData.get(i).getRoadDirect()
                                && (!fiberState.contains(i))){
                    matchflag = 1;
                    fiberState.add(i); //将状态位置为1，表示该光纤数据已经进行去重
                    fiberData.get(i).setSpeedx(radarDatum.getSpeedx());
                    fiberData.get(i).setSpeedy(radarDatum.getSpeedy());
                    fiberData.get(i).setSpeed(radarDatum.getSpeed());
                    fiberData.get(i).setLongitude(radarDatum.getLongitude());
                    fiberData.get(i).setLatitude(radarDatum.getLatitude());
                    fiberData.get(i).setMercatorx(radarDatum.getMercatorx());
                    fiberData.get(i).setMercatory(radarDatum.getMercatory());
                    fiberData.get(i).setFrenetx(radarDatum.getFrenetx());
                    fiberData.get(i).setFrenety(radarDatum.getFrenety());
                    fiberData.get(i).setHeadingAngle(radarDatum.getHeadingAngle());
                    fiberData.get(i).setFrenetAngle(radarDatum.getFrenetAngle());
                    break;
                }
            }
            if (matchflag == 0) {//激光雷达未匹配成功数据保留
                targetData.add(radarDatum);
            }

        }
        //对激光雷达和光纤的数据去重
        Set<Integer> lidarFiberState = new HashSet<>();
        for (VehicleModel lisarDatum : lidarData) {
            int lidarmatchflag = 0;
            for (int i = 0; i < fiberData.size(); i++) {
                /**
                 * 车道不考虑
                 */
                if (lisarDatum.getLane() == fiberData.get(i).getLane()     //满足frentx,lane,roadDirect,state条件
                        && Math.abs(lisarDatum.getFrenetx() - fiberData.get(i).getFrenetx()) < FibMcwX
                        && lisarDatum.getRoadDirect() == fiberData.get(i).getRoadDirect()
                        && (!lidarFiberState.contains(i))) {
                    lidarmatchflag = 1;
                    lidarFiberState.add(i); //将状态位置为1，表示该光纤数据已经进行去重
                    fiberData.get(i).setSpeedx(lisarDatum.getSpeedx());
                    fiberData.get(i).setSpeedy(lisarDatum.getSpeedy());
                    fiberData.get(i).setSpeed(lisarDatum.getSpeed());
                    fiberData.get(i).setLongitude(lisarDatum.getLongitude());
                    fiberData.get(i).setLatitude(lisarDatum.getLatitude());
                    fiberData.get(i).setMercatorx(lisarDatum.getMercatorx());
                    fiberData.get(i).setMercatory(lisarDatum.getMercatory());
                    fiberData.get(i).setFrenetx(lisarDatum.getFrenetx());
                    fiberData.get(i).setFrenety(lisarDatum.getFrenety());
                    fiberData.get(i).setHeadingAngle(lisarDatum.getHeadingAngle());
                    fiberData.get(i).setFrenetAngle(lisarDatum.getFrenetAngle());
                    break;
                }
            }
            if (lidarmatchflag == 0) {//激光雷达未匹配成功数据保留
                targetData.add(lisarDatum);
            }

        }

        targetData.addAll(fiberData); //将光纤数据加入到输出队列中
        return targetData;
    }
}
