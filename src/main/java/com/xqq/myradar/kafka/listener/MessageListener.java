package com.xqq.myradar.kafka.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.xqq.myradar.radar.Buffer.DevicesBuffer;
import com.xqq.myradar.radar.Buffer.LicenselocBuffer;
import com.xqq.myradar.radar.Buffer.RealDataBuffer;
import com.xqq.myradar.radar.Entity.Licenseloc;
import com.xqq.myradar.radar.Model.CarplateV3Model;
import com.xqq.myradar.radar.Model.VehicleModel;
import com.xqq.myradar.radar.Service.VehicleModelService;
import com.xqq.myradar.redis.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.xqq.myradar.radar.Utils.ConvertUtil.LaserUTM2Frenet;
import static com.xqq.myradar.radar.Utils.ConvertUtil.gpstoutm;

//@Component
@Slf4j
public class MessageListener {

    @Autowired
    VehicleModelService vehicleModelService;

    static int flag = 0;
    @KafkaListener(topics = {"EZ030003MatchResultMiniData"},containerFactory = "radarKafkaFactory")//指定topic和容器工厂
    public void radarListener(List<ConsumerRecord<?, ?>> records)   {
        List<VehicleModel> vehicleModelList = new ArrayList<>();
        records.stream().forEach(
                r ->{
                    try {
                        if(flag%4==0){
                            JSONObject value = JSONObject.parseObject(new String((byte[]) r.value(), "UTF-8"));
                            if(DevicesBuffer.getS().sellectByip(value.getString("deviceCode"))!=null&&DevicesBuffer.getS().sellectByip(value.getString("deviceCode")).getState()==1) {//设备有效才进行解
                                //开始解析接收到的json文件
                                int num = value.getInteger("participantNum");
                                Long globalTimeStamp = value.getLong("globalTimeStamp");
                                long remainder = globalTimeStamp % 1000;
                                remainder = (long) Math.floor(remainder / 200);
                                globalTimeStamp = (globalTimeStamp / 1000) * 1000 + remainder * 200;
                                //JedisUtil.saveTimeStamp(globalTimeStamp);
                                if (num > 0) {
                                    JSONArray e1FrameParticipant = value.getJSONArray("e1FrameParticipant");
                                    for (Object e : e1FrameParticipant) {
                                        // log.warn("这是微波雷达解析一次的开始时间"+System.currentTimeMillis());
                                        HashMap<String, String> radarData = new HashMap<>();
                                        VehicleModel vehicleModel = new VehicleModel();
                                        JSONObject ejs = (JSONObject) e;
                                        vehicleModel.setId(ejs.getInteger("id"));
                                        vehicleModel.setType(ejs.getString("originalType"));
                                        vehicleModel.setTimestamp(globalTimeStamp);
                                        vehicleModel.setHeadingAngle(ejs.getFloat("courseAngle"));
                                        float getSpeed = (float) (ejs.getFloat("speed")/3.6);
                                        vehicleModel.setSpeed(getSpeed);
                                        vehicleModel.setLongitude(ejs.getDouble("longitude"));
                                        vehicleModel.setLatitude(ejs.getDouble("latitude"));
                                        vehicleModel.setLane(ejs.getInteger("laneNum"));
                                        vehicleModel.setRoadDirect(ejs.getInteger("direction"));

                                        vehicleModel.setIp(value.getString("deviceCode"));
                                        //System.out.println("vehicleModel.setIp(value.getString(\"deviceCode\"));"+value.getString("deviceCode"));
//                                        if (LaneInfoBufferRight.getS().laneInfomap.containsKey(value.getString("deviceCode")) || LaneInfoBufferLeft.getS().laneInfomap.containsKey(value.getString("deviceCode"))) {
                                            Date date = new Date();
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                                            vehicleModel.setCode(dateFormat.format(date));
                                            //System.out.println(globalTimeStamp);
                                            if (ejs.getFloat("speedX") == null)
                                                vehicleModel.setSpeedx(0);
                                            else
                                                vehicleModel.setSpeedx((float) (ejs.getFloat("speedX")/3.6));
                                            if (ejs.getFloat("speedY") == null)
                                                vehicleModel.setSpeedy(0);
                                            else
                                                vehicleModel.setSpeedy((float) (ejs.getFloat("speedY")/3.6));
                                            double longitude = ejs.getDouble("longitude");
                                            double latititude = ejs.getDouble("latitude");
                                            //坐标转换
//                                        double[] utm = gpstoutm(longitude, latititude);

                                            String RadarNumber = value.getString("deviceCode");
                                            double Angle = ejs.getFloat("courseAngle");
                                            //坐标转换
                                            //double result[] = UTM2Frenet(utm[0], utm[1], Angle, RadarNumber);

                                            int left = DevicesBuffer.getS().sellectByip(RadarNumber).getRoadDirect().equals("2") ? 1 : 0;//判断是不是左幅
                                            vehicleModel.setRoadDirect(left + 1);//左幅设置2，右幅设置1；根据静态信息覆盖掉雷达设备检测出来的车道方向

                                        /*vehicleModel.setFrenetx(result[0]);
                                        vehicleModel.setFrenety(result[1]);
                                        vehicleModel.setFrenetAngle((float) result[2]);
                                        vehicleModel.setLane((int) result[3]);
                                        vehicleModel.setMercatorx(utm[0]);
                                        vehicleModel.setMercatory(utm[1]);*/

                                            String vmJson = JSONObject.toJSONString(vehicleModel);
                                            radarData.put(String.valueOf(vehicleModel.getId()), vmJson);
                                            //数据写入Redis用于去重
                                            JedisUtil.saveMicrowaveData(radarData, globalTimeStamp);
                                            vehicleModelList.add(vehicleModel);
//                                        } else {
//                                            //System.out.println("现在时间"+globalTimeStamp+"发现一个无探测范围雷达"+value.getString("deviceCode"));
//                                        }
                                        // log.warn("这是微波雷达解析一次的结束时间"+System.currentTimeMillis());
                                    }
                                }

                            }
                            if(flag!=0) {
                                flag=0;
                            }


                            //log.warn("这是微波雷达解析一次的结束时间"+System.currentTimeMillis());
                        }
                        flag++;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    System.out.println("topic:"+r.topic()+"|partition:"+r.partition()+"|offset:"+r.offset()+"|value:"+r.value());
                }
        );
        //System.out.println("雷达结束时间"+System.currentTimeMillis()+"数据长度"+vehicleModelList.size());
        if (vehicleModelList.size() > 0){
            RealDataBuffer.addVehicleModel(vehicleModelList);
        }

    }



    @KafkaListener(topics = {"EZPositionCameraData"},containerFactory = "picKafkaFactory")
    public void picListener(ConsumerRecord<?, ?> record) throws UnsupportedEncodingException {
//       System.out.println("牌照开始时间"+System.currentTimeMillis());
        long offset = record.offset();
//       log.info(String.valueOf(offset));
        JSONObject value = JSONObject.parseObject((String) record.value());
        JSONArray locationInfoList = value.getJSONArray("LocationInfoList");
        String picLicence = "";
        for(Object o:locationInfoList){
            JSONObject jo = (JSONObject)o;
            picLicence = jo.getString("picLicense");
            break;
        }
        if(!picLicence.equals("默A00000")) {
            log.warn("牌照解析开始"+System.currentTimeMillis());
            CarplateV3Model cvm = new CarplateV3Model();
            //pic.setIp(value.getString("ip"));
            String ip = value.getString("ip");
            String picId = "";
            String gantryId = "";
            int cameraNum = 0;
            String pictime = "";
            int laneNum = 0;
            int vehicleId = 0;
            int licenseColor = 0;
            long globalTimeStamp = 0;
            double vehSpeed = 0.0;
            for (Object o : locationInfoList) {
                JSONObject jo = (JSONObject) o;
                picId = jo.getString("picId");
                gantryId = jo.getString("gantryId");
                cameraNum = jo.getInteger("cameraNum");
                pictime = jo.getString("picTime");
                laneNum = jo.getInteger("laneNum");
                vehicleId = jo.getInteger("vehicleId");
                licenseColor = jo.getInteger("licenseColor");
                vehSpeed = jo.getDouble("vehSpeed");
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                pictime = pictime.replace('T',' ');
                Date date = df.parse(pictime);
                globalTimeStamp = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date date = new Date();
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

            Licenseloc licenseloc = LicenselocBuffer.getS().sellectBydeviceCode(gantryId);

            if(licenseloc!=null&&licenseloc.getState()==1){
                double start = licenseloc.getStart();
                double end = licenseloc.getEnd();
                String roadDirect = licenseloc.getRoadDirect();
                int state=0;//初始话状态，使之为0,表示未进行匹配
                cvm.setIp(ip).setPicId(picId).setGantryId(gantryId).setCameraNum(cameraNum).setPicTime(pictime).
                        setLaneNum(laneNum).setVehicleId(vehicleId).setLicenseColor(licenseColor).setVehSpeed(vehSpeed).setGlobalTimeStamp(globalTimeStamp)
                        .setPicLicense(picLicence).setSaveTime(dateFormat.format(date)).setStart(start).setEnd(end).setRoadDirect(roadDirect).setState(state);
                HashMap<String,String> picData = new HashMap<>();
                String picJson = JSONObject.toJSONString(cvm);
                System.out.println(picJson);
                picData.put(String.valueOf(cvm.getPicLicense()),picJson);
                JedisUtil.saveLicensePlate(picData,globalTimeStamp); //存储进入Redis
                RealDataBuffer.addCarplate(cvm);
                log.warn("牌照解析结束"+System.currentTimeMillis());
            }
            else if(licenseloc!=null&&licenseloc.getState()==0){
                double start = licenseloc.getStart();
                double end = licenseloc.getEnd();
                String roadDirect = licenseloc.getRoadDirect();
                int state=0;//初始话状态，使之为0,表示未进行匹配
                cvm.setIp(ip).setPicId(picId).setGantryId(gantryId).setCameraNum(cameraNum).setPicTime(pictime).
                        setLaneNum(laneNum).setVehicleId(vehicleId).setLicenseColor(licenseColor).setVehSpeed(vehSpeed).setGlobalTimeStamp(globalTimeStamp)
                        .setPicLicense(picLicence).setSaveTime(dateFormat.format(date)).setStart(start).setEnd(end).setRoadDirect(roadDirect).setState(state);
//               HashMap<String,String> picData = new HashMap<>();
//               String picJson = JSONObject.toJSONString(cvm);
//               System.out.println(picJson);
//               picData.put(String.valueOf(cvm.getPicLicense()),picJson);
//               JedisUtil.saveLicensePlate(picData,globalTimeStamp); //存储进入Redis
                    RealDataBuffer.addCarplate(cvm);
            }
//           else {
//               cvm.setIp(ip).setPicId(picId).setGantryId(gantryId).setCameraNum(cameraNum).setPicTime(pictime).
//                       setLaneNum(laneNum).setVehicleId(vehicleId).setLicenseColor(licenseColor).setVehSpeed(vehSpeed).setGlobalTimeStamp(globalTimeStamp)
//                       .setPicLicense(picLicence).setSaveTime(dateFormat.format(date)).setStart(0).setEnd(0).setRoadDirect("未知牌照设备数据").setState(0);;
//           }



        }
    }

    /**
     *   激光雷达数据解析
     * @param records 二进制数据
     */
    @KafkaListener(topics = {"EZ030001MatchResultMiniData","EZ030002MatchResultMiniData"},containerFactory = "fpKafkaFactory")
    public void fpListener(List<ConsumerRecord<?, ?>> records) {
        List<VehicleModel> vehicleModelList = new ArrayList<>();
        records.stream().forEach(
                r->{
                    JSONObject value = null;
                    try {
                        value = JSONObject.parseObject(new String((byte[]) r.value(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    long offset = r.offset();
                    String topic = r.topic();
                    int num = value.getInteger("participantNum");
                    Long globalTimeStamp = value.getLong("globalTimeStamp");
                    long remainder = globalTimeStamp%1000;
                    remainder = (long) Math.floor(remainder/200);
                    globalTimeStamp = (globalTimeStamp/1000)*1000 + remainder*200;
                    //JedisUtil.saveTimeStamp(globalTimeStamp);
                    if(num>0){
                        //  log.info(String.valueOf(value));
                        JSONArray e1FrameParticipant = value.getJSONArray("e1FrameParticipant");
                        for(Object e:e1FrameParticipant) {
                            try {
                                //log.warn("激光雷达解析开始"+System.currentTimeMillis()+"时间戳:"+globalTimeStamp);
                                HashMap<String, String> radarLightData = new HashMap<>();
                                VehicleModel vehicleModel = new VehicleModel();
                                JSONObject ejs = (JSONObject) e;
                                String orgCode = ejs.getString("orgCode").replaceFirst("0", "");

                                if(orgCode.equals("30001")){//只要激光雷达右幅数据
//                                    System.out.println("激光数据下"+orgCode);
                                    String sn = ejs.getString("stakeNum");
                                    if(sn != null&& !sn.equals("") ){
                                        float stakeNum = (sn.charAt(1)-48)*1000+Float.valueOf(sn.substring(2));
                                        vehicleModel.setFiberX((int) stakeNum);
                                    }
                                    vehicleModel.setId(ejs.getInteger("id"));
                                    vehicleModel.setTimestamp(globalTimeStamp);
                                    vehicleModel.setCode(ejs.getString("orgCode"));
                                    vehicleModel.setHeadingAngle(ejs.getFloat("courseAngle"));
                                    float getSpeed = ejs.getFloat("speed");
                                    getSpeed = (float) (getSpeed/3.6);
                                    vehicleModel.setSpeed(getSpeed);

                                    vehicleModel.setLongitude(ejs.getDouble("longitude"));
                                    vehicleModel.setLatitude(ejs.getDouble("latitude"));
                                    vehicleModel.setIp(ejs.getString("ip"));
                                    vehicleModel.setPosx(111);
                                    double[] UTMresult = gpstoutm(ejs.getDouble("longitude"), ejs.getDouble("latitude"));
                                    double UTME = UTMresult[0];
                                    double UTMN = UTMresult[1];
                                    vehicleModel.setMercatorx(UTME);
                                    vehicleModel.setMercatory(UTMN);

//                                System.out.println("orgCode的值为" + orgCode + "UTME" + UTME + "UTMN" + UTMN);
                                    double[] result = LaserUTM2Frenet(UTME, UTMN, ejs.getFloat("courseAngle"), orgCode);
                                    double frenetx = result[0];
                                    double frenety = result[1];
                                    double frenetAngle = result[2];
                                    double laneNumber = result[3];
                                    //vehicleModel.setLane(ejs.getInteger("laneNum"));
                                    vehicleModel.setLane((int) laneNumber);//
                                    vehicleModel.setFrenetx(frenetx);
                                    vehicleModel.setFrenety(frenety);
                                    vehicleModel.setLiscenseColor(-1);
                                    vehicleModel.setCarId("0");
                                    vehicleModel.setFrenetAngle((float) frenetAngle);
                                    int direction = 0;
                                    if (orgCode.equals("30002")) {
                                        direction = 1;
                                        vehicleModel.setSpeedx(getSpeed);
                                    } else {
                                        direction = 2;
                                        vehicleModel.setSpeedx(0-getSpeed);
                                    }
                                    vehicleModel.setRoadDirect(direction);
                                    vehicleModel.setIp(value.getString("topic"));
                                    Date date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                                    vehicleModel.setCode(dateFormat.format(date));
                                    String vmJson = JSONObject.toJSONString(vehicleModel);
                                    radarLightData.put(String.valueOf(vehicleModel.getId()), vmJson);
//                                    JedisUtil.saveLidarData(radarLightData, globalTimeStamp);
                                    vehicleModelList.add(vehicleModel);
                                }
                                else if(orgCode.equals("30002")){//右幅数据
                                    String sn = ejs.getString("stakeNum");
                                    if(sn != null&& !sn.equals("") ){
                                        float stakeNum = (sn.charAt(1)-48)*1000+Float.valueOf(sn.substring(2));
                                        vehicleModel.setFiberX((int) stakeNum);
                                    }
                                    vehicleModel.setId(ejs.getInteger("id"));
                                    vehicleModel.setTimestamp(globalTimeStamp);
                                    vehicleModel.setCode(ejs.getString("orgCode"));
                                    vehicleModel.setHeadingAngle(ejs.getFloat("courseAngle"));
                                    float getSpeed = ejs.getFloat("speed");
                                    getSpeed = (float) (getSpeed/3.6);
                                    vehicleModel.setSpeed(getSpeed);

                                    vehicleModel.setLongitude(ejs.getDouble("longitude"));
                                    vehicleModel.setLatitude(ejs.getDouble("latitude"));
                                    vehicleModel.setIp(ejs.getString("ip"));
                                    vehicleModel.setPosx(111);
                                    double[] UTMresult = gpstoutm(ejs.getDouble("longitude"), ejs.getDouble("latitude"));
                                    double UTME = UTMresult[0];
                                    double UTMN = UTMresult[1];
                                    vehicleModel.setMercatorx(UTME);
                                    vehicleModel.setMercatory(UTMN);

//                                System.out.println("orgCode的值为" + orgCode + "UTME" + UTME + "UTMN" + UTMN);
                                    double[] result = LaserUTM2Frenet(UTME, UTMN, ejs.getFloat("courseAngle"), orgCode);
                                    double frenetx = result[0];
                                    double frenety = result[1];
                                    double frenetAngle = result[2];
                                    double laneNumber = result[3];
                                    vehicleModel.setLane(ejs.getInteger("laneNum"));
                                    vehicleModel.setLane((int) laneNumber);//
                                    vehicleModel.setFrenetx(frenetx);
                                    vehicleModel.setFrenety(frenety);
                                    vehicleModel.setLiscenseColor(-1);
                                    vehicleModel.setCarId("0");
                                    vehicleModel.setFrenetAngle((float) frenetAngle);
                                    int direction = 0;
                                    if (orgCode.equals("30002")) {
                                        direction = 1;
                                        vehicleModel.setSpeedx(getSpeed);
                                    } else {
                                        direction = 2;
                                        vehicleModel.setSpeedx(0-getSpeed);
                                    }
                                    vehicleModel.setRoadDirect(direction);
                                    vehicleModel.setIp(value.getString("topic"));
                                    Date date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                                    vehicleModel.setCode(dateFormat.format(date));
                                    String vmJson = JSONObject.toJSONString(vehicleModel);
                                    radarLightData.put(String.valueOf(vehicleModel.getId()), vmJson);
                                    JedisUtil.saveLidarData(radarLightData, globalTimeStamp);
                                    vehicleModelList.add(vehicleModel);
                                }
                                //log.warn("激光雷达解析结束"+System.currentTimeMillis());
                            }catch (Exception exception){
                                exception.printStackTrace();
                            }
                        }
                    }
                }
        );
        if (vehicleModelList.size() > 0){
            RealDataBuffer.addVehicleModel(vehicleModelList);
        }


    }
}
