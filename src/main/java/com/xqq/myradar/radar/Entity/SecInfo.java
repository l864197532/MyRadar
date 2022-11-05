package com.xqq.myradar.radar.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("secinfo")
@Data
public class SecInfo {
    private String xsecName;
    private  double xsecValue;
}
