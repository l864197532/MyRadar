package com.xqq.myradar.radar.Buffer;


import com.xqq.myradar.radar.Mapper.TrajMapper;
import com.xqq.myradar.radar.Model.*;
import com.xqq.myradar.radar.Utils.TableUtils;
import com.xqq.myradar.redis.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 用于进行轨迹融合的缓冲区
 */

@Component
public class TrajBuffer {

    @Autowired
    TrajMapper trajMapper;

    private long trajId = 0;    //当前的最大轨迹编号
    public List<TrajectoryModelLine> trajectoryModelLineArrayList = new LinkedList<>(); //将设备数据固化在内存
    public  List<VehicleModel> virtualFiberData = new LinkedList<>();
    public  List<RealTrajModLine> realTrajModLineList = new LinkedList<>();
    private static TrajBuffer trajBuffer= new TrajBuffer();

    private TrajBuffer(){

    }
    public static TrajBuffer getS(){//为使外部能访问 故此用static
        return trajBuffer;
    }


    public long  getMaxTrajId(){       // 获取当前最大轨迹轨迹编号
        trajId++;
        return trajId;

    }

    /**
     *
     * @param trajectoryModel 传过来的时缓冲区存储的轨迹融合对象
     */
    public void insertBufferToTable(TrajectoryModel trajectoryModel,long timeStamp){    //对所有的轨迹删除最远的一帧,并且将数据写入数据库，调用service
        Trajectory trajectory = new Trajectory();   //存储存入数据库的轨迹对象
        trajectory = trajectoryModel.trajectoryModelToTrajectory(); //将缓存区的对象转换成数据库存储的对象
        trajectory.setTimeStamp(timeStamp);
        String trajTableName = TableUtils.getTodayTrajTableName();
//        System.out.println("我要写入数据库了");
        trajMapper.insertSingle(trajTableName,trajectory);

    }
    public void insertBuffer2DataBase(List<Trajectory> trajectoryList){    //对所有的轨迹删除最远的一帧,并且将数据写入数据库，调用service
        String trajTableName = TableUtils.getTodayTrajTableName();
//        System.out.println("我要写入数据库了");
        trajMapper.insertBatch(trajTableName,trajectoryList);

    }
    public void insertBuffer2Redis(Map<String,String> trajectoryMap,Long timeStamp){    //对所有的轨迹删除最远的一帧,并且将数据写入数据库，调用service
        String trajTableName = TableUtils.getTodayTrajTableName();
//        System.out.println("我要写入Redis中了");
        JedisUtil.saveTrajectory(trajectoryMap,timeStamp);

    }


    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
        trajBuffer = this;
        trajBuffer.trajMapper = this.trajMapper;
        // 初使化时将已静态化的testService实例化
    }


}
