package com.xqq.myradar.netty.Component;

import com.xqq.myradar.netty.Handler.NettyServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyServer {

    public void start(int port){
        log.info("NettyServer.start()");

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,worker).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 300)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new NettyServerInitializer());

        ChannelFuture future;
        try {
            future = serverBootstrap.bind(port).sync();
            log.info(NettyServer.class + " 启动正在监听： " + future.channel().localAddress());
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅地退出，释放线程池资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
