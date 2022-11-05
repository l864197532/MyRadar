package com.xqq.myradar.radar.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface TableMapper {

    @Update("create table ${tableName} as select * from data where 0 = 1")
    void createTable(@Param("tableName") String tableName);

    @Update("create table ${TrajtableName} as select * from traj where 0 = 1")
    void createTrajTable(@Param("TrajtableName") String trajTableName);
}
