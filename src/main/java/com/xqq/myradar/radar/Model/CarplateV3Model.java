package com.xqq.myradar.radar.Model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarplateV3Model {
    private String ip;//相机ip
    private String picId;//牌识流水号+相机编号+ 批次号+批次内序号
    private String gantryId;//门牌序号
    private int cameraNum;//相机编号
    private String picTime;//抓拍时间
    private long globalTimeStamp;//检测时间，unix时间戳，距离1970年时间
    private int laneNum;//物理车道编码 ,由行驶方向内侧向外顺序递增，跨多车道时采用组合编号。 示例：1（车道1）、123（同时覆
    private int vehicleId;//车辆编号
    private String picLicense;//车牌号码 ,无车牌时填”默 A00000”
    private double vehSpeed;//车辆速度
    private int licenseColor;//车牌颜色
    private String saveTime;
    private double start;
    private double end;
    private String roadDirect;
    private int state;

    public CarplateV3Model(String pictime, String savetime, String ip, String picId, String gantryId, int cameraNum, int laneNum, int vehicleId, String picLiense, int licenseColor, int vehSpeed, long globalTimeStamp, double start, double end) {
        this.picTime=pictime;
        this.saveTime=savetime;
        this.ip=ip;
        this.picId=picId;
        this.gantryId=gantryId;
        this.cameraNum=cameraNum;
        this.laneNum=laneNum;
        this.vehicleId=vehicleId;
        this.picLicense=picLiense;
        this.licenseColor=licenseColor;
        this.vehSpeed=vehSpeed;
        this.globalTimeStamp=globalTimeStamp;
        this.start=start;
        this.end=end;

    }

    public CarplateV3Model(String ip, String picId, String gantryId, int cameraNum, String picTime, long globalTimeStamp, int laneNum, int vehicleId, String picLicense, double vehSpeed, int licenseColor, String saveTime, double start, double end, String roadDirect, int state) {
        this.ip = ip;
        this.picId = picId;
        this.gantryId = gantryId;
        this.cameraNum = cameraNum;
        this.picTime = picTime;
        this.globalTimeStamp = globalTimeStamp;
        this.laneNum = laneNum;
        this.vehicleId = vehicleId;
        this.picLicense = picLicense;
        this.vehSpeed = vehSpeed;
        this.licenseColor = licenseColor;
        this.saveTime = saveTime;
        this.start = start;
        this.end = end;
        this.roadDirect = roadDirect;
        this.state = state;
    }

    public CarplateV3Model(){}
}
