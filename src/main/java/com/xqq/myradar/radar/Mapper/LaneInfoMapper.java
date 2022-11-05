package com.xqq.myradar.radar.Mapper;


import com.xqq.myradar.radar.Entity.LaneInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LaneInfoMapper {
    @Select("select * from ${tableName}")
    List<LaneInfo> selectAll(@Param(value="tableName") String tableName);


}
