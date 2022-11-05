package com.xqq.myradar.radar.Utils;


import com.xqq.myradar.radar.Buffer.*;
import com.xqq.myradar.radar.Entity.LaneInfo;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.cos;

@Slf4j
public class ConvertUtil {

    /**
     * Frenet2UTM
     * @param S 为frenetX
     * @param D 为 frenety
     * @param A 为 道路幅向（右幅为0）
     * @return  double[]{UTMX,UTMY}
     * 从缓冲区找最相近的frenetx,没有利用map结构
     */
    public static double[] Frenet2UTM(double S, double D, int A) {

        double MIN=1000;
        double Q=0;
        int index=0;
        int j=0;
        double X=0;
        double Y=0;
        double theta=0;
        double UTMX=0;
        double UTMY=0;
        double roadAngle=0;
        List<LaneInfo> laneInfoList;
        if(A==1){//1是右幅，2是左幅
            laneInfoList = LaneInfoBufferRight.getS().laneInfoList;
        }else laneInfoList = LaneInfoBufferLeft.getS().laneInfoList;
//        System.out.println("我要从"+(A==1?"右幅":"左幅")+"表中读取数据啦,它一共有"+laneInfoList.size()+"条数据");
        for (int i = 0; i < laneInfoList.size(); i++) {
            Q=S-laneInfoList.get(i).getLocationNumberStart();
            if(MIN>Math.abs(Q)){
                MIN=Q;
                index=i;
            }
        }
//        System.out.println("S:"+S+" D:"+D+" A:"+A+"  index:"+(MIN>0?index:index-1));
        if(MIN>0){
            j=index;
        }else j=index-1;


        X=S- laneInfoList.get(j).getLocationNumberStart();
        Y=D;

        if(A==1){

            theta=Math.abs(laneInfoList.get(j).getDirectionAngle()-270)*Math.PI/180;

            if(laneInfoList.get(j).getDirectionAngle()-90<180){
                UTMX=laneInfoList.get(j).getUTME()-Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfoList.get(j).getUTMN()-Math.sin(theta)*X+Math.cos(theta)*Y;
            }else {
                UTMX=laneInfoList.get(j).getUTME()+Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfoList.get(j).getUTMN()+Math.sin(theta)*X+Math.cos(theta)*Y;
            }
        }
        else {
            theta=Math.abs(laneInfoList.get(j).getDirectionAngle()-90)*Math.PI/180;

            if(laneInfoList.get(j).getDirectionAngle()+90<180){
                UTMX=laneInfoList.get(j).getUTME()+Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfoList.get(j).getUTMN()-Math.sin(theta)*X-Math.cos(theta)*Y;
            }else {
                UTMX=laneInfoList.get(j).getUTME()-Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfoList.get(j).getUTMN()+Math.sin(theta)*X-Math.cos(theta)*Y;

            }
        }
        roadAngle=laneInfoList.get(j).getDirectionAngle();
        return new double[]{UTMX,UTMY,roadAngle};
    }

    /**
     * Frenet2UTMlasted,利用map准确匹配
     * @param S 为frenetX
     * @param D 为 frenety
     * @param A 为 道路幅向（右幅为0）
     * @return  double[]{UTMX,UTMY}
     */
    public static double[] Frenet2UTMlasted(double S, double D, int A) {



        double MIN=1000;
        double Q=0;
        int index=0;
        int j=0;
        double X=0;
        double Y=0;
        double theta=0;
        double UTMX=0;
        double UTMY=0;
        double roadAngle=0;
        Map<Double,LaneInfo> fiberLaneInfoMap;
        if(A==1){//1是右幅，2是左幅
            fiberLaneInfoMap = LaneInfoBufferRight.getS().fiberLaneInfoMap;
        }else fiberLaneInfoMap = LaneInfoBufferLeft.getS().fiberLaneInfoMap;
//        System.out.println("我要从"+(A==1?"右幅":"左幅")+"表中读取数据啦,它一共有"+laneInfoList.size()+"条数据");
//        for (int i = 0; i < laneInfoList.size(); i++) {
//            Q=S-laneInfoList.get(i).getLocationNumberStart();
//            if(MIN>Math.abs(Q)){
//                MIN=Q;
//                index=i;
//            }
//        }


        //找出最匹配历程的静态数据
//        System.out.println("S:"+S+" D:"+D+" A:"+A+"  index:"+(MIN>0?index:index-1));
//        if(MIN>0){
//            j=index;
//        }else j=index-1;

        LaneInfo laneInfo = fiberLaneInfoMap.get(S);
//        System.out.println("匹配到的"+laneInfo.toString());
        X=0;
        Y=D;

        if(A==1){

            theta=Math.abs(laneInfo.getDirectionAngle()-270)*Math.PI/180;

            if(laneInfo.getDirectionAngle()-90<180){
                UTMX=laneInfo.getUTME()-Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfo.getUTMN()-Math.sin(theta)*X+Math.cos(theta)*Y;
            }else {
                UTMX=laneInfo.getUTME()+Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfo.getUTMN()+Math.sin(theta)*X+Math.cos(theta)*Y;
            }
        }
        else {
            theta=Math.abs(laneInfo.getDirectionAngle()-90)*Math.PI/180;

            if(laneInfo.getDirectionAngle()+90<180){
                UTMX=laneInfo.getUTME()+Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfo.getUTMN()-Math.sin(theta)*X-Math.cos(theta)*Y;
            }else {
                UTMX=laneInfo.getUTME()-Math.sin(theta)*Y-Math.cos(theta)*X;
                UTMY=laneInfo.getUTMN()+Math.sin(theta)*X-Math.cos(theta)*Y;

            }
        }
        roadAngle=laneInfo.getDirectionAngle();
        return new double[]{UTMX,UTMY,roadAngle};
    }
    /**
     * gpstoutm
     * @param longitude
     * @param latitude
     * @return double[]{UTME,UTMN}
     */
    public static double[] gpstoutm(double longitude,double latitude) {
        double a=6378.137;
        double e=0.0818192;
        double k0=0.9996;
        double E0=500;
        double N0=0;
        int Zonenum=(int)longitude/6+31;
        double lamda0=(Zonenum-1)*6-180+3;
        lamda0=lamda0*Math.PI/180;
        double phi=latitude*Math.PI/180;
        double lamda=longitude*Math.PI/180;
        double v=1/Math.sqrt(1-e*e*(Math.sin(phi)*Math.sin(phi)));
        double A=(lamda-lamda0)*Math.cos(phi);
        double T=Math.tan(phi)*Math.tan(phi);
        double C=e*e*Math.cos(phi)*Math.cos(phi)/(1-e*e);
        double s=(1-e*e/4-3*Math.pow(e,4)/64-5*Math.pow(e,6)/256)*phi-(3*Math.pow(e,2)/8+3*Math.pow(e,4)/32+45*Math.pow(e,6)/1024)*Math.sin(2*phi)+(15*Math.pow(e,4)/256+45*Math.pow(e,6)/1024)*Math.sin(4*phi)-35*Math.pow(e,6)/3072*Math.sin(6*phi);
        double UTME=(E0+k0*a*v*(A+(1-T+C)*Math.pow(A,3)/6+(5-18*T+T*T)*Math.pow(A,5)/120))*1000; //UTM坐标系下的横向坐标
        double UTMN=(N0+k0*a*(s+v*Math.tan(phi)*(A*A/2+(5-T+9*C+4*C*C)*Math.pow(A,4)/24+(61-58*T+T*T)*Math.pow(A,6)/720)))*1000; //UTM坐标系下的纵向坐标
        return new double[]{UTME,UTMN} ;
    }

    /**
     * UTM2Frenet 用于微波雷达坐标
     * @param utme
     * @param utmn
     * @param angle 角度
     * @param RadarNumber 微波雷达编号
     * @return double[]{S,D,heading,laneNumber} 返回frenetx,frenety,角度，车道号
     */
    public static double[] UTM2Frenet(double utme, double utmn, double angle,String RadarNumber) {

        double MIN=1000;
        int index=0;
        double S=0;
        double D=0;
        double Q=0;
        double h=0;
        double heading=0;
        int j=0;
        int laneNumber=0;
        int flag=0;
        RoadInfoBufferRight roadInfoBufferRight = null;
        RoadInfoBufferLeft roadInfoBufferLeft=null;
        Map<String, List<LaneInfo>> laneInfomap;

        int right =  DevicesBuffer.getS().sellectByip(RadarNumber).getRoadDirect().equals("1")?1:0;
        if(right==1){
            roadInfoBufferRight = RoadInfoBufferRight.getS();
            laneInfomap = LaneInfoBufferRight.getS().laneInfomap;
        }
        else {
            roadInfoBufferLeft = RoadInfoBufferLeft.getS();
            laneInfomap = LaneInfoBufferLeft.getS().laneInfomap;
        }



        List<LaneInfo> laneInfoList=laneInfomap.get(RadarNumber);//找到雷达编号对应的道路数据

        //System.out.println("现在静态表中一共有"+laneInfomap.size()+"个雷达"+" 当前数据对应的雷达共有"+laneInfoList.size()+"条记录");
        //System.out.println("雷达编号"+RadarNumber+"找他的道路表,内有数据"+laneInfoList.size()+"条");
        for (int i = 0; i < laneInfoList.size(); i++) {
            if (laneInfoList.get(i).getRadarId().equals(RadarNumber)){
                Q=Math.pow(laneInfoList.get(i).getUTME()-utme,2)+Math.pow(laneInfoList.get(i).getUTMN()-utmn,2);
                if(MIN>Q){
                    MIN=Q;
                    index=i;
                }
            }
        }

        //System.out.println("找到车车离第 "+index+" 条数据最近");

        //System.out.println(laneInfoList.get(index).toString());
        //System.out.println("");
        S = laneInfoList.get(index).getLocationNumberStart();

        D = Math.sqrt(MIN);
        //如果D大于一定值20，可以把这条数据去除掉
        h = angle-laneInfoList.get(index).getDirectionAngle();

        if(h<0){
            heading = 360 + h;
        }
        else heading = h;

        if(right==1){
            for (int i = 0; i < roadInfoBufferRight.roadInfoList.size(); i++) {
                //如果它的雷达编号符合，并且车辆在这个道路区间上
                if (roadInfoBufferRight.roadInfoList.get(i).getRadarId().equals("0")&&Math.abs(S)>=roadInfoBufferRight.roadInfoList.get(i).getLocationNumberStart()&&Math.abs(S)<=roadInfoBufferRight.roadInfoList.get(i).getLocationNumberEnd()){

                    if(Math.abs(D)>=roadInfoBufferRight.roadInfoList.get(i).getRoadMarginStart()&&Math.abs(D)<=roadInfoBufferRight.roadInfoList.get(i).getRoadMarginEnd()){
                        laneNumber=roadInfoBufferRight.roadInfoList.get(i).getLaneNumber();
                        flag=1;
                        //log.warn("frenetx"+S+"frenety"+D+"找到合适道路"+laneNumber);
                        break;
                    }
                }
            }
        }
        else {
            for (int i = 0; i < roadInfoBufferLeft.roadInfoList.size(); i++) {
                //如果它的雷达编号符合，并且车辆在这个道路区间上
                if (roadInfoBufferLeft.roadInfoList.get(i).getRadarId().equals("0")&&Math.abs(S)>=roadInfoBufferLeft.roadInfoList.get(i).getLocationNumberStart()&&Math.abs(S)<=roadInfoBufferLeft.roadInfoList.get(i).getLocationNumberEnd()){

                    if(Math.abs(D)>=roadInfoBufferLeft.roadInfoList.get(i).getRoadMarginStart()&&Math.abs(D)<=roadInfoBufferLeft.roadInfoList.get(i).getRoadMarginEnd()){
                        laneNumber=roadInfoBufferLeft.roadInfoList.get(i).getLaneNumber();
                        flag=1;
                        //log.warn("frenetx"+S+"frenety"+D+"找到合适道路"+laneNumber);
                        break;
                    }
                }
            }
        }
        if(flag==0||laneNumber==0){
//            System.out.println("微波雷达"+RadarNumber+"frenetx"+S+"frenety"+D+"找不到合适道路线形");
            log.warn("雷达"+RadarNumber+"frenetx"+S+"frenety"+D+"找不到合适道路线形");
        }
        return new double[]{S,D,heading,laneNumber};
    }

    /**
     * utmtogps
     * @param utmX
     * @param utmY
     * @return double[]{longitude,latitude}
     */
    public static double[] utmtogps(double utmX,double utmY) {

        double diflat = 0.000000400372863181963;
        double diflon = -0.0000000688086842459646;
        double zone = 50;
        double e=2.718281828;
        double c_sa = 6378137.000000;
        double c_sb = 6356752.314245;
        double e2 = Math.pow(c_sa*c_sa- c_sb*c_sb,0.5)/ c_sb;
        double e2cuadrada = e2*e2;
        double c = c_sa*c_sa/ c_sb;
        double x = utmX-500000;
        double y =  utmY;
        double s = (zone * 6.0) - 183.0;
        double lat = y / (c_sa * 0.9996);
        double v = c/ Math.pow(1 +e2cuadrada * cos(lat)* cos(lat),0.5)*0.9996;
        double a = x / v;
        double a1 = Math.sin(2*lat);
        double a2 = a1 * cos(lat)* cos(lat);
        double j2 = lat + (a1/2);
        double j4 = (3*j2+ a2) / 4.0;
        double j6 = (5*j4 + Math.pow(a2 *cos(lat),2))/ 3.0;
        double alfa = (3.0 / 4.0) * e2cuadrada;
        double beta = (5.0 / 3.0) *alfa*alfa;
        double gama = (35.0 / 27.0) * alfa*alfa*alfa;
        double bm = 0.9996 * c * (lat - alfa * j2 + beta * j4 - gama * j6);
        double b = (y - bm) / v;
        double epsi = (e2cuadrada * a*a) / 2.0* cos(lat)*cos(lat);
        double eps = a * (1 - (epsi / 3.0));
        double nab = (b * (1 - epsi)) + lat;
        double senoheps = (Math.pow(e,eps)- Math.pow(e,(-eps))) / 2.0;
        double delt = Math.atan(senoheps/cos(nab));
        double tao = Math.atan(cos(delt)*Math.tan(nab));
        double longitude = ((delt * (180.0 / Math.PI)) + s)+diflon;
        double latitude = ((lat + (1 + e2cuadrada * cos(lat)*cos(lat) - (3.0 / 2.0) * e2cuadrada * Math.sin(lat) * cos(lat) * (tao - lat)) * (tao - lat)) * (180.0 /Math.PI)+diflat);
        return new double[]{longitude,latitude};
    }

    /**
     * 根据道路编号转成frenety
     * @param laneNumber
     * @return
     */
    public static double laneNumber2frenetY(int laneNumber){
        if(laneNumber==1) return 3.75/2;
        if(laneNumber==2) return 3.75/2+3.75;
        if(laneNumber==3) return 3.75/2+3.75*2;
        if(laneNumber==9) return 3.0/2+3.75*3;
        if(laneNumber==4) return 3.0/2+3.75*3;
        return 0;
    }

    public static double frenetY2laneNumber(int laneNumber){
        if(laneNumber==1) return 3.75/2;
        if(laneNumber==2) return 3.75/2+3.75;
        if(laneNumber==3) return 3.75/2+3.75*2;
        if(laneNumber==9) return 3.0/2+3.75*3;
        if(laneNumber==4) return 3.0/2+3.75*3;
        return 0;
    }

    /**
     * 用于激光雷达坐标转换
     * @param utme
     * @param utmn
     * @param angle
     * @param RadarNumber
     * @return
     */
    public static double[] LaserUTM2Frenet(double utme, double utmn, double angle,String RadarNumber) {

        double MIN=100000;
        int index=0;
        double S=0;
        double D=0;
        double Q=0;
        double h=0;
        double heading=0;
        int j=0;
        int laneNumber=0;
        int flag=0;
        double p=0;
        String part="";
        LaserRoadInfoBuffer laserroadInfoBuffer =LaserRoadInfoBuffer.getS();

        Map<String, List<LaneInfo>> laneInfomap;

        if(RadarNumber.equals("30002")){//右幅
            laneInfomap = LaserLaneInfoBufferRight.getS().laneInfomap;
            if(utme>=308887.953571532&&utme<309201.86016944){
                part=RadarNumber+"-1";
            }
            if(utme>=308559.511848479&&utme<308887.953571532){
                part=RadarNumber+"-2";
            }
            if(utme>=308255.045423838&&utme<308559.511848479){
                part=RadarNumber+"-3";
            }
        }
        else {
            laneInfomap = LaserLaneInfoBufferLeft.getS().laneInfomap;
            if(utme>=307929.529519366&&utme<308229.557675297){
                part=RadarNumber+"-1";
            }
            if(utme>=307628.182446835&&utme<307929.529519366){
                part=RadarNumber+"-2";
            }
            if(utme>=307331.957466198&&utme<307628.182446835){
                part=RadarNumber+"-3";
            }
            if(utme>=306997.31531719&&utme<307331.957466198){
                part=RadarNumber+"-4";
            }
        }
        System.out.println("utme: "+utme+" part:"+part);
        List<LaneInfo> laneInfoList=laneInfomap.get(part);//找到雷达编号对应的道路数据
//        System.out.println("现在静态表中一共有"+laneInfomap.size()+"个雷达"+" 当前数据对应的雷达"+part+"共有"+laneInfoList.size()+"条记录");

        for (int i = 0; i < laneInfoList.size(); i++) {
            if (laneInfoList.get(i).getRadarId().equals(part)){
                Q=Math.pow(laneInfoList.get(i).getUTME()-utme,2)+Math.pow(laneInfoList.get(i).getUTMN()-utmn,2);
                if(MIN>Q){
                    MIN=Q;
                    index=i;
                }
            }
        }
//        System.out.println("找到车车离第 "+index+" 条数据最近");
//        System.out.println(laneInfoList.get(index).toString());

        S = laneInfoList.get(index).getLocationNumberStart();
        D = Math.sqrt(MIN);
        h = angle-laneInfoList.get(index).getDirectionAngle();

        if(h<0){
            heading = 360 + h;
        }
        else heading = h;

        for (int i = 0; i < laserroadInfoBuffer.roadInfoList.size(); i++) {
            //如果它的雷达编号符合，并且车辆在这个道路区间上
            if (Math.abs(S)>=laserroadInfoBuffer.roadInfoList.get(i).getLocationNumberStart()&&Math.abs(S)<=laserroadInfoBuffer.roadInfoList.get(i).getLocationNumberEnd()){
                if(Math.abs(D)>=laserroadInfoBuffer.roadInfoList.get(i).getRoadMarginStart()&&Math.abs(D)<=laserroadInfoBuffer.roadInfoList.get(i).getRoadMarginEnd()){
                    laneNumber=laserroadInfoBuffer.roadInfoList.get(i).getLaneNumber();
                    break;
                }
            }
        }
        return new double[]{S,D,heading,laneNumber};
    }
    public static long[] getTimeRangeFromDate(long TodayTimeStamp) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date(TodayTimeStamp);
        String time = simpleDateFormat.format(date1);//时间格式
        String day = time.substring(0, 10);
        String s = day + " 00:00:00";
        Date parse = simpleDateFormat.parse(s);
        long timeresult= parse.getTime();
        return new long[]{timeresult,timeresult+86400*1000};
    }
    public static String stampToDate(String s){//时间戳转换时间
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String dateToStamp(String s) throws ParseException {//时间转时间戳
        String res;
        //设置时间模版
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }





    public static String unicodeDecode(String string) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }
    public static String formatWithMakingUp(String src) {
        String FORMAT_STRING="0000";
        if (null == src) {
            return null;
        }
        int delta = FORMAT_STRING.length() - src.length();
        if (delta <= 0) {
            return src;
        }
        return FORMAT_STRING.substring(0, delta) + src;
    }
}
