package com.xqq.myradar.redis;


import com.xqq.myradar.radar.Model.CarplateV3Model;
import com.xqq.myradar.radar.Model.VehicleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.xqq.myradar.redis.JedisCompoment.*;

@Component
public class JedisUtil {

    @Autowired
    private RedisService data2Redis;

    public static JedisUtil jedisUtil;

    //初始化
    @PostConstruct
    public void init() {
        jedisUtil = this;
        jedisUtil.data2Redis= this.data2Redis;
    }

    //雷达直连获得的数据
    public  static void  saveData(Map<String, String> map, long timeStamp) { jedisUtil.data2Redis.saveData(map, timeStamp,REAL_DATA); }//实时数据存放时间戳  微波雷达数据
    public static void saveDateStamp(long timeStamp) { jedisUtil.data2Redis.saveTimeStamp(timeStamp, REAL_DATA); }


    //kafka 获得的微波雷达数据
    public static void saveMicrowaveData(Map<String, String> map,long timeStamp){ jedisUtil.data2Redis.saveData(map,timeStamp,MICROWAVE_DATA); }
    public  static void getMicrowaveData(long timeStamp,List<VehicleModel> vm) { jedisUtil.data2Redis.getData(timeStamp,vm,MICROWAVE_DATA); }

    //kafka 获得的激光雷达数据
    public  static void getLidarData(long timeStamp,List<VehicleModel> vm) { jedisUtil.data2Redis.getData(timeStamp,vm,LIDAR_DATA); }
    public static void saveLidarData(Map<String, String> map,long timeStamp){ jedisUtil.data2Redis.saveData(map,timeStamp,LIDAR_DATA); }

    //zmq 获得的光纤数据
    public  static void getFiberData(long timeStamp,List<VehicleModel> vm) { jedisUtil.data2Redis.getData(timeStamp,vm,FIBER_DATA); }
    public static void saveFiberData(Map<String, String> map,long timeStamp){ jedisUtil.data2Redis.saveData(map,timeStamp,FIBER_DATA); }

    //数据去重后的数据
    public static void savaDedupTime(long timeStamp){ jedisUtil.data2Redis.saveTimeStamp(timeStamp,DUPLICATE_DATA); }
    public static long getDedupTime(){ return jedisUtil.data2Redis.getNextTimeStamp(DUPLICATE_DATA); }
    public static void saveDeduplicationData(Map<String, String> map,long timeStamp){ jedisUtil.data2Redis.saveData(map,timeStamp,DUPLICATE_DATA); }
    public  static void getCurrentData(long timeStamp, List<VehicleModel> vm) {jedisUtil.data2Redis.getData(timeStamp, vm,DUPLICATE_DATA); }


    public  static void   saveTrajectory(Map<String, String> map,long timeStamp){ jedisUtil.data2Redis.saveTrajectory(map, timeStamp); }




    //获取剩余的所有牌照数据
    public static boolean selectPic10Sec(List<CarplateV3Model> carplateV3ModelList10Secos){ return jedisUtil.data2Redis.selectPic10Sec(carplateV3ModelList10Secos,CARPLATE_DATA); }
    public static void deleteRedisCarId(List<CarplateV3Model> carIdList){ jedisUtil.data2Redis.deleteRedisCarId(carIdList,CARPLATE_DATA); }
    public  static void  saveLicensePlate(Map<String, String> map,long timeStamp){ jedisUtil.data2Redis.saveLicensePlate(map,timeStamp,CARPLATE_DATA); }


}
