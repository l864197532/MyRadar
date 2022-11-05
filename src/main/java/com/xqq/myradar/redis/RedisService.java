package com.xqq.myradar.redis;

import com.alibaba.fastjson.JSON;
import com.xqq.myradar.radar.Model.CarplateV3Model;
import com.xqq.myradar.radar.Model.VehicleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.*;

import static com.xqq.myradar.redis.JedisCompoment.CARPLATE_DATA;

@Service
public class RedisService {



    @Autowired
    private JedisPool jedisPool;
    public  void  saveData(Map<String, String> map, long timeStamp) {
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        try {
            //直接写入，重复的会自动去重
            //默认写到db0
            jedis.hset(String.valueOf(timeStamp), map);
            //设置过期时间
            jedis.expire(String.valueOf(timeStamp),60);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }


    }

    /**
     * 存储时间戳
     * @param timeStamp
     */
    public  void  saveTimeStamp(long timeStamp, int dbname) {
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        try {
            jedis.select(dbname);
            if (jedis.exists("timeStamp")){
                Set<String> timeStamp2 = jedis.zrange("timeStamp", 0, 0);
                Iterator<String> iterator = timeStamp2.iterator();
                long nextTimeStamp = Long.parseLong(iterator.next());
                if (nextTimeStamp <= timeStamp)
                    jedis.zadd("timeStamp", timeStamp,String.valueOf(timeStamp));
            }else {
                jedis.zadd("timeStamp", timeStamp,String.valueOf(timeStamp));
            }


        }
        catch (Exception e ){
            System.out.println("存储时间戳出错");
        }finally {
            jedis.close();//关闭对Redis资源的访问
        }


    }


    /**
     *
     * @param map 数据源
     * @param timeStamp 数据时间戳
     */
    public  void  saveData(Map<String, String> map,long timeStamp,int dbname) {
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        try {
            //直接写入，重复的会自动去重
            //0代表光纤数据库；1代表微波雷达数据库； 2代表的激光雷达数据库 4
            jedis.select(dbname);
            jedis.hset(String.valueOf(timeStamp), map);
            //设置过期时间
            jedis.expire(String.valueOf(timeStamp),60);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }

    }
    /**
     * 存储轨迹
     * @param map
     * @param timeStamp
     */
    public  void   saveTrajectory(Map<String, String> map,long timeStamp) {
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        try {
            //将数据写入到Redis的db1中
            jedis.select(5);//存储的融合后数据;
            jedis.hset(String.valueOf(timeStamp), map);
            //将轨迹数据设置过期时间为一分钟
            jedis.expire(String.valueOf(timeStamp),60);
        }catch (Exception e){
            System.out.println("存储轨迹信息出错");
        }finally {
            jedis.close();
        }


    }    /**
     *
     * @param timeStamp 获取的时间戳
     * @param vm //数据集合
     */
    public  void   getData(long timeStamp, List<VehicleModel> vm, int dbname) {
        //在连接池中得到Jedis连接
        //0代表光纤数据库；1代表微波雷达数据库； 2代表的激光雷达数据库

        Jedis jedis=jedisPool.getResource();
        Map<String, String> stringMap = new HashMap<>();
        try {
            jedis.select(dbname);
            stringMap = jedis.hgetAll(String.valueOf(timeStamp));
        }catch (Exception e) {
            System.out.println("从Redis获取实时数据出错");
        }finally {
            jedis.close();
        }

        Collection<String> values = stringMap.values();
        for (String value : values) {
            VehicleModel vehicleModel =  JSON.parseObject(value, VehicleModel.class);
            vm.add(vehicleModel);
        }

    }

    /**
     * 获取下一时刻进行轨迹融合的时间戳
     * @param dbname 数据库名
     * @return
     */
    public  long  getNextTimeStamp(int dbname) {
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        long nextTimeStamp = 0;
        try {
            jedis.select(dbname);
            Set<String> timeStamp2 = jedis.zrange("timeStamp", 0, 0);
            Iterator<String> iterator = timeStamp2.iterator();
            nextTimeStamp = Long.parseLong(iterator.next());
            Tuple timeStamp1 = jedis.zpopmin("timeStamp");
        } catch (Exception e){
            System.out.println("获取时间戳失败");
        }finally {
            jedis.close();
        }
        return nextTimeStamp;
    }


    /**
     * 获取当前进行轨迹融合的实时数据
     * @param timeStamp
     * @param vm
     */
    public  void   getCurrentData(long timeStamp,List<VehicleModel> vm) {
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        Map<String, String> stringMap = new HashMap<>();
        try {
            jedis.select(4);
            stringMap = jedis.hgetAll(String.valueOf(timeStamp));
        }catch (Exception e) {
            System.out.println("从Redis获取实时数据出错");
        }finally {
            jedis.close();
        }

        Collection<String> values = stringMap.values();
        for (String value : values) {
            VehicleModel vehicleModel =  JSON.parseObject(value, VehicleModel.class);
            vm.add(vehicleModel);
        }

    }


    public boolean selectPic10Sec(List<CarplateV3Model> carplateV3ModelList,int dbname){
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        Set<String> keys = new HashSet<>();
        Map<String,String> stringMap = new HashMap<>();
        try {
            jedis.select(dbname);//存储的牌照数据;
            keys = jedis.keys("*");
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()){
                String getCarIdKey = iterator.next();
                stringMap.putAll(jedis.hgetAll(getCarIdKey));
            }

        }catch (Exception e) {
            System.out.println("从Redis获取牌照数据出错");
        }finally {
            jedis.close();
        }
        if (stringMap.isEmpty())
            return false;
        Collection<String> values = stringMap.values();
        for (String value : values) {
            CarplateV3Model carplateV3Model =  JSON.parseObject(value, CarplateV3Model.class);
            carplateV3ModelList.add(carplateV3Model);
        }
        return true;
    }

    public void deleteRedisCarId(List<CarplateV3Model> carIdList,int dbname){
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(dbname);//选择牌照Redis数据库;
            for (CarplateV3Model carId : carIdList) {
//                jedis.del(String.valueOf(carId.getGlobalTimeStamp()));
                jedis.hdel(String.valueOf(carId.getGlobalTimeStamp()),carId.getPicLicense());
            }
        }catch (Exception e){
            System.out.println("删除牌照数据错误！");
        }finally {
            jedis.close();
        }
    }

    /**
     *
     * @param map 牌照信息
     * @param timeStamp 信息时间戳
     */
    public  void   saveLicensePlate(Map<String, String> map,long timeStamp,int dbname) {
        //在连接池中得到Jedis连接
        Jedis jedis=jedisPool.getResource();
        try {
            //将数据写入到Redis的db1中
            jedis.select(dbname);//存储的牌照数据;
            jedis.hset(String.valueOf(timeStamp), map);
            jedis.expire(String.valueOf(timeStamp),10*60);//牌照过期时间
        }catch (Exception e){
            System.out.println("存储牌照信息出错");
        }finally {
            jedis.close();
        }


    }
}
