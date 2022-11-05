package com.xqq.myradar.netty.Handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        log.info("初始化channel");
//        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));//启动日志
        ch.pipeline().addLast(new IdleStateHandler(60, 0, 0));//心跳监测
        ch.pipeline().addLast(new NettyHeartKeeper());//心跳监测处理
        ch.pipeline().addLast(new NettyMessageDecoder());//消息处理
        ch.pipeline().addLast(new TestHandler());
    }
}
