package com.xqq.myradar.radar.Entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@TableName("DEVICE")
@Getter
@Setter
@ToString
public class Device {

  private String rid;
  private String name;
  private String ip;
  private Integer alpha;
  private Integer xdistance;
  private Integer ydistance;
  private String remark;
  private String image;
  private  String radarAgreement;//版本号
  private double longitude;  //经度
  private double latitude; // 纬度
  private float deflectionAngle; //方向角
  private String roadDirect;
  private double detectStart;
  private double detectEnd;
  private int state;

}
