package com.xqq.myradar.radar.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
@Getter
@Setter
public class TrajectoryModelLine {
    public int state; //状态位，表示在当前帧该轨迹是否已经关联
    public int emptyFrameNum; //表示出现的空帧数
    public long carIdTimestamp;//存储当前轨迹的上一次牌照绑定时间
    public int earlyWarningFrame;//记录预警事件帧数
    public int earlyWarninType;//记录预警事件类型
    public int valiateFrameNum;

    public List<TrajectoryModel> trajectoryModels = new LinkedList<>();

}
