package com.xqq.myradar.radar.Buffer;


import com.xqq.myradar.radar.Entity.Licenseloc;
import com.xqq.myradar.radar.Mapper.LicenselocMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Component
public class LicenselocBuffer {

    @Autowired
    private LicenselocMapper licenselocMapper;

    public List<Licenseloc> LicenseloctList=new LinkedList<>(); //将设备数据固化在内存
    private static LicenselocBuffer licenselocBuffer= new LicenselocBuffer();
    public LicenselocBuffer(){ }

    public static LicenselocBuffer getS(){//为使外部能访问 故此用static
        return licenselocBuffer;
    }

    public void select(){
        LicenseloctList= this.licenselocMapper.selectAll();
    }

    public Licenseloc sellectBydeviceCode(String deviceCode){
        for (int i = 0; i < LicenseloctList.size(); i++) {
            if(LicenseloctList.get(i).getDeviceCode().equals(deviceCode)){
                return LicenseloctList.get(i);
            }
        }
        return null;
    }

    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        licenselocBuffer = this;
        licenselocBuffer.licenselocMapper= this.licenselocMapper;
        // 初使化时将已静态化的testService实例化
    }


}