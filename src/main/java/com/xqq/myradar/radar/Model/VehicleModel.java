package com.xqq.myradar.radar.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleModel {

    //原始数据
    private String code;
    //雷达ip
    private String ip;

    //时间戳
    private long timestamp;
    //目标id-重要
    private int id;         //雷达或者光纤给车分配的编号
    //目标类型-重要
    private String type;    //unsigned char
    //目标长度-重要
    private float length;   //unsigned char
    //目标宽度-重要
    private float width;    //unsigned char
    //x坐标
    private float posx;     //unsigned short
    //y坐标
    private float posy;     //unsigned short
    //x轴速度
    private float speedx;   //short
    //y轴速度
    private float speedy;   //short
    //车速
    private float speed;    //unsigned
    //目标方向加速度
    private float acceleration; //short
    //目标经度
    private double longitude; //short
    //目标维度
    private double latitude; //short
    //目标平面坐标x
    private double mercatorx; //short
    //目标平面坐标y
    private double mercatory; //short
    //目标frenet坐标x
    private double frenetx; //short
    //目标frenet坐标y
    private double frenety; //short
    //目标航向角
    private float headingAngle; //unsigned

    private int FiberX;

    private int lane;

    private float FrenetAngle;

    private int RoadDirect;

    private String carId; //车牌照信息

    private Integer liscenseColor; //车牌颜色


    public E1FrameParticipant vehicleModel2ElFrameParticipant(){
        E1FrameParticipant elFrameParticipant = new E1FrameParticipant();

        elFrameParticipant.setDirection(this.getRoadDirect());
        elFrameParticipant.setLongitude(this.longitude);
        elFrameParticipant.setLatitude(this.latitude);
        elFrameParticipant.setLaneNum(this.lane);
        elFrameParticipant.setCourseAngle(this.headingAngle);
        elFrameParticipant.setPicLicense(this.type);
        elFrameParticipant.setSpeed(this.speed);
        elFrameParticipant.setTopic("EZ030005MatchResultMiniData");
        int rabdomNum=(int)(Math.random()*900)+100;
        String recordSerialNumber = "030005"+System.currentTimeMillis()+rabdomNum;
        elFrameParticipant.setRecordSerialNumber(recordSerialNumber);
        elFrameParticipant.setId(this.id);
        return elFrameParticipant;



    }
}