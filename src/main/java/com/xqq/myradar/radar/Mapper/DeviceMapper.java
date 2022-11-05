package com.xqq.myradar.radar.Mapper;

import com.xqq.myradar.radar.Entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DeviceMapper {

    @Select("select * from device")
    List<Device> selctAll();

    @Select("select ip from device")
    List<String> selectIp();
}
