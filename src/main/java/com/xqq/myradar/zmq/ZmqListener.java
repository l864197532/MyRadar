package com.xqq.myradar.zmq;


import com.alibaba.fastjson.JSONObject;

import com.xqq.myradar.radar.Buffer.RealDataBuffer;
import com.xqq.myradar.radar.Model.E1FrameParticipant;
import com.xqq.myradar.radar.Model.VehicleModel;
import com.xqq.myradar.redis.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.xqq.myradar.radar.Utils.ConvertUtil.*;


@Slf4j
@Component
public class ZmqListener {

    public static void run() {
        ZmqSubThread zmqSubThread = new ZmqSubThread() {
            @Override
            public void dealWith(byte[] data) {
                Long startTimeStamp = System.currentTimeMillis();
                //log.warn("光纤的开始时间"+System.currentTimeMillis()+Thread.currentThread().getName());
                StringBuilder DEVICEIP = new StringBuilder();
                for (int i = 4; i < 8; i++) {
                    if(data[i]<0) {
                        int tmp=data[i];
                        DEVICEIP .append(String.valueOf((tmp<<24)>>>24)+".");
                    }
                    else DEVICEIP.append(String.valueOf(data[i])+".");
                }
                //DEVICEIP为服务IP
                DEVICEIP.setLength(DEVICEIP.length()-1);
                StringBuilder sbTime=new StringBuilder();
                for(int i=8;i<16;i++){
                    String toBIN=toEightBin(data[i]);
                    sbTime.insert(0,toBIN);
                }
                String timeFirst=sbTime.toString();
                //去除前缀0
                String newTimeStr = timeFirst.replaceFirst("^0*", "");
                //时间戳
                long time=toLong(newTimeStr);
                long remainder = time%1000;
                remainder = (long) Math.floor(remainder/200);
                time = (time/1000)*1000 + remainder*200;
                StringBuilder sbCount=new StringBuilder();
                for(int i=16;i<20;i++){
                    String toBIN=toEightBin(data[i]);
                    sbCount.insert(0,toBIN);
                }
                //车辆数量
                int len=data.length;
                int count= (int) toLong(sbCount.toString());
                List<VehicleModel> vm = new ArrayList<>();
                for (int i = 20; i < data.length; i+=44) {
                    //CarTotal存放一辆车的byte数据
                    StringBuilder CarTotal=new StringBuilder();
                    for (int j = i; j < i+44; j++) {
                        CarTotal.append(toEightBin(data[j]));
                    }
                    //车辆ID
                    long ID=toLong(toCarString(CarTotal,0,64));
                    String picId = "";
                    if (ID < 10)
                        picId = "鄂A1357"+ID;
                    else if (ID >= 10&& ID < 100)
                        picId = "鄂A246"+ID;
                    else {
                        ID = ID%1000;
                        picId ="鄂A11"+ID;
                    }

                    byte[] CarnumberByte=new byte[16];
                    for (int j = i+8,k=0; j < i+24&&k<16; j++,k++) {
                        CarnumberByte[k]=data[j];
                    }
                    //车牌号
                    String Carnumber=new String(CarnumberByte);
                    //车型
                    int type= (int) toLong(toCarString(CarTotal,192,200));
                    //车辆里程范围
                    int scope[]=new int [2];
                    scope[0]=(int)toLong(toCarString(CarTotal,200,232));
                    scope[1]=(int)toLong(toCarString(CarTotal,232,264));
                    //车速
                    float speed=Float.intBitsToFloat(Integer.parseInt(toCarString(CarTotal,264,296),2));

                    //车道号
                    int wayno= (int) toLong(toCarString(CarTotal,296,304));

                    //里程
                    int Tpointno= (int) toLong(toCarString(CarTotal,304,336));

                    //方向
                    int Direct=(int)toLong(toCarString(CarTotal,344,352));
                    String code="";
                    for (int j = 0; j < data.length; j++) {
                        code+=toEightBin(data[j]);
                    }
                    double D=laneNumber2frenetY(wayno);

                    double[] UTM=Frenet2UTMlasted(Tpointno,D,Direct);

                    double[] GPS = utmtogps(UTM[0], UTM[1]);

//                    JedisUtil.saveTimeStamp(time);
                    VehicleModel vehicleModel=new VehicleModel();
                    vehicleModel.setCode(code);
                    vehicleModel.setId((int) ID);
                    vehicleModel.setType(picId);//假牌照数据
                    vehicleModel.setTimestamp(time);
//                    vehicleModel.setType(String.valueOf(type));
                    vehicleModel.setSpeed(speed);
                    vehicleModel.setSpeedx(speed);
                    vehicleModel.setLane(wayno);
                    vehicleModel.setCarId("0");
                    vehicleModel.setLiscenseColor(-1);
                    vehicleModel.setRoadDirect(Direct);
                    vehicleModel.setFiberX(Tpointno);
                    vehicleModel.setFrenetx(Tpointno);
                    vehicleModel.setFrenety(D);
                    vehicleModel.setLongitude(GPS[0]);
                    vehicleModel.setLatitude(GPS[1]);
                    vehicleModel.setMercatorx(UTM[0]);
                    vehicleModel.setMercatory(UTM[1]);
                    vehicleModel.setHeadingAngle((float) UTM[2]);
                    Date date = new Date();
                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    vehicleModel.setCode(dateFormat.format(date));
                    Map<String,String> redisMap=new HashMap<>();
                    String vmJson = JSONObject.toJSONString(vehicleModel);
                    redisMap.put(String.valueOf(vehicleModel.getId()),vmJson);
                    JedisUtil.saveFiberData(redisMap,time);
                    vm.add(vehicleModel);

                }
                if (vm.size() > 0){
                    RealDataBuffer.addVehicleModel(vm);
                }

            }

        };

        ZmqSubThread2 zmqSubThread2 = new ZmqSubThread2() {
            @Override
            public void dealWith(byte[] data) {
                Long startTimeStamp = System.currentTimeMillis();
//                log.warn("光纤的开始时间"+System.currentTimeMillis()+Thread.currentThread().getName());
                StringBuilder DEVICEIP = new StringBuilder();
                for (int i = 4; i < 8; i++) {
                    if(data[i]<0) {
                        int tmp=data[i];
                        DEVICEIP .append(String.valueOf((tmp<<24)>>>24)+".");
                    }
                    else DEVICEIP.append(String.valueOf(data[i])+".");
                }
                //DEVICEIP为服务IP
                DEVICEIP.setLength(DEVICEIP.length()-1);
                StringBuilder sbTime=new StringBuilder();
                for(int i=8;i<16;i++){
                    String toBIN=toEightBin(data[i]);
                    sbTime.insert(0,toBIN);
                }
                String timeFirst=sbTime.toString();
                //去除前缀0
                String newTimeStr = timeFirst.replaceFirst("^0*", "");
                //时间戳
                long globalTimeStamp=toLong(newTimeStr);
                long remainder = globalTimeStamp%1000;
                remainder = (long) Math.floor(remainder/200);
                globalTimeStamp = (globalTimeStamp/1000)*1000 + remainder*200;
                StringBuilder sbCount=new StringBuilder();
                for(int i=16;i<20;i++){
                    String toBIN=toEightBin(data[i]);
                    sbCount.insert(0,toBIN);
                }
                //车辆数量
                int len=data.length;
                int count= (int) toLong(sbCount.toString());
                List<VehicleModel> vm = new ArrayList<>();
                List<E1FrameParticipant> elFrameParticipants = new ArrayList<>();

                for (int i = 20; i < data.length; i+=44) {
                    //CarTotal存放一辆车的byte数据
                    StringBuilder CarTotal=new StringBuilder();
                    for (int j = i; j < i+44; j++) {
                        CarTotal.append(toEightBin(data[j]));
                    }
                    //车辆ID
                    long ID=toLong(toCarString(CarTotal,0,64));
                    String picId = "";
                    if (ID <10)
                        picId = "鄂A1357"+ID;
                    else if (ID > 10&& ID < 100)
                        picId = "鄂A246"+ID;
                    else {
                        ID = ID%1000;
                        picId ="鄂A11"+ID;
                    }
                    byte[] CarnumberByte=new byte[16];
                    for (int j = i+8,k=0; j < i+24&&k<16; j++,k++) {
                        CarnumberByte[k]=data[j];
                    }
                    //车牌号
                    String Carnumber=new String(CarnumberByte);
                    //车型
                    int type= (int) toLong(toCarString(CarTotal,192,200));
                    //车辆里程范围
                    int scope[]=new int [2];
                    scope[0]=(int)toLong(toCarString(CarTotal,200,232));
                    scope[1]=(int)toLong(toCarString(CarTotal,232,264));
                    //车速
                    float speed=Float.intBitsToFloat(Integer.parseInt(toCarString(CarTotal,264,296),2));

                    //车道号
                    int wayno= (int) toLong(toCarString(CarTotal,296,304));

                    //里程
                    int Tpointno= (int) toLong(toCarString(CarTotal,304,336));

                    //方向
                    int Direct=(int)toLong(toCarString(CarTotal,344,352));

                    String code="";
                    for (int j = 0; j < data.length; j++) {
                        code+=toEightBin(data[j]);
                    }

                    double D=laneNumber2frenetY(wayno);
                    double[] UTM=Frenet2UTMlasted(Tpointno,D,Direct);
                    double[] GPS = utmtogps(UTM[0], UTM[1]);
                    //JedisUtil.saveTimeStamp(globalTimeStamp);
                    VehicleModel vehicleModel=new VehicleModel();
                    vehicleModel.setCode(code);
                    vehicleModel.setId((int) ID);
                    vehicleModel.setType(picId);
                    vehicleModel.setLiscenseColor(-1);
                    vehicleModel.setCarId("0");
                    vehicleModel.setTimestamp(globalTimeStamp);
                    vehicleModel.setSpeed(speed);
                    vehicleModel.setSpeedx(0-speed);  //光纤的roadDurect为2时取负
                    vehicleModel.setLane(wayno);
                    vehicleModel.setRoadDirect(Direct);
                    vehicleModel.setFiberX(Tpointno);
                    if(Tpointno==0){
                       // System.out.println("光纤FiberX为0");
                    }
                    vehicleModel.setFrenetx(Tpointno);
                    vehicleModel.setFrenety(D);
                    vehicleModel.setLongitude(GPS[0]);
                    vehicleModel.setLatitude(GPS[1]);
                    vehicleModel.setMercatorx(UTM[0]);
                    vehicleModel.setMercatory(UTM[1]);
                    vehicleModel.setHeadingAngle((float) UTM[2]);
                    Date date = new Date();
                    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    vehicleModel.setCode(dateFormat.format(date));
                    Map<String,String> redisMap=new HashMap<>();
                    String vmJson = JSONObject.toJSONString(vehicleModel);
                    redisMap.put(String.valueOf(vehicleModel.getId()),vmJson);
                    JedisUtil.saveFiberData(redisMap,globalTimeStamp);
                    vm.add(vehicleModel);
                    elFrameParticipants.add(vehicleModel.vehicleModel2ElFrameParticipant());
//                    log.warn("光纤的结束时间"+System.currentTimeMillis()+Thread.currentThread().getName());

                    /*log.warn("车辆ID:"+ID+" "+"车牌号:"+Carnumber+" "+"车型:"+type+" "+"车辆里程范围:"
                            +scope[0]+","+scope[1]+" "+"车速:"+speed+" "+"车道号:"+wayno+" "
                            +"里程:"+Tpointno+" "+"方向:"+Direct+" "
                    );*/
                }
                if (vm.size() > 0){
                    RealDataBuffer.addVehicleModel(vm);//读入缓冲区域 addVehicleModel 是写入数据源2
                }

                //System.out.println("zmq的结束时间"+System.currentTimeMillis()+Thread.currentThread().getName()+"总共时间"+endTimeStamp);
                //System.out.println("服务IP:"+DEVICEIP+" "+"时间戳:"+globalTimeStamp+" "+"车辆数量:"+count);
                //System.out.println("----------------------------------------------------------------------------------------------------------");
            }
        };
        Thread thread = new Thread(zmqSubThread);
        thread.start();
        Thread thread2 = new Thread(zmqSubThread2);
        thread2.start();
    }

    //把byte转为八位二进制数
    public static String toEightBin(byte data){
        StringBuilder sb=new StringBuilder();
        if(data>=0){
            String toBIN=Integer.toBinaryString(data);
            int fillCnt=8-toBIN.length();
            for (int j = 0; j < fillCnt; j++) {
                sb.append("0");
            }
            sb.append(toBIN);
        }else{
            String toBIN=Integer.toBinaryString(data);
            sb.append(toBIN.substring(toBIN.length()-8));
        }
        return sb.toString();
    }

    //二进制转为十进制数组
    public static Integer[] toArrayInt(String timeFirst){
        Integer [] toINt=new Integer[8];
        int cnt=0;
        for (int i = 0; i < timeFirst.length(); i+=8) {
            String timeTmp=timeFirst.substring(i,i+8);
            toINt[cnt]=BinaryToDecimal(timeTmp);
            cnt++;

        }
        return toINt;
    }

    public static int BinaryToDecimal(String timeFirst){
        int decimal = 0;
        for(int i=0;i<timeFirst.length();i++){
            int timeAt=Integer.parseInt(String.valueOf(timeFirst.charAt(i)));
            decimal+=timeAt*Math.pow(2,7-i);
        }
        return decimal;
    }

    public  static  long BinToLong(Integer []arrayInt){
        long decimal = 0;
        for(int i=0;i<arrayInt.length;i++){
            decimal+=arrayInt[i]*(Math.pow(16,7-i));
        }
        return decimal;
    }

    //二进制转为long
    public static long toLong(String timeFirst){
        long res=0;
        for(int i=0;i<timeFirst.length();i++){
            int timeAt=Integer.parseInt(String.valueOf(timeFirst.charAt(i)));
            res+=timeAt*Math.pow(2,timeFirst.length()-i-1);
        }
        return res;
    }

    public static  String toCarString(StringBuilder CarTotal,int start,int end){
            String first=CarTotal.substring(start,end);
            StringBuilder seco=new StringBuilder();
            for (int j = end-start-8; j >=0 ; j-=8) {
                seco.append(first.substring(j,j+8));
            }
            return seco.toString();
    }


}


