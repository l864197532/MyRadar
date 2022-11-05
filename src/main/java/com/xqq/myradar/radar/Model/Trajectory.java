package com.xqq.myradar.radar.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Trajectory {
    private long trajId;//1
    private long timeStamp;//1
    private double frenetx;//1
    private double frenety;//1
    private float  speedx ;//1
    private float  speedy ;//1
    private float headingAngle;//1
    private double longitude;//1
    private double latitude;//1
    private double mercatorx;//1
    private double mercatory;//1
    private float  DHW    ;//无
    private long ts;
    private float  THW    ;//无
    private float  TTC    ;//无
    private float  accx   ;//无
    private int  RoadDirect   ;//1
    private String  CarId ;//1
    private int licenseColor;//无
    private int lane;//1
    private int type;//1
    private int empty1;//无
    private int empty2;//无
    private int empty3;//无
}
