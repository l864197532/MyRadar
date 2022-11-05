package com.xqq.myradar.radar.Buffer;


import com.xqq.myradar.radar.Entity.LaneInfo;
import com.xqq.myradar.radar.Mapper.LaneInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class LaserLaneInfoBufferLeft {
    @Autowired
    private LaneInfoMapper laneInfoMapper;

    public Map<String,List<LaneInfo>> laneInfomap=new HashMap<>();
    public List<LaneInfo> laneInfoList=new LinkedList<>();

    private  static LaserLaneInfoBufferLeft laneInfoBuffer=new LaserLaneInfoBufferLeft();
    public LaserLaneInfoBufferLeft(){};

    public static LaserLaneInfoBufferLeft getS(){
        return laneInfoBuffer;
    }

    public  void select(String tableName){
        laneInfoList = this.laneInfoMapper.selectAll(tableName);

        for (int i = 0; i < laneInfoList.size(); i++) {
            //如果它不在map中就新增
            if(!laneInfomap.containsKey(laneInfoList.get(i).getRadarId())){
                List<LaneInfo> laneList=new LinkedList<>();
                laneList.add(laneInfoList.get(i));
                laneInfomap.put(laneInfoList.get(i).getRadarId(),laneList);
            }
            //如果它在map中，就在它后面加上
            else {
                List<LaneInfo> laneListTEMP = laneInfomap.get(laneInfoList.get(i).getRadarId());
                laneListTEMP.add(laneInfoList.get(i));
            }
        }
    }


    @PostConstruct
    public void init() {
        laneInfoBuffer = this;
        laneInfoBuffer.laneInfoMapper= this.laneInfoMapper;
        // 初使化时将已静态化的testService实例化
    }
}
