package com.xqq.myradar.radar.Mapper;


import com.xqq.myradar.radar.Model.Trajectory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TrajMapper {

    @Insert({
            "<script>",
            "insert into ${tableName}(trajId, timeStamp, frenetx, frenety, speedx, speedy, headingAngle, longitude, latitude, mercatorx, mercatory, DHW, THW, TTC, accx, RoadDirect, CarId, lane, type,empty3) values ",
            "<foreach collection='itemLists' item='item' index='index' separator=','>",
            "(#{item.trajId}, #{item.timeStamp}, #{item.frenetx}, #{item.frenety}, #{item.speedx}, #{item.speedy}, #{item.headingAngle}, #{item.longitude},#{item.latitude},#{item.mercatorx},#{item.mercatory}, #{item.DHW}, #{item.THW},#{item.TTC}, #{item.accx}, #{item.RoadDirect}, #{item.CarId}, #{item.lane}, #{item.type}, #{item.empty1})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param(value="tableName") String tableName, @Param(value="itemLists") List<Trajectory> itemLists);

    @Insert({
            "<script>",
            "insert into ${tableName}(trajId, timeStamp, frenetx, frenety, speedx, speedy, headingAngle, longitude, latitude, mercatorx, mercatory, DHW, THW, TTC, accx, RoadDirect, CarId, lane, type) values ",
            "(#{item.trajId}, #{item.timeStamp}, #{item.frenetx}, #{item.frenety}, #{item.speedx}, #{item.speedy}, #{item.headingAngle}, #{item.longitude},#{item.latitude},#{item.mercatorx},#{item.mercatory}, #{item.DHW}, #{item.THW},#{item.TTC}, #{item.accx}, #{item.RoadDirect}, #{item.CarId}, #{item.lane}, #{item.type})",
            "</script>"
    })
    void insertSingle(@Param(value="tableName") String tableName, @Param(value="item") Trajectory trajectory);
    @Select("select * from ${tableName} where (timeStamp>=${timestart} && timeStamp<=${timeend} && CarId = ${CarId}) order by timeStamp asc")
    List<Trajectory> selectByTimeAndId(@Param(value="tableName") String tableName, @Param("CarId") String  CarId, @Param("timestart") long  timestart, @Param("timeend") long timeend);

    @Select("select * from ${tableName} where (timeStamp>=${timestart} && timeStamp<=${timeend} && trajId = ${trajId}) order by timeStamp asc")
    List<Trajectory> selectByTimeAndTrajId(@Param(value="tableName") String tableName,@Param("trajId") long  trajId, @Param("timestart") long  timestart, @Param("timeend") long timeend);
}
