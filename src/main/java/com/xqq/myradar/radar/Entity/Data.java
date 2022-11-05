package com.xqq.myradar.radar.Entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@TableName("DATA")
@Getter
@Setter
@ToString
public class Data {

  private long timestamp;
  private long id;
  private String type;
  private double length;
  private double width;
  private double posx;
  private double posy;
  private double speedx;
  private double speedy;
  private double speed;
  private double acceleration;
  private String ip;

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
  private float headingAngle;
}
