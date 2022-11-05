package com.xqq.myradar.radar.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TableUtils {

    //创建了实时数据的数据库名
    public static String[] getTodayTableName() {
        Date date = new Date();//取时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String day = "data" + "_" + sdf.format(date) + "_";
        String[] ret = {day + "0", day + "1", day + "2", day + "3", day + "4", day + "5"};
        return ret;
    }
    //创建处理后的数据的数据库名
    public static String[] getTodayHandleTableName() {
        Date date = new Date();//取时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String day = "dataHandle" + "_" + sdf.format(date) + "_";
        String[] ret = {day + "0", day + "1", day + "2", day + "3", day + "4", day + "5"};
        return ret;
    }
    public static String[] getTomorrowTableName() {
        Date date = new Date();//取时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //把日期往后增加一天.整数往后推,负数往前移动(1:表示明天、-1：表示昨天，0：表示今天)
        calendar.add(Calendar.DATE, 1);

        //这个时间就是日期往后推一天的结果
        date = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String day = "data" + "_" + formatter.format(date) + "_";
        String[] ret = {day + "0", day + "1", day + "2", day + "3", day + "4", day + "5"};
        return ret;
    }

    public static String getCurrentTableName() {
        Date now = new Date();
        StringBuilder sb = new StringBuilder("data_");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat hoursdf = new SimpleDateFormat("HH");
        String suffix = String.valueOf(Integer.parseInt(hoursdf.format(now)) / 4);
        sb.append(sdf.format(now)).append("_").append(suffix);
        return sb.toString();
    }

    public static String getTableNameFromLong(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat hoursdf = new SimpleDateFormat("HH");
        String suffix = String.valueOf(Integer.parseInt(hoursdf.format(date)) / 4);
        StringBuilder sb = new StringBuilder("data_");
        sb.append(sdf.format(date)).append("_").append(suffix);
        return sb.toString();
    }

    /**
     *
     * @return获取轨迹融合后的当天的数据表
     */
    public static String getTodayTrajTableName() {
        Date date = new Date();//取时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String day = "traj" + "_" + sdf.format(date) ;
        return day;
    }
    public static String getTomorrowTrajTableName() {
        Date date = new Date();//取时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //把日期往后增加一天.整数往后推,负数往前移动(1:表示明天、-1：表示昨天，0：表示今天)
        calendar.add(Calendar.DATE, 1);

        //这个时间就是日期往后推一天的结果
        date = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String day = "traj" + "_" + formatter.format(date) ;
        return day;
    }
    public static String getTodaySectionName(){
        return todayName("section");
    }
    public static String todayName(String str) {
        Date date = new Date();//取时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String day = str + "_" + sdf.format(date) ;
        return day;
    }
    public static void main(String[] args) {
        String[] a = getTodayTableName();
        for (String s : a) {
            System.out.println(s);
        }

    }
    public static String getCarplateTable() {
        return "carplatev3model";
    }
}
