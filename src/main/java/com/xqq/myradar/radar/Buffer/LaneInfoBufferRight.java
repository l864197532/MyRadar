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
public class LaneInfoBufferRight {
    @Autowired
    private LaneInfoMapper laneInfoMapper;

    public Map<String,List<LaneInfo>> laneInfomap=new HashMap<>();
    public List<LaneInfo> laneInfoList=new LinkedList<>();
    public Map<Double,LaneInfo> fiberLaneInfoMap= new HashMap<>();
    private  static LaneInfoBufferRight laneInfoBuffer=new LaneInfoBufferRight();
    public LaneInfoBufferRight(){};

    public static LaneInfoBufferRight getS(){
        return laneInfoBuffer;
    }

    public  void select(String fiberName,String radarName){
        laneInfoList = this.laneInfoMapper.selectAll(fiberName);
        List<LaneInfo> radarlaneinfo_right = this.laneInfoMapper.selectAll(radarName);//这里把最新的微波雷达静态数据表写死了
        for (int i = 0; i < radarlaneinfo_right.size(); i++) {
            //如果它不在map中就新增
            if (!radarlaneinfo_right.get(i).getRadarId().equals("0")) {
                if (!laneInfomap.containsKey(radarlaneinfo_right.get(i).getRadarId())) {
                    List<LaneInfo> laneList = new LinkedList<>();
                    laneList.add(radarlaneinfo_right.get(i));
                    laneInfomap.put(radarlaneinfo_right.get(i).getRadarId(), laneList);
                }
                //如果它在map中，就在它后面加上
                else {
                    List<LaneInfo> laneListTEMP = laneInfomap.get(radarlaneinfo_right.get(i).getRadarId());
                    laneListTEMP.add(radarlaneinfo_right.get(i));
                }
            }
        }
        for (int i = 0; i < laneInfoList.size(); i++) {
            fiberLaneInfoMap.put(laneInfoList.get(i).getLocationNumberStart(),laneInfoList.get(i));
        }

    }


    @PostConstruct
    public void init() {
        laneInfoBuffer = this;
        laneInfoBuffer.laneInfoMapper= this.laneInfoMapper;
        // 初使化时将已静态化的testService实例化
    }
}
