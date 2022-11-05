package com.xqq.myradar.radar.Service;

import com.xqq.myradar.radar.Dao.DataDao;
import com.xqq.myradar.radar.Entity.Data;
import com.xqq.myradar.radar.Mapper.DataMapper;
import com.xqq.myradar.radar.Utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class DataService {
    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private DataDao dataDao;

    public void insertBatchData(List<Data> item) {
        String tableName = TableUtils.getCurrentTableName();
        dataMapper.insertBatchData(tableName, item);
//        dataDao.insertBatchData(tableName,item);
    }
//    public void insertBatchDataDAO(List<Data> item) {
//        String tableName = TableUtils.getCurrentTableName();
//        dataDao.insertBatchData(tableName, item);
////        dataDao.insertBatchData(tableName,item);
//    }

}
