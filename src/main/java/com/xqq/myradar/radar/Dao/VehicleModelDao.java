package com.xqq.myradar.radar.Dao;

import com.xqq.myradar.radar.Model.VehicleModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface VehicleModelDao {

    @Insert({
            "<script>",
            "insert into ${tableName}(timestamp, id, type, length, width, posx, posy, longitude, latitude, headingAngle, mercatorx, mercatory, frenetx, frenety, speedx, speedy, speed, acceleration, ip,fiberX, lane, FrenetAngle, RoadDirect, code) values ",
            "<foreach collection='itemLists' item='item' index='index' separator=','>",
            "(#{item.timeStamp}, #{item.id}, #{item.type}, #{item.length}, #{item.width}, #{item.posx}, #{item.posy}, #{item.longitude}, #{item.latitude}, #{item.headingAngle}, #{item.mercatorx}, #{item.mercatory}, #{item.frenetx}, #{item.frenety}, #{item.speedx}, #{item.speedy}, #{item.speed}, #{item.acceleration}, #{item.ip},#{item.FiberX}, #{item.lane},#{item.FrenetAngle},#{item.RoadDirect}, #{item.code})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param(value="tableName") String tableName, @Param(value="itemLists") List<VehicleModel> itemLists);

}
