package com.xqq.myradar.radar.Mapper;



import com.xqq.myradar.radar.Entity.RoadInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoadInfoMapper {
    @Select("select * from ${tableName}")
    List<RoadInfo> selectAll(@Param(value="tableName") String tableName);

    @Select("select COUNT(*) from ${tableName}")
    int selectColumnNumber(@Param(value="tableName") String tableName);


}
