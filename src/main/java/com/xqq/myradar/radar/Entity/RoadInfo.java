package com.xqq.myradar.radar.Entity;

import lombok.Data;

@Data
public class RoadInfo {
    private  int  rid;
    private String radarId;
    private double locationNumberStart;
    private double locationNumberEnd;
    private double roadMarginStart;
    private double roadMarginEnd;
    private int laneNumber;
}
