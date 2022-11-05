package com.xqq.myradar.radar.Utils;

import com.alibaba.fastjson.JSONObject;


import com.xqq.myradar.radar.Buffer.DevicesBuffer;
import com.xqq.myradar.radar.Buffer.RealDataBuffer;
import com.xqq.myradar.radar.Entity.Data;
import com.xqq.myradar.radar.Entity.Device;
import com.xqq.myradar.redis.JedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;



import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;




/**
 * @authro: Chrix Wu
 * @create: 2021/07/05 10:00
 */

@Slf4j
public class DataUtil {


    private static final HashMap<Byte, String> hm = new HashMap<Byte, String>() {
        {
            put((byte) 0x00, "点");
            put((byte) 0x01, "小汽车");
            put((byte) 0x02, "卡车");
            put((byte) 0x03, "行人");
            put((byte) 0x04, "摩托车");
            put((byte) 0x05, "自行车");
            put((byte) 0x06, "超宽车");
            put((byte) 0x07, "保留");
        }
    };
    private static final HashMap<Byte, String> hm2 = new HashMap<Byte, String>() {
        {
            put((byte) 0x00, "未知");
            put((byte) 0x01, "机动车");
            put((byte) 0x02, "行人");
            put((byte) 0x03, "非机动车");
        }
    };
    private static final HashMap<Byte, String> hm3 = new HashMap<Byte, String>() {
        {
            put((byte) 0x00, "未知");
            put((byte) 0x01, "轿车");
            put((byte) 0x02, "面包车");
            put((byte) 0x03, "皮卡");
            put((byte) 0x04, "越野车/SUV");
            put((byte) 0x05, "商务车/MPV");
            put((byte) 0x06, "轻型客车");
            put((byte) 0x07, "中型客车");
            put((byte) 0x08, "大型客车");
            put((byte) 0x09, "公交车");
            put((byte) 0x10, "校车");
            put((byte) 0x11, "微型货车");
            put((byte) 0x12, "轻型货车");
            put((byte) 0x13, "中型货车");
            put((byte) 0x14, "大型货车");
            put((byte) 0x15, "重型货车");
            put((byte) 0x16, "集装箱车");
            put((byte) 0x17, "三轮车");
            put((byte) 0x18, "其他");
            put((byte) 0x19, "叉车");
        }
    };
    /**
     * @param firstBa  第一个字节数组
     * @param secondBA 第二个字节数组
     * @return 拼接后的数组
     * @brief: 拼接数组，上一个遗留的数组和下一条消息拼接
     * @create: 2021/07/12 21:31
     */
    public static byte[] linkByteArray(byte[] firstBa, byte[] secondBA) {
        if (firstBa == null)
            return secondBA;
        if (secondBA == null)
            return firstBa;
        Integer len = firstBa.length + secondBA.length;
        byte[] linkBA = new byte[len];
        System.arraycopy(firstBa, 0, linkBA, 0, firstBa.length);
        System.arraycopy(secondBA, 0, linkBA, firstBa.length, secondBA.length);
        return linkBA;
    }


    public static boolean isTailEnd(byte[] bytes) {
        byte[] tail = new byte[4];
        byte[] stdTail = new byte[]{(byte) 0xEA, (byte) 0xEB, (byte) 0xEC, (byte) 0xED};
        System.arraycopy(bytes, bytes.length - 4, tail, 0, 4);
        return Arrays.equals(tail, stdTail);
    }


    /**
     * @param bytes  字节数组
     * @param bstart 起始处理位置的索引
     * @return
     * @brief: 获取object的有效长度
     * @creat: 2021/07/09 20:42
     */
    public static Integer getLength(byte[] bytes, int bstart) {
        byte[] length = new byte[2];
        try {
            System.arraycopy(bytes, 4 + bstart, length, 0, 2);
            return Integer.valueOf(HUtil.bytes2Hex(length), 16);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getCrcCode(byte[] bytes, int bstart, int len) {
        byte[] crc = new byte[2];
        try {
            System.arraycopy(bytes, 2 + bstart + len, crc, 0, 2);
            return HUtil.bytes2Hex(crc);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param bytes 被查找对象
     * @param pos   从第 pos 个元素开始查找
     * @return 包头所在的索引
     * @brief: 查找包头(ca cb cc cd)所在位置的索引
     */
    public static Integer seek(byte[] bytes, int pos) {
        byte[] head = new byte[]{(byte) 0xca, (byte) 0xcb, (byte) 0xcc, (byte) 0xcd};
        int i = pos;    //主串当前下标
        int j = 0;      //子串下标
        while (i < bytes.length && j < 4) {
            if (bytes[i] == head[j]) {
                i++;
                j++;
            } else {
                i = i - j + 1;
                j = 0;
            }
        }
        if (j >= 4)
            return i - 4;
        else
            return -1;
    }


    /**
     * @param head 包头数组
     * @param tail 包尾数组
     * @return boolean
     * @brief: 检查字节数组完整性，是否包头开始，包尾结束
     * @create: 2021/07/10 20:33
     */
    public static boolean isHeadTailComplete(byte[] head, byte[] tail) {
        byte[] stdHead = new byte[]{(byte) 0xCA, (byte) 0xCB, (byte) 0xCC, (byte) 0xCD};
        byte[] stdTail = new byte[]{(byte) 0xEA, (byte) 0xEB, (byte) 0xEC, (byte) 0xED};

        boolean isHead = Arrays.equals(head, stdHead);
        boolean isTail = Arrays.equals(tail, stdTail);
        return isHead && isTail;
    }

    /**
     * @param bytes 待解析字节数组
     * @param start 字节数组起始位置索引
     * @brief: 解析字节数组，默认ca cb cc cd开始，否则返回错误
     * @create: 2021/07/02 11:24
     * @return: crc校验出错返回-1，数组不完整返回0，正常返回1
     */
    public static int decoding(byte[] bytes, Integer start, String ip, Map<Integer, Long> ids,Map<String,Double> timeStampMap) {
        byte[] head = new byte[4];
        byte[] length = new byte[2];//不算包头包尾呦
        byte[] dataType = new byte[1];
        byte[] deviceNumber = new byte[20];//这里是雷达设备编号呦它占20个字节
        byte[] dateTime = new byte[8];
        byte[] crcCode = new byte[2];
        byte[] tail = new byte[4];
        try {
            System.arraycopy(bytes, start, head, 0, 4);
            System.arraycopy(bytes, 4 + start, length, 0, 2);

            System.arraycopy(bytes, 6 + start, dataType, 0, 1);
            System.arraycopy(bytes, 7 + start, deviceNumber, 0, 20);//这里是雷达设备编号呦
            System.arraycopy(bytes, 27 + start, dateTime, 0, 8);
            //解析时间戳
            long timeStamp = ByteArr2TimeStamp(dateTime);

            JedisUtil.saveDateStamp(timeStamp);
            int len = Integer.valueOf(HUtil.bytes2Hex(length), 16); //数据长度

            System.arraycopy(bytes, len + 2 + start, crcCode, 0, 2);
            System.arraycopy(bytes, len + 4 + start, tail, 0, 4);

            //数组完整性检查（包头包尾是否完整）
            if (!isHeadTailComplete(head, tail)) {
//                System.out.println("数组不完整...");
                return 0;
            }

            //crc校验
            byte[] data = new byte[len - 2];
            System.arraycopy(bytes, 4 + start, data, 0, len - 2);
            if (!CrcUtil.CRC16_MSB1021(data, len - 2, crcCode)) {
//                System.out.println("crc校验出错");
                return -1;
            }
            //解析雷达编号
            String radarNumber = HUtil.convertHexToASC(HUtil.ByteArrayToHexString(deviceNumber));
            radarNumber = radarNumber.replaceAll("\u0000","");
            if(radarNumber.equals("")){
                radarNumber="NULL";
            }


            //解析目标车辆数据
            //System.out.println("这是雷达编号=》"+radarNumber);
            //根据内存中的数据进行判断雷达协议类型
            String radarAgreement = "";
            DevicesBuffer devicesBuffer = DevicesBuffer.getS();
            for (Device device : devicesBuffer.deviceList) {
                if (device.getIp().equals(radarNumber)) {
                    System.out.println(device.getIp());
                    radarAgreement = device.getRadarAgreement();//获取雷达协议
                    break;
                }

            }
            //添加雷达判断，调用不同的解析协议。

            Integer numOfVehicle = 0;

            byte[] objectData = new byte[len - 33];//目标数据
            System.arraycopy(bytes, 35 + start, objectData, 0, len - 33);//目标数据
            List<Data> vm = new ArrayList<>();
            Map<String,String> redisMap = new HashMap<>();
            //进行雷达协议进行不同的雷达协议解析，此处包括04和05协议
            if (radarAgreement.equals("radarAgreement05")){

                numOfVehicle = (len - 33) / 96;//有多少条记录（一条记录一个车）,05协议96字节的数据体
                decodeObjectData_05(objectData, timeStamp, numOfVehicle, radarNumber, vm,redisMap); //解析05协议


            }
            else {
                numOfVehicle = (len - 33) / 37;//有多少条记录（一条记录一个车），04协议37字节的数据体
                decodeObjectData_04(objectData, timeStamp, numOfVehicle, radarNumber, vm,redisMap, ids); //解析04协议

            }
            //System.out.println(vm.size());
            if(vm.size()>0){

                RealDataBuffer.addData(vm);//存入缓冲区

                //这个是雷达直接获取到的数据，Data
                JedisUtil.saveData(redisMap,vm.get(0).getTimestamp()); //将数据写入redis


                log.info("ip:"+ip+"下 "+"雷达编号:"+radarNumber+" 的"+vm.size()+"条数据为有效数据");
            }

            else{
                //System.out.println("ip:"+ip+"下 "+"雷达编号:"+radarNumber+"的数据为无效");
            }
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }
    public static String getExceptionInfo(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }
    /**
     * @param b 字节数组，长度为2
     * @return 转换后的有符号short
     * @brief: 字节数组转short
     */
    public static short getShort(byte[] b) {
        return (short) (((b[0] << 8) | b[1] & 0xff));
    }

    /**
     *
     * @param b 字节数组长度为4
     * @return转换后的有符号int
     */
    public static int getInt(byte[] b) {
        return ByteBuffer.wrap(b).getInt();
    }

    /**
     * @brief: 16进制原始数据解析为字符串
     * @create: 2021/07/01 19:15
     * @param: str 待解析字符串
     * @return: String 解析后的字符串
     */
    public static String decoding(String str) {
        try {
            JSONObject jo = new JSONObject();
            //包头 4字节
            String head = str.substring(0, 8);

            //数据长度 2字节
            String lenStr = new BigInteger(str.substring(8, 12), 16).toString();
            Integer length = Integer.valueOf(lenStr);

            //数据类型 1字节
            String dataType = str.substring(12, 14);

            //设备编号 20字节
            String deviceNumber = str.substring(14, 54);

            //数据时间 8字节
            String tsStr = str.substring(54, 70);
            String year = "20" + Integer.valueOf(tsStr.substring(0, 2), 16).toString();
            String month = Integer.valueOf(tsStr.substring(2, 4), 16).toString();
            String day = Integer.valueOf(tsStr.substring(4, 6), 16).toString();
            String hour = Integer.valueOf(tsStr.substring(6, 8), 16).toString();
            String min = Integer.valueOf(tsStr.substring(8, 10), 16).toString();
            String sec = Integer.valueOf(tsStr.substring(10, 12), 16).toString();
            String ms = Integer.valueOf(tsStr.substring(12, 16), 16).toString();
            String DateTime = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec + ":" + ms;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
            Date date = sdf.parse(DateTime);
            long timeStamp = date.getTime();

            //时间戳转北京时间
            /*
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
            Date dd = new Date(ts);
            String time = simpleDateFormat.format(dd);

            System.out.println(time);
            */

            //目标数据 车辆数 N = (length - 33) / 37
            //修改37改为96
            Integer NumOfVechile = (length - 33) / 37;
            List VehicleList = new ArrayList<String>();
            for (int i = 0; i < NumOfVechile; i++) {
                //TODO: 车辆信息进一步解析
                String VehicleInfo = str.substring(70 + i * 74, 144 + i * 74);
                System.out.println(VehicleInfo);

                //目标ID unsigned short
                Integer oId = Integer.valueOf(VehicleInfo.substring(0, 4), 16);

                //目标所属车道 unsigned char
                String oLane = VehicleInfo.substring(4, 6);

                //目标类型 unsigned char
                byte[] a = HexUtil.hexStringToBytes(oLane);
                char c = HexUtil.byteToChar(a);
                System.out.println("C:" + c);

                //目标长度 unsigned char
                VehicleList.add(VehicleInfo);
            }

            //crc校验码
            String crcStr = str.substring((length + 2) * 2, length * 2 + 8);
            //TODO: CRC校验 数据长度-目标数据
            //System.out.println(CrcUtil.getCRC("01 03 01 2C 00 10"));

            //包尾 4字节
            String tail = str.substring(length * 2 + 8, length * 2 + 16);

            jo.put("head", head);
            jo.put("length", length);
            jo.put("dataType", dataType);
            jo.put("deviceNumber", deviceNumber);
            jo.put("timeStamp", timeStamp);
            jo.put("VehicleleList", VehicleList);
            jo.put("crcStr", crcStr);
            jo.put("tail", tail);
            System.out.println(jo);
            return tail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param bytes 目标字节数组数据
     * @param num   车辆数目
     * @return 解析后的车辆数据model
     * @brief: 解析目标车辆数据
     * @create: 2021/07/05 19:26
     */
    public static void   decodeObjectData_05(byte[] bytes, long timeStamp, Integer num, String deviceNumber, List<Data> out,Map<String,String> redisMap) {
        byte[] id = new byte[2];//1
        byte[] type = new byte[1];//3
        byte[] length = new byte[1];//4
        byte[] width = new byte[1];//5
        byte[] posx = new byte[2];//7
        byte[] posy = new byte[2];//8
        byte[] speedx = new byte[2];//9
        byte[] speedy = new byte[2];//10
        byte[] speed = new byte[2];//11
        byte[] acceleration = new byte[2];//12
        byte[] code = new byte[17];//
        byte[] longitude = new byte[4];//
        byte[] latitude = new byte[4];//
        byte[] headingAngle = new byte[2];//
        double[] getFrenet = new double[5];
//        List vehicleList = new ArrayList<VehicleModel>();
        for (int i = 0; i < num; i++) {
//            VehicleModel vm = new VehicleModel();
            Data vm = new Data();
            //车辆的数据要在这里修改，仅仅提取我们要的数据
            //如果要改成1.0.5协议的话96应该修改为96

            System.arraycopy(bytes, i * 96, code, 0, 17);
            System.arraycopy(bytes, i * 96, id, 0, 2);
            System.arraycopy(bytes, 3 + i * 96, type, 0, 1);
            System.arraycopy(bytes, 4 + i * 96, length, 0, 1);
            System.arraycopy(bytes, 5 + i * 96, width, 0, 1);
            System.arraycopy(bytes, 7 + i * 96, headingAngle, 0, 2);
            System.arraycopy(bytes, 9 + i * 96, posx, 0, 2);
            System.arraycopy(bytes, 11 + i * 96, posy, 0, 2);
            System.arraycopy(bytes, 15 + i * 96, speedx, 0, 2);
            System.arraycopy(bytes, 17 + i * 96, speedy, 0, 2);
            System.arraycopy(bytes, 21 + i * 96, speed, 0, 2);
            System.arraycopy(bytes, 29 + i * 96, acceleration, 0, 2);
            System.arraycopy(bytes, 31 + i * 96, longitude, 0, 4);
            System.arraycopy(bytes, 35 + i * 96, latitude, 0, 4);

            vm.setIp(deviceNumber);
            vm.setTimestamp(timeStamp);
            vm.setId(Integer.valueOf(HUtil.bytes2Hex(id), 16));
            vm.setType(hm2.get(type[0]));
            vm.setLength((float) (Integer.valueOf(HUtil.bytes2Hex(length), 16) * 0.1));
            vm.setWidth((float) (Integer.valueOf(HUtil.bytes2Hex(width), 16) * 0.1));
            vm.setPosx((float) (Integer.valueOf(HUtil.bytes2Hex(posx), 16) * 0.01));  //只是正数的调用
            vm.setPosy((float) (DataUtil.getShort(posy) * 0.01));       //带正负的直接调用
            vm.setSpeedx((float) (DataUtil.getShort(speedx) * 0.01));
            vm.setSpeedy((float) (DataUtil.getShort(speedy) * 0.01));
            vm.setAcceleration((float) (DataUtil.getShort(acceleration) * 0.01));
            vm.setSpeed((float) (Integer.valueOf(HUtil.bytes2Hex(speed), 16) * 0.01));
            //vm.setCode(HUtil.bytes2Hex(code));
            //获取经纬度作为转换成平面或者frenet坐标的参数
            double getLongitude = (DataUtil.getInt(longitude) * Math.pow(10,-7));    //获得的经度
            double getLatitude  = (DataUtil.getInt(latitude) * Math.pow(10,-7)); //获得的维度
            vm.setLongitude(getLongitude);  //加入到对象集合中
            vm.setLatitude(getLatitude);
//            double getMercatorx = CoordiTransfUtil.gpsToPlanex(getLongitude);   //获得转换后的墨卡托坐标
//            double getMercatory = CoordiTransfUtil.gpsToPlaney(getLatitude);
//            vm.setMercatorx(getMercatorx);   //将墨卡托坐标加入集合
//            vm.setMercatory(getMercatory);
            float getHeadingAngle =(float) (Integer.valueOf(HUtil.bytes2Hex(headingAngle), 16) * 0.1);
            vm.setHeadingAngle(getHeadingAngle);//增加航向角
            //通过经纬度获取frenet坐标集合，
//            try {
//            //    getFrenet = CoordiTransfUtil.Mercator2frenetV3(getMercatorx,getMercatory,getHeadingAngle);   //传入墨卡托坐标转换成frenet坐标
//
//            }catch (Exception e){
//                System.out.println("无效数据，转换frenet坐标出错了！");
//            }

//            vm.setFrenetx(getFrenet[0]);
//            vm.setFrenety(getFrenet[1]);
//            vm.setLane((int) getFrenet[3]); //车道
//            vm.setRoadDirect((int) getFrenet[4]);  //左右辐

            String vmJson = JSONObject.toJSONString(vm);
            redisMap.put(String.valueOf(vm.getId()),vmJson); //将实时数据转换成jason字符串
            out.add(vm);
        }

    }

    /**
     *
     * @param bytes 目标字节数组数据
     * @param num   车辆数目
     * @return 解析后的车辆数据model
     * @brief: 解析目标车辆数据
     *04协议的解码函数，数据部分为37字节
     */

    public static void decodeObjectData_04(byte[] bytes, long timeStamp, Integer num, String deviceNumber, List<Data> out,Map<String,String> redisMap, Map<Integer, Long> ids) {
        byte[] id = new byte[2];//1
        byte[] type = new byte[1];//3
        byte[] length = new byte[1];//4
        byte[] width = new byte[1];//5
        byte[] posx = new byte[2];//7
        byte[] posy = new byte[2];//8
        byte[] speedx = new byte[2];//9
        byte[] speedy = new byte[2];//10
        byte[] speed = new byte[2];//11
        byte[] acceleration = new byte[2];//12
        byte[] code = new byte[37];//13
        byte[] longitude = new byte[4]; // 14
        byte[] latitude = new byte[4];  //15
        byte[] headingAngle = new byte[2];  //16
        byte[] laneNumber =new byte[1];//所属车道，如果是ff就不要这个数据



//        List vehicleList = new ArrayList<VehicleModel>();
        for (int i = 0; i < num; i++) {
            // VehicleModel vm = new VehicleModel();
            Data vm = new Data();
            //车辆的数据要在这里修改，仅仅提取我们要的数据
            System.arraycopy(bytes, 2 + i * 37, laneNumber, 0, 1);
//            if(!HUtil.bytes2Hex(laneNumber).equals("ff")){
//                continue;
//            }
            System.arraycopy(bytes, i * 37, code, 0, 37);
            System.arraycopy(bytes, i * 37, id, 0, 2);
            System.arraycopy(bytes, 3 + i * 37, type, 0, 1);
            System.arraycopy(bytes, 4 + i * 37, length, 0, 1);
            System.arraycopy(bytes, 5 + i * 37, width, 0, 1);
            System.arraycopy(bytes, 8 + i * 37, posx, 0, 2);
            System.arraycopy(bytes, 10 + i * 37, posy, 0, 2);
            System.arraycopy(bytes, 12 + i * 37, speedx, 0, 2);
            System.arraycopy(bytes, 14 + i * 37, speedy, 0, 2);
            System.arraycopy(bytes, 16 + i * 37, speed, 0, 2);
            System.arraycopy(bytes, 18 + i * 37, acceleration, 0, 2);
            vm.setIp(deviceNumber);

            vm.setTimestamp(timeStamp);
            int _id = Integer.valueOf(HUtil.bytes2Hex(id), 16);
            vm.setId(_id);
            if (!ids.containsKey(_id) || ids.get(_id) != timeStamp) {
                ids.put(_id, timeStamp);
                vm.setType(hm.get(type[0]));
                vm.setLength((float) (Integer.valueOf(HUtil.bytes2Hex(length), 16) * 0.1));
                vm.setWidth((float) (Integer.valueOf(HUtil.bytes2Hex(width), 16) * 0.1));
                vm.setPosx((float) (Integer.valueOf(HUtil.bytes2Hex(posx), 16) * 0.01));
                vm.setPosy((float) (DataUtil.getShort(posy) * 0.01));
                vm.setSpeedx((float) (DataUtil.getShort(speedx) * 0.01));
                vm.setSpeedy((float) (DataUtil.getShort(speedy) * 0.01));
                vm.setAcceleration((float) (DataUtil.getShort(acceleration) * 0.01));
                vm.setSpeed((float) (Integer.valueOf(HUtil.bytes2Hex(speed), 16) * 0.01));
                System.arraycopy(bytes, 6 + i * 37, headingAngle, 0, 2);
                System.arraycopy(bytes, 24 + i * 37, longitude, 0, 4);
                System.arraycopy(bytes, 28 + i * 37, latitude, 0, 4);
                vm.setLongitude(DataUtil.getInt(longitude) * Math.pow(10, -7));
                vm.setLatitude(DataUtil.getInt(latitude) * Math.pow(10, -7));
                float getHeadingAngle = (float) (Integer.valueOf(HUtil.bytes2Hex(headingAngle), 16) * 0.1);
                vm.setHeadingAngle(getHeadingAngle);//增加航向角
                //vm.setCode(HUtil.bytes2Hex(code));

                double getLongitude = (DataUtil.getInt(longitude) * Math.pow(10, -7));    //获得的经度
                double getLatitude = (DataUtil.getInt(latitude) * Math.pow(10, -7)); //获得的维度
                if (getLongitude == 0 || getLatitude == 0)  //将值为零的数全部丢弃
                    continue;
                vm.setLongitude(getLongitude);  //加入到对象集合中
                vm.setLatitude(getLatitude);
//                double getMercatorx = CoordiTransfUtil.gpsToPlanex(getLongitude);   //获得转换后的墨卡托坐标
//                double getMercatory = CoordiTransfUtil.gpsToPlaney(getLatitude);
//                vm.setMercatorx(getMercatorx);   //将墨卡托坐标加入集合
//                vm.setMercatory(getMercatory);
                //通过经纬度获取frenet坐标集合，
//                try {
                //  double[] getFrenet = CoordiTransfUtil.Mercator2frenetV3(getMercatorx, getMercatory, getHeadingAngle);   //传入墨卡托坐标
                //输出结果ksd[0]为frenetx
                //输出结果ksd[1]为frenety
                //输出结果ksd[2]为沿道路方向的heading
                //输出结果ksd[3]为车道编号
                //输出结果ksd[4]为左右幅，左幅为-1，右幅为1
//                    if (getFrenet[0] == 0)  //将噪点排除
//                        continue;
//                    vm.setFrenetx(getFrenet[0]); //frenetx
//                    vm.setFrenety(getFrenet[1]); //frenety
//                    vm.setFrenetAngle((float) getFrenet[2]);
//                    vm.setLane((int) getFrenet[3]);
//                    vm.setRoadDirect((int) getFrenet[4]);    //
//                    if(getFrenet[2]>90&&getFrenet[2]<270){
//                         vm.setSpeedx((float) (-1* Math.abs(vm.getSpeedx())));
//                    }
//                    else {
//                        vm.setSpeedx((float) Math.abs(vm.getSpeedx()));
//                    }
//                    //确定speedY正负：
//                    if((getFrenet[2]>270&&getFrenet[2]<360)||(getFrenet[2]>90&&getFrenet[2]<180)){
//                        vm.setSpeedy(-1* Math.abs(vm.getSpeedy()));
//                    }
//                    else {
//                        vm.setSpeedy( Math.abs(vm.getSpeedy()));
//                    }

//                } catch (Exception e) {
//                    System.out.println("无效数据，转换frenet坐标时输入出错！");
//                }
                String vmJson = JSONObject.toJSONString(vm);
                redisMap.put(String.valueOf(vm.getId()), vmJson);//将实时数据转换成jason字符串
                out.add(vm);
            }
        }


    }

    /**
     * @param bytes
     * @return long类型的时间戳
     * @brief: 字节数组转时间戳
     * @create: 2021/07/05 14:54
     */
    public static long ByteArr2TimeStamp(byte[] bytes) {
        byte[] byear = new byte[1];
        byte[] bmonth = new byte[1];
        byte[] bday = new byte[1];
        byte[] bhour = new byte[1];
        byte[] bmin = new byte[1];
        byte[] bsec = new byte[1];
        byte[] bms = new byte[2];

        System.arraycopy(bytes, 0, byear, 0, 1);
        System.arraycopy(bytes, 1, bmonth, 0, 1);
        System.arraycopy(bytes, 2, bday, 0, 1);
        System.arraycopy(bytes, 3, bhour, 0, 1);
        System.arraycopy(bytes, 4, bmin, 0, 1);
        System.arraycopy(bytes, 5, bsec, 0, 1);
        System.arraycopy(bytes, 6, bms, 0, 2);

        String year = "20" + Integer.valueOf(HUtil.bytes2Hex(byear), 16).toString();
        String month = Integer.valueOf(HUtil.bytes2Hex(bmonth), 16).toString();
        String day = Integer.valueOf(HUtil.bytes2Hex(bday), 16).toString();
        String hour = Integer.valueOf(HUtil.bytes2Hex(bhour), 16).toString();
        String min = Integer.valueOf(HUtil.bytes2Hex(bmin), 16).toString();
        String sec = Integer.valueOf(HUtil.bytes2Hex(bsec), 16).toString();
        String ms = Integer.valueOf(HUtil.bytes2Hex(bms), 16).toString();

        String DateTime = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec + ":" + ms;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");

        //时间戳转北京时间
        /*
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        Date dd = new Date(ts);
        String time = simpleDateFormat.format(dd);
        System.out.println(time);
        */

        try {
            Date date = sdf.parse(DateTime);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }



}
