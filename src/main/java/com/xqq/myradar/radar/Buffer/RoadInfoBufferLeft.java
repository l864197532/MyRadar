package com.xqq.myradar.radar.Buffer;

import com.xqq.myradar.radar.Entity.RoadInfo;
import com.xqq.myradar.radar.Mapper.RoadInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Component
public class RoadInfoBufferLeft {
    @Autowired
    private RoadInfoMapper roadInfoMapper;

    public List<RoadInfo> roadInfoList =new LinkedList<>();

    private  static RoadInfoBufferLeft roadInfoBuffer=new RoadInfoBufferLeft();
    public RoadInfoBufferLeft(){};

    public static RoadInfoBufferLeft getS(){
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

}
