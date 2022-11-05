package com.xqq.myradar;

import com.xqq.myradar.radar.Buffer.DevicesBuffer;
import com.xqq.myradar.radar.Dao.DeviceDao;
import com.xqq.myradar.radar.Entity.Device;
import com.xqq.myradar.radar.Entity.LaneInfo;
import com.xqq.myradar.radar.Mapper.LaneInfoMapper;
import com.xqq.myradar.radar.Service.DeviceService;
import com.xqq.myradar.redis.JedisCompoment;
import com.xqq.myradar.task.FileUpload.Client;
import com.xqq.myradar.task.FileUpload.Server;
import com.xqq.myradar.task.FileUpload.fileUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
class MyRadarApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void getDeviceBuffer(){
        DevicesBuffer.getS().select();
        Arrays.toString(DevicesBuffer.getS().deviceList.toArray());

    }


    @Autowired
    DeviceService deviceService;
    @Test
    void DeviceServiceTest(){
        List<Device> devices = deviceService.selectAll();
        System.out.println(Arrays.toString(devices.toArray()));
    }

    @Autowired
    DeviceDao deviceDao;

    @Test
    void DeviceDaoTest(){
        List<Device> devices = deviceDao.selctAll();
        System.out.println(Arrays.toString(devices.toArray()));
    }

    @Test
    void JedisCompomentTest(){
        System.out.println(JedisCompoment.REAL_DATA);
    }

    @Test
    void filedownload() throws IOException {
        new Server().accept();

    }

    @Test
    void fileupload() throws IOException {
        new Client().upload();
    }
    @Test
    void getvalueTest() throws IOException {
        String SERVERIP= fileUtil.getbackupuploadfolder();
        System.out.println(SERVERIP);
    }
    @Autowired
    LaneInfoMapper laneInfoMapper;

    @Test
    void getLanInfo() {
        List<LaneInfo> radarlaneinfo_right = laneInfoMapper.selectAll("radarlaneinfo_right");
        System.out.println(radarlaneinfo_right.size());
    }

}
