package com.xqq.myradar.radar.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("licenseloc")
@Data
public class Licenseloc {
    private int lid;
    private String deviceCode;
    private String installPlace;
    private String roadDirect;
    private double Start;
    private double End;
    private int laneNumber;
    private int state;
}
