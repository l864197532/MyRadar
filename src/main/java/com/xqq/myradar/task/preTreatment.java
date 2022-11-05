package com.xqq.myradar.task;

import com.xqq.myradar.radar.Buffer.*;
import com.xqq.myradar.radar.Config.RadarConfig;
import com.xqq.myradar.redis.JedisCompoment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class preTreatment implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("preTreatment Start");
        //1.数据库表格建立
        RadarConfig.init();


        //2.静态信息导入到buffer
        DevicesBuffer.getS().select();
        //radardetectlaneinfo_right 是光纤数据fiberx转经纬度用的，用map结构存储fiberx,每0.5米一个数据
        //radarlaneinfo_right 是雷达数据转frenetx 用的
        LaneInfoBufferRight.getS().select("radardetectlaneinfo_right","radarlaneinfo_right");
        LaneInfoBufferLeft.getS().select("radardetectlaneinfo_left","radarlaneinfo_left");
        RoadInfoBufferRight.getS().select("radardetectroadinfo_right");//右侧道路宽度信息
        RoadInfoBufferLeft.getS().select("radardetectroadinfo_left");   //左侧道路宽度信息
        LaserLaneInfoBufferLeft.getS().select("laserradardetectlaneinfo_left");
        LaserLaneInfoBufferRight.getS().select("laserradardetectlaneinfo_right");
        LaserRoadInfoBuffer.getS().select("laserradardetectroadinfo");
    }
}
