package com.xqq.myradar.radar.Dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xqq.myradar.radar.Entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DeviceDao extends BaseMapper<Device> {

    @Update("update device set image = '${image}'  where name = '${name}'")
    void uploadImg(@Param("image") String image, @Param("name") String name);

    @Update("update device set alpha = ${alpha}, xdistance = ${xdistance}, ydistance = ${ydistance}, remark = '${remark}', longitude = ${longitude}, latitude = ${latitude},deflectionAngle=${deflectionAngle},radarAgreement = '${radarAgreement}' where name = '${name}'")
    void updateArg(@Param("alpha") int alpha, @Param("xdistance") int xdistance, @Param("ydistance")int ydistance,
                   @Param("remark") String remark, @Param("name") String name, @Param("longitude") float longitude,
                   @Param("latitude") float latitude, @Param("deflectionAngle") float deflectionAngle,@Param("radarAgreement") String agreement);

    @Select("select * from device")
    List<Device> selctAll();


    @Select("select ip from device")
    List<String> selectIp();




}
