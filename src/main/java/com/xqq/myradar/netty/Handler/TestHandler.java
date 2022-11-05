package com.xqq.myradar.netty.Handler;


import com.xqq.myradar.netty.Component.NettyClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        NettyClient.addSocketChannel(ctx.channel().id().asLongText(), (SocketChannel) ctx.channel());
        log.info("netty:新客户端连接" + ctx.channel().remoteAddress());

    }
    @Override
    public void channelInactive(final ChannelHandlerContext ctx)throws Exception{
        NettyClient.removeSocketChannel(ctx.channel().id().asLongText());
        System.out.println("断开连接" + ctx.channel().remoteAddress());
        //连接激活后，可做处理
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead");
        //根据消息类型回复报文数据
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("read complete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws
            Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
