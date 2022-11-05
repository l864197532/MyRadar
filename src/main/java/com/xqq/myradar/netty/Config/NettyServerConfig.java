package com.xqq.myradar.netty.Config;

import com.xqq.myradar.netty.Component.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyServerConfig {
    private static NettyServerConfig nettyServerConfig = null;
    private NettyServer nettyServer;

    private NettyServerConfig(){
        nettyServer = new NettyServer();
    };

    public static NettyServerConfig getNettyServerConfig(){
        if(nettyServerConfig==null){
            nettyServerConfig=new NettyServerConfig();
        }
        return nettyServerConfig;
    }

    public void StartNettyServer(int port){
        log.info("netty 准备启动 端口:"+port);
        nettyServer.start(port);
    }

}
