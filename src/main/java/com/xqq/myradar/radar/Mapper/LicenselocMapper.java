package com.xqq.myradar.radar.Mapper;


import com.xqq.myradar.radar.Entity.Licenseloc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LicenselocMapper {

    @Select("select * from licenseloc")
    List<Licenseloc> selectAll();

    @Select("select * from licenseloc where  deviceCode = ${deviceCode}")
    Licenseloc selectByDeviceCode(@Param("deviceCode") String deviceCode);

}
