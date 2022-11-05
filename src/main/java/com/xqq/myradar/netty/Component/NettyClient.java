package com.xqq.myradar.netty.Component;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NettyClient {
    //tcp连接集合
    private static Map<String, SocketChannel> socketGroup = new ConcurrentHashMap<>();

    public static void removeSocketChannel(String id) {
        socketGroup.remove(id);
    }

    public static void addSocketChannel(String id, SocketChannel gateway_channel) {
        socketGroup.put(id, gateway_channel);
    }
}