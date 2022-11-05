package com.xqq.myradar.radar.Model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class E1FrameParticipant {
    private String orgCode; //站点编码，030004
    private String topic;//EZ030004MatchResultMiniData
    private String recordSerialNumber;//数据流水号，station{站点编码}{时间戳}{3位随机数}
    private int id;//参与者全域ID，唯一值；
    private int originalType;//车辆类型  参加 附录-车辆类型中的类型代码
    private int vehicleType;//收费车型
    private int direction;//行驶方向：1-上行（从机场高速上站）2-下行（从机场高速出站）
    private int originalColor;//车辆颜色：参见 附录-车辆颜色中的类型代码
    private double longitude;//分辨率1e-7°，东经为正，西经为负
    private double latitude;//分辨率1e-7°，北纬为正，南纬为负
    private double speed;//速度，单位：Km/h
    private int laneNum;//由行驶方向内侧向外顺序递增（应急 车道固定编码 9），跨多车道时采用组 合编号。3+1 车道对应的车道号依次为 1、2、3、9。
    private double courseAngle;//航向角，单位：°，保留1位小数，车头与正北夹角
    private String picLicense;//车牌号
    private int vehicleColor;//车牌颜色 参见 附录-车牌颜色中的类型代码
    private int axleNum;//轴数
    private String axleGroupType;//轴组型
    private int totalWeight;//车货总重  单位：kg
    private int wholeOver;//超限量  单位：kg
    private int overLimitFlag;//超限标识  0、未超限；1、超限； -1：称重失败
    private Integer etcInStatus;//ETC预读状态(入口)  0：正常，1：异常  -1:MTC车辆
    private Integer etcOutStatus;//ETC交易状态(出口)  0：正常，1：异常  -1:非ETC车辆
    private Integer mileage; //里程
}
