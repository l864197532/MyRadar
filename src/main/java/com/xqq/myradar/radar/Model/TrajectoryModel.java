package com.xqq.myradar.radar.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrajectoryModel {
    private String type;
    private long trajId;
    private double frenetx;
    private double frenety;
    private float  speedx;
    private float  speedy;
    private float  DHW;
    private float  THW;
    private float  TTC;
    private float  accx;
    private int  RoadDirect   ;  // 道路左右辐
    private String  CarId;   //车牌
    private int licenseColor; //车牌颜色
    private float mercatorx;
    private float mercatory;
    private double longitude;
    private double latitude;
    private float headingAngle;
    private double frenetxPrediction;
    private double frenetyPrediction;
    private int lane; //车道编号
    private int rawId; //雷达或者光纤分配给目标的编号
    private long timeStamp;
    private int empty1;
    private int empty2;
    public Trajectory trajectoryModelToTrajectory() {
        Trajectory trajectory = new Trajectory();
        trajectory.setTimeStamp(this.timeStamp);
        trajectory.setTrajId(this.trajId);
        trajectory.setFrenetx(this.frenetx);
        trajectory.setFrenety(this.frenety);
        trajectory.setSpeedx(this.speedx);
        trajectory.setSpeedy(this.speedy);
        trajectory.setDHW(DHW);
        trajectory.setTHW(this.THW);
        trajectory.setTTC(this.TTC);
        trajectory.setAccx(this.accx);
        trajectory.setRoadDirect(this.RoadDirect);
        trajectory.setCarId(this.CarId);
        trajectory.setLicenseColor(this.licenseColor);
        trajectory.setMercatorx(this.mercatorx);
        trajectory.setMercatory(this.mercatory);
        trajectory.setLongitude(this.longitude);
        trajectory.setLatitude(this.latitude);
        trajectory.setHeadingAngle(this.headingAngle);
        trajectory.setEmpty1(this.rawId);
        trajectory.setSpeedx(this.speedx);
        trajectory.setLane(this.lane);
        return trajectory;
    }
}
