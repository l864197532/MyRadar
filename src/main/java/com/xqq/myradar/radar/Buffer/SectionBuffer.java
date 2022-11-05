package com.xqq.myradar.radar.Buffer;


import com.xqq.myradar.radar.Entity.AvgSec;
import com.xqq.myradar.radar.Entity.SecInfo;
import com.xqq.myradar.radar.Mapper.AvgSecMapper;
import com.xqq.myradar.radar.Mapper.SecInfoMapper;
import com.xqq.myradar.radar.Model.Trajectory;
import com.xqq.myradar.radar.Utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class SectionBuffer {
    //断面静态缓冲区
    @Autowired
    SecInfoMapper secInfoMapper;
    @Autowired
    AvgSecMapper avgSecMapper;


    private static SectionBuffer sectionBuffer= new SectionBuffer();
    public List<AvgSec> sectionModel = new ArrayList<>(); //存储静态区间信息
    public List<AvgSec> sectionList = new ArrayList<>(); //存储帧的统计数据
    public Map<String,Map<Long,Float>> sectionStatic_right = new HashMap();
    public Map<String,Map<Long,Float>> sectionStatic_left = new HashMap();
    public long CurrentCalcuTime = 0;
    private int frameNum = 5*60*5; //五分钟的帧数

    private SectionBuffer(){

    }
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        sectionBuffer = this;
        sectionBuffer.secInfoMapper = this.secInfoMapper;
        // 初使化时将已静态化的testService实例化
    }
    public static SectionBuffer getS(){//为使外部能访问 故此用static
        return sectionBuffer;
    }

    public void selctData(){
        List<SecInfo> secInfosList = secInfoMapper.selectAll();
        Iterator<SecInfo> secInfoIterator = secInfosList.iterator();
        while (secInfoIterator.hasNext()){
            SecInfo secInfo = secInfoIterator.next();
            sectionModel.add(new AvgSec(secInfo.getXsecName(),secInfo.getXsecValue()));
        }
    }

    /**
     * 存储一段时间的断面交通流参数的缓冲区
     * @param timeStamp 开始计算参数的时间
     */
    public void sectionListModel(long timeStamp){
        for (AvgSec avgSec : sectionModel) {
            AvgSec newAvgSec = new AvgSec();
            newAvgSec.setXsecName(avgSec.getXsecName());
            newAvgSec.setXsecValue(avgSec.getXsecValue());
            newAvgSec.setCountMinute(frameNum/300);
            newAvgSec.setTimeStampStart(timeStamp);
            sectionList.add(newAvgSec);
        }
    }

    public void calculateDataSection(Trajectory trajectory){

        int roadDirectRight = 1;//断面的右幅
        int roadDirectLeft = 2;//断面左幅
        for (AvgSec avgSec : sectionList) {
            if (trajectory.getFrenetx() >= avgSec.getXsecValue()-10 &&
                    trajectory.getFrenetx() <= avgSec.getXsecValue()+10&&
                    trajectory.getRoadDirect() == roadDirectRight){
                int getNumRight = (int) (avgSec.getAvgQRight()+1);
                avgSec.setAvgQRight(getNumRight);
                avgSec.setAvgSpeedRight(avgSec.getAvgSpeedRight()+Math.abs(trajectory.getSpeedx()));
            }
            if (trajectory.getFrenetx() >= avgSec.getXsecValue()-10 &&
                    trajectory.getFrenetx() <= avgSec.getXsecValue()+10&&
                    trajectory.getRoadDirect() == roadDirectLeft){
                int getNumLeft = (int) (avgSec.getAvgQLeft()+1);
                avgSec.setAvgQLeft(getNumLeft);
                avgSec.setAvgSpeedLeft(avgSec.getAvgSpeedLeft()+Math.abs(trajectory.getSpeedx()));
            }
        }

    }
    public void sectionStatic(Trajectory trajectory){
        int roadDirectRight = 1;//断面的右幅
        int roadDirectLeft = 2;//断面左幅
        for (AvgSec avgSec : sectionList) {
            if (trajectory.getFrenetx() >= avgSec.getXsecValue()-10 &&
                    trajectory.getFrenetx() <= avgSec.getXsecValue()+10&&
                    trajectory.getRoadDirect() == roadDirectRight){//在该断面位置

                if (!sectionStatic_right.containsKey(avgSec.getXsecName())){
                    Map<Long,Float> nowSectionMap = new HashMap<>();
                    nowSectionMap.put(trajectory.getTrajId(), trajectory.getSpeedx());
                    sectionStatic_right.put(avgSec.getXsecName(),nowSectionMap);
                }else {
                    sectionStatic_right.get(avgSec.getXsecName()).put(trajectory.getTrajId(), trajectory.getSpeedx());
                }

            }
            if (trajectory.getFrenetx() >= avgSec.getXsecValue()-10 &&
                    trajectory.getFrenetx() <= avgSec.getXsecValue()+10&&
                    trajectory.getRoadDirect() == roadDirectLeft){
                if (!sectionStatic_left.containsKey(avgSec.getXsecName())){
                    Map<Long,Float> nowSectionMap = new HashMap<>();
                    nowSectionMap.put(trajectory.getTrajId(), trajectory.getSpeedx());
                    sectionStatic_left.put(avgSec.getXsecName(),nowSectionMap);
                }else {
                    sectionStatic_left.get(avgSec.getXsecName()).put(trajectory.getTrajId(), trajectory.getSpeedx());
                }
            }
        }
    }
    public void sectionData2Database(long timeStamp){
        int calculateNum = 60*300/frameNum;
        for (AvgSec avgSec : sectionList) {
            //统计断面右幅的流量和速度
            if (sectionStatic_right.containsKey(avgSec.getXsecName())){
                int vehicleNum = 0;
                float vehicle_speed = 0;
                for (Map.Entry<Long, Float> entry : sectionStatic_right.get(avgSec.getXsecName()).entrySet()) {
                    vehicleNum++;//五分钟内有几辆车呢
                    vehicle_speed += Math.abs(entry.getValue());
                }
                avgSec.setAvgSpeedRight(vehicle_speed/vehicleNum);
                avgSec.setAvgQRight(vehicleNum*calculateNum);
            }else {
                avgSec.setAvgSpeedRight(9999);
                avgSec.setAvgQRight(0);
            }
            //统计断面左幅的流量和速度
            if (sectionStatic_left.containsKey(avgSec.getXsecName())){
                int vehicleNum = 0;
                float vehicle_speed = 0;
                for (Map.Entry<Long, Float> entry : sectionStatic_left.get(avgSec.getXsecName()).entrySet()) {
                    vehicleNum++;
                    vehicle_speed += Math.abs(entry.getValue());
                }
                avgSec.setAvgSpeedLeft(vehicle_speed/vehicleNum);
                avgSec.setAvgQLeft(vehicleNum*calculateNum);
            }else {
                avgSec.setAvgSpeedLeft(9999);
                avgSec.setAvgQLeft(0);
            }
        }
        if (sectionList.size() > 0){
            avgSecMapper.insertBatch(TableUtils.getTodaySectionName(),sectionList);
        }

        CurrentCalcuTime = timeStamp;
        sectionList.clear();
        sectionListModel(timeStamp);//重置下一轮计算的初始值
        sectionStatic_left.clear();
        sectionStatic_right.clear();
    }


}
