package com.xqq.myradar;

import com.xqq.myradar.netty.Config.NettyServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class MyRadarApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyRadarApplication.class, args);
        //netty启动
        NettyServerConfig.getNettyServerConfig().StartNettyServer(8090);
    }

}
