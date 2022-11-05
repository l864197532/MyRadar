package com.xqq.myradar.zmq;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.zeromq.ZMQ;

/**
 * ZMQ接收线程
 */

@Configuration
@PropertySource(value ="classpath:application.yml")
public abstract class ZmqSubThread implements Runnable {




    /**
     * ZMQ启动线程数
     */
    private int ZMQThreadCount = Integer.parseInt("1");

    /**
     * ZMQ接收端口
     */
    private int ZMQRecvPort1 =8030;


    /**
     * ZMQ监听接收端口
     */
    private String ZMQRecvIP1="100.65.23.240";



    private ZMQ.Context context = null;
    private ZMQ.Socket subSock = null;

    public ZmqSubThread() {
        initZMQ();
    }



    /**
     * 初始化ZMQ对象
     */
    public void initZMQ() {

        if (ZMQRecvIP1 == null || "".equals(ZMQRecvIP1)) {
            throw new RuntimeException("IP Error!");
        }
        if (ZMQRecvPort1 == 0 ) {
            throw new RuntimeException("Port Error!");
        }

        context = ZMQ.context(ZMQThreadCount);
        subSock = context.socket(ZMQ.SUB);
        String ConUri1 = "tcp://" + ZMQRecvIP1 + ":" + ZMQRecvPort1;
        subSock.connect(ConUri1);
        subSock.subscribe("".getBytes());
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] recvBuf = subSock.recv(ZMQ.SUB);
                if (recvBuf == null) {
                    continue;
                }
                dealWith(recvBuf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理接收到数据的抽象方法
     */
    public abstract void dealWith(byte[] data);
}


