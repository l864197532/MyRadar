package com.xqq.myradar.radar.Mapper;



import com.xqq.myradar.radar.Entity.SecInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SecInfoMapper {
    @Insert({
            "insert into ${tableName}(xsecName,xsecValue) values (#{item.xsecName}, #{item.xsecValue})"
    })
    void insertItem(@Param(value="tableName") String tableName, @Param(value="item") SecInfo item);

    @Select("select * from secinfo  where xsecValue=#{xsecValue}")
    SecInfo selectByXsecValue(@Param("xsecValue") double xsecValue);

    @Select("select * from secinfo ")
    List<SecInfo> selectAll();

    @Update("update ${tableName} set xsecName = #{item.xsecName} where xsecValue = #{item.xsecValue}")
    void updateSecInfo(@Param(value="tableName") String tableName, @Param(value="item") SecInfo item);

    @Update("delete from ${tableName}  where xsecValue = #{xsecValue}")
    void deleteSecInfo(@Param(value="tableName") String tableName, @Param(value="xsecValue") double xsecValue);
}
