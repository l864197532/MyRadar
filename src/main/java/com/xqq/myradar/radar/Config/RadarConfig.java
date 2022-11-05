package com.xqq.myradar.radar.Config;


import com.xqq.myradar.radar.Service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 雷达的缓冲区配置和初始化
 */
@Component
public class RadarConfig {

    @Autowired
    TableService newtableService;

    public static TableService tableService;

    public static void init() {
        try {
            tableService.createTodayTable();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostConstruct
    public void create() {
        tableService = newtableService;
    }

}
