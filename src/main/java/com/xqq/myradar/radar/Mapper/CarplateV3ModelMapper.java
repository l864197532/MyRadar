package com.xqq.myradar.radar.Mapper;

import com.xqq.myradar.radar.Model.CarplateV3Model;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarplateV3ModelMapper {

    @Insert({
            "<script>",
            "insert into ${tableName}(ip, picId, gantryId, cameraNum, picTime, globalTimeStamp, laneNum, vehicleId, picLicense, vehSpeed, licenseColor，saveTime) values ",
            "<foreach collection='itemLists' item='item' index='index' separator=','>",
            "(#{item.ip}, #{item.picId}, #{item.gantryId}, #{item.cameraNum}, #{item.picTime}, #{item.globalTimeStamp}, #{item.laneNum}, #{item.vehicleId},#{item.picLicense},#{item.vehSpeed},#{item.licenseColor}, #{item.saveTime})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param(value="tableName") String tableName, @Param(value="itemLists") List<CarplateV3Model> itemLists);

    @Insert({
            "<script>",
            "insert into ${tableName}(ip, picId, gantryId, cameraNum, picTime, globalTimeStamp, laneNum, vehicleId, picLicense, vehSpeed, licenseColor,saveTime) values ",
            "(#{item.ip}, #{item.picId}, #{item.gantryId}, #{item.cameraNum}, #{item.picTime}, #{item.globalTimeStamp}, #{item.laneNum}, #{item.vehicleId}, #{item.picLicense}, #{item.vehSpeed}, #{item.licenseColor}, #{item.saveTime})",
            "</script>"
    })
    void insertSingle(@Param(value="tableName") String tableName, @Param(value="item") CarplateV3Model carplateV3Model);

}
