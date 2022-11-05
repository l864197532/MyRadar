package com.xqq.myradar.task.Timely;

import com.xqq.myradar.radar.Buffer.RealDataBuffer;
import com.xqq.myradar.radar.Dao.DataDao;
import com.xqq.myradar.radar.Entity.Data;
import com.xqq.myradar.radar.Mapper.CarplateV3ModelMapper;
import com.xqq.myradar.radar.Mapper.TrajMapper;
import com.xqq.myradar.radar.Model.CarplateV3Model;
import com.xqq.myradar.radar.Model.Trajectory;
import com.xqq.myradar.radar.Model.VehicleModel;
import com.xqq.myradar.radar.Service.DataService;
import com.xqq.myradar.radar.Service.VehicleModelService;
import com.xqq.myradar.radar.Utils.TableUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class TimelyBufferToMysql {
    @Autowired
    DataService service;

    //{秒数} {分钟} {小时} {日期} {月份} {星期} {年份(可为空)}

    @Autowired
    VehicleModelService vehicleModelService;


    @Autowired
    TrajMapper trajMapper;

    @Autowired
    private CarplateV3ModelMapper carplateV3ModelMapper;

    /**
     * 定时将缓冲区数据写入数据库
     */
    @Scheduled(cron = "*/2 * * * * ?")
    @Async
    void getMsgFromJMS()
    {
        List<Data> dataList = RealDataBuffer.getData();
        List<VehicleModel> vehicleModelList = RealDataBuffer.getVehicleModel();
        List<Trajectory> trajLists = RealDataBuffer.getTraj();
        List<CarplateV3Model> carplateV3ModelList = RealDataBuffer.getCarplate();
        if (dataList.size() > 0) {
            service.insertBatchData(dataList);

        }
        if (vehicleModelList.size() > 0) {
            vehicleModelService.insertBatchDataDAO(vehicleModelList);

        }
        if (trajLists.size() > 0) {
            String todayTrajTableName = TableUtils.getTodayTrajTableName();
            trajMapper.insertBatch(todayTrajTableName,trajLists);

        }
        if (carplateV3ModelList.size() > 0) {
            String carplateTable = TableUtils.getCarplateTable();
            carplateV3ModelMapper.insertBatch(carplateTable,carplateV3ModelList);

        }

    }

}
