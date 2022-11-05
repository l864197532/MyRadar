package com.xqq.myradar.radar.Service;

import com.xqq.myradar.radar.Dao.DataDao;
import com.xqq.myradar.radar.Dao.VehicleModelDao;
import com.xqq.myradar.radar.Model.VehicleModel;
import com.xqq.myradar.radar.Utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleModelService {
    @Autowired
    private VehicleModelDao vehicleModelDao;
        public void insertBatchDataDAO(List<VehicleModel> item) {
        String tableName = TableUtils.getCurrentTableName();
        vehicleModelDao.insertBatch(tableName, item);
    }
}
