package com.xqq.myradar.radar.Dao;


import com.xqq.myradar.radar.Entity.Data;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//@TableSplitTarget(rules = @TableSplitRule(tableName="data"))
@Mapper
public interface DataDao {


    @Select("select * from ${tableName} order by timestamp desc")
    List<Data> selectAll(@Param(value="tableName") String tableName);

    @Select("select * from DATAVIEW where timestamp between ${startTime} and ${endTime}")
    List<Data> selectByTimeOnly(Long startTime, Long endTime );  //所有雷达都查询， 仅仅以时间为条件


    @Select("select * from ${tableName} where ip = ${ip} and timestamp between ${start} and ${end}")
    List<Data> selectByTimeAndradarName(@Param("tableName") String tableName,@Param("start")  long start,@Param("end")  long end ,@Param("ip") String ip);


    @Insert({
            "<script>",
            "insert into ${tableName}(timestamp, id, type, length, width, posx, posy, longitude, latitude, headingAngle, mercatorx, mercatory, frenetx, frenety, speedx, speedy, speed, acceleration, ip) values ",
            "<foreach collection='itemLists' item='item' index='index' separator=','>",
            "(#{item.timestamp}, #{item.id}, #{item.type}, #{item.length}, #{item.width}, #{item.posx}, #{item.posy}, #{item.longitude}, #{item.latitude}, #{item.headingAngle}, #{item.mercatorx}, #{item.mercatory}, #{item.frenetx}, #{item.frenety}, #{item.speedx}, #{item.speedy}, #{item.speed}, #{item.acceleration}, #{item.ip})",
            "</foreach>",
            "</script>"
    })
    void insertBatchData(@Param(value="tableName") String tableName, @Param(value="itemLists") List<Data> itemLists);

}
