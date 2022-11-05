package com.xqq.myradar.radar.Mapper;


import com.xqq.myradar.radar.Entity.AvgSec;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AvgSecMapper {

    @Select("select * from avgsec ")
    List<AvgSec> selectAll();

    @Insert({
            "<script>",
            "insert into ${tableName}(xsecName,xsecValue,timeStampStart,countMinute, avgQRight,avgTHWRight,avgQLeft,avgTHWLeft,avgSpeedLeft,avgSpeedRight) values",
            "<foreach collection='itemLists' item='item' index='index' separator=','>",
            "(#{item.xsecName}, #{item.xsecValue}, #{item.timeStampStart}, #{item.countMinute}, #{item.avgQRight}, #{item.avgTHWRight}, #{item.avgQLeft}, #{item.avgTHWLeft}, #{item.avgSpeedLeft}, #{item.avgSpeedRight})",
            "</foreach>",
            "</script>"
    })
    void insertBatch(@Param(value="tableName") String tableName, @Param(value="itemLists") List<AvgSec> itemLists);

    @Select("select * from ${tableName} where timeStampStart>=${timestart} && timeStampStart<=${timeend} order by timeStampStart asc")
    List<AvgSec> selectByTimeRange(@Param(value="tableName") String tableName, @Param("timestart") long  timestart, @Param("timeend") long timeend);

    @Select("select * from ${tableName}  order by timeStampStart desc limit #{secSize}")
    List<AvgSec> selectLastedSecData(@Param(value="tableName") String tableName, @Param("secSize") int  secSize);
}
