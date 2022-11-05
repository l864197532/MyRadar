package com.xqq.myradar.radar.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@TableName("avgsec")
@Data
public class AvgSec {
    private String xsecName;
    private  double xsecValue;
    private long timeStampStart;
    private int countMinute;
    private float avgQRight;
    private float avgTHWRight;
    private float avgQLeft;
    private float avgTHWLeft;
    private float avgSpeedLeft;
    private float avgSpeedRight;

    public AvgSec(String xsecName, double xsecValue) {
        this.xsecName = xsecName;
        this.xsecValue = xsecValue;
    }

    public AvgSec() {
    }
}
