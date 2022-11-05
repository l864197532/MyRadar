package com.xqq.myradar.radar.Buffer;


import com.xqq.myradar.radar.Entity.RoadInfo;
import com.xqq.myradar.radar.Mapper.RoadInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Component
public class LaserRoadInfoBuffer {
    @Autowired
    private RoadInfoMapper roadInfoMapper;

    public List<RoadInfo> roadInfoList =new LinkedList<>();

    private  static LaserRoadInfoBuffer roadInfoBuffer=new LaserRoadInfoBuffer();
    public LaserRoadInfoBuffer(){};

    public static LaserRoadInfoBuffer getS(){
        return roadInfoBuffer;
    }

    public  void select(String tableName){
        roadInfoList=this.roadInfoMapper.selectAll(tableName);
    }

    @PostConstruct
    public void init() {
        roadInfoBuffer = this;
        roadInfoBuffer.roadInfoMapper= this.roadInfoMapper;
        // 初使化时将已静态化的testService实例化
    }

    public void print(){
        for (int i = 0; i < roadInfoList.size(); i++) {
            System.out.println(roadInfoList.get(i).toString());
        }
    }
    public List<RoadInfo> sellectByRadarID(String radarId){
        List<RoadInfo> list=new LinkedList<>();
        for (int i = 0; i < roadInfoList.size(); i++) {
            if (roadInfoList.get(i).getRadarId().equals(radarId)){
                list.add(roadInfoList.get(i));
            }
        }
        return list;
    }
}
