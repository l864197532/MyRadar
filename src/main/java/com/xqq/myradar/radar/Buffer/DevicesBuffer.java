package com.xqq.myradar.radar.Buffer;

import com.xqq.myradar.radar.Entity.Device;
import com.xqq.myradar.radar.Service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 用于存储数据库中device静态信息的缓冲区
 */
@Component
public class DevicesBuffer {
    @Autowired
    DeviceService deviceService;

    public  List<Device> deviceList = new LinkedList<>(); //将设备数据固化在内存
    private static DevicesBuffer devicesBuffer= new DevicesBuffer();

    private DevicesBuffer(){

    }
    public static DevicesBuffer getS(){//为使外部能访问 故此用static
        return devicesBuffer;
    }
    public void select(){
        deviceList = this.deviceService.selectAll();
    }
    public Device sellectByip(String ip){
        for (int i = 0; i < deviceList.size(); i++) {
            if(deviceList.get(i).getIp().equals(ip)){
                return deviceList.get(i);
            }
        }
        return null;
    }
    @PostConstruct
    public void init() {
        devicesBuffer = this;
        devicesBuffer.deviceService = this.deviceService;

    }

}
