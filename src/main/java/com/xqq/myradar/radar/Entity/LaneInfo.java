package com.xqq.myradar.radar.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class LaneInfo {
    private  int lid;
    private String radarId;
    private double directionAngle;
    private double longitudeStart;
    private double latitudeStart;
    private double UTME;
    private double UTMN;
    private double locationNumberStart;
}
