package com.xqq.myradar.radar.Service;




import com.xqq.myradar.radar.Dao.TableDao;
import com.xqq.myradar.radar.Mapper.TableMapper;
import com.xqq.myradar.radar.Utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    @Autowired
    TableMapper tableMapper;

    @Autowired
    TableDao tableDao;





    public void createTomorrowTable() {
        String[] tableName = TableUtils.getTomorrowTableName();
        for (String s : tableName) {
            tableMapper.createTable(s);
            tableDao.createTable(s);

        }
//        String trajName = TableUtils.getTomorrowTrajTableName();
//        tableMapper.createTrajTable(trajName);
    }


    public void createTodayTable() {
        String[] tableName = TableUtils.getTodayTableName();
        for (String s : tableName) {
            try{
                tableMapper.createTable(s); //创建当天的实时数据表
                tableDao.createTable(s);

                tableMapper.createTrajTable(TableUtils.getTomorrowTrajTableName()); //创建轨迹表

            }catch (Exception e){
//                e.printStackTrace();
            }
            try {
//                tableDao.createTable(s);
            }catch (Exception e){

            }

        }
//        String trajName = TableUtils.getTodayTrajTableName();
//            tableMapper.createTrajTable(trajName);  //创建当天的轨迹融合数据库
    }
}
