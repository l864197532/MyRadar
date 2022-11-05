package com.xqq.myradar.netty.Handler;

import com.xqq.myradar.radar.Utils.DataUtil;
import com.xqq.myradar.redis.JedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class NettyMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        if (in.readableBytes() < 40) {
            System.out.println("该数据小于40字节");
            return;
        }
        String ip = ctx.channel().remoteAddress().toString().substring(1).split(":")[0];


        byte[] array = new byte[in.readableBytes()];

        in.readBytes(array);
        in.discardReadBytes();//通过调用ByteBuf.discardReadBytes()来回收已经读取过的字节
        //查找包头，返回包头位置
        int processLen = DataUtil.seek(array, 0);
        Map<Integer, Long> ids = new HashMap<>();   //存储当前包的所有车的时间戳和车牌号以便相同包的相同数据去重
        Map<String,Double> timeStampMap = new HashMap<>();//存储当前包的所有时间戳
        ArrayList<String> crcList = new ArrayList<>();  //crc列表
        while ((array.length - processLen) >= 41) { //最少每41个字节为一条完整数据
            int flag;
            int length = DataUtil.getLength(array, processLen);//获取object的有效长度
            String crcCode = DataUtil.getCrcCode(array, processLen, length);//获取CRC校验
            if (!crcList.contains(crcCode)) {
                crcList.add(crcCode);
                flag = DataUtil.decoding(array, processLen, ip, ids,timeStampMap);//进行crc校验
            }
            else {
                flag = -1;
            }

            if (flag == 1) {    //正常处理
                processLen += (length + 8);
            } else {            //异常处理
                processLen = DataUtil.seek(array, processLen + 1);//查找包头，返回包头位置
                if (processLen == -1) {
                    processLen = array.length;
                }
            }

        }

    }
}
