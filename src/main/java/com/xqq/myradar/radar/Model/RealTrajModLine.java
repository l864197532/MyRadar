package com.xqq.myradar.radar.Model;

import lombok.Data;

@Data
public class RealTrajModLine {
    private long trajId;//程序轨迹Id
    private long fiberId; //光纤Id
    private long saveTime; //记录上一帧的真实时间，以便销毁
    private String picLicense; //牌照数据
    private int licenseColor;//牌照颜色
    private long liceTime;//牌照更新时间
    private int roadDirect; //轨迹方向
    private double frenetX; //轨迹最新位置
    private double frenetY; //轨迹最新位置
    private float speedx;
}
