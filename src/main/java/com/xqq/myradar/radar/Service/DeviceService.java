package com.xqq.myradar.radar.Service;

import com.xqq.myradar.radar.Entity.Device;
import com.xqq.myradar.radar.Mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    @Autowired
    DeviceMapper deviceMapper;

    public List<Device> selectAll(){
        return deviceMapper.selctAll();
    }
}
