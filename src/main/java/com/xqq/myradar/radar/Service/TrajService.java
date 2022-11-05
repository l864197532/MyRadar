//package com.xqq.myradar.radar.Service;
//
//import com.example.WebApplication.Entity.Trajectory;
//import com.example.WebApplication.Mapper.TrajMapper;
//import com.example.WebApplication.TDMapper.TDGetTrajDataMapper;
//import com.example.WebApplication.Utils.TableUtils;
//import com.xqq.myradar.radar.Mapper.TrajMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//@Service
//public class TrajService {
//
//    @Autowired
//    TrajMapper trajMapper;
//
//    @Autowired
//    TDGetTrajDataMapper tdGetTrajDataMapper;
//
//    public List<Trajectory> selectByTimeAndId(String CarId, long timestart, long timeend) {
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String table = simpleDateFormat.format(new Date(timestart));
//        String tableName = TableUtils.getTodayTrajTableName();
//        //System.out.println("CarId====>" + CarId);
//        CarId = "'" + CarId + "'";
//        List<Trajectory>trajectoryList=tdGetTrajDataMapper.getTrajByTimeAndCarid(timestart,timeend,CarId,tableName);
//        return trajectoryList;
//    }
//
//    public List<Trajectory> selectByTimeAndTrajId(long trajId, long timestart, long timeend) {
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//        String table = simpleDateFormat.format(new Date(timestart));
//
//        String tableName = TableUtils.getTodayTrajTableName();
//       // System.out.println("trajId====>" + trajId);
//
//        List<Trajectory>trajectoryList=tdGetTrajDataMapper.getTrajByTimeAndTrajid(timestart,timeend,trajId,tableName);
//        return trajectoryList;
//    }
//
//
//}
