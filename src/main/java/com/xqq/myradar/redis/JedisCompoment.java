package com.xqq.myradar.redis;


public class JedisCompoment {
    public final static int REAL_DATA=10;//雷达直连 实时数据存放地点

    public final static int FIBER_DATA=0;//存储光纤数据;
    public final static int MICROWAVE_DATA=1;//存储微波雷达数据;
    public final static int LIDAR_DATA=2;//存储微波雷达数据;
    public final static int CARPLATE_DATA=3;//存储车牌照数据;

    public final static int DUPLICATE_DATA=4;//存储微波雷达、激光、光纤去重后的数据;
    public final static int TRAJ_DATA=5;//存储的融合后数据;

}
