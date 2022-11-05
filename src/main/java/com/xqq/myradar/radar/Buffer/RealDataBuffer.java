package com.xqq.myradar.radar.Buffer;


import com.xqq.myradar.radar.Entity.Data;
import com.xqq.myradar.radar.Model.CarplateV3Model;
import com.xqq.myradar.radar.Model.Trajectory;
import com.xqq.myradar.radar.Model.VehicleModel;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.List;

/**
 * 用于定时向数据库写数据的缓冲区
 */
@Slf4j
public class RealDataBuffer {
    private static ArrayList<Data> DataLists = new ArrayList<>();
    private static ArrayList<VehicleModel> VehicleModellists = new ArrayList<>();
    private static ArrayList<Trajectory> trajectories = new ArrayList<>();
    private static ArrayList<CarplateV3Model> carplateV3Modellists = new ArrayList<>();
    private RealDataBuffer(){}

    public static synchronized void addData(List<Data> DataLists) {
        if(DataLists!=null){
            log.info("data数据加载至缓冲区");
        }
        RealDataBuffer.DataLists.addAll(DataLists);
    }
    public static synchronized List<Data>  getData() {
//        System.out.println("从缓冲区取出数据");
        ArrayList<Data> dataArrayList = new ArrayList<>(DataLists);
        DataLists.clear();
        return dataArrayList;
    }


    public static synchronized void addVehicleModel(List<VehicleModel> VehicleModellists) {
        if(VehicleModellists!=null){
            log.info("vehicle数据加载至缓冲区");
        }
        RealDataBuffer.VehicleModellists.addAll(VehicleModellists);
    }
    public static synchronized List<VehicleModel>  getVehicleModel() {
//         System.out.println("从缓冲区取出数据");
        ArrayList<VehicleModel> vehicleModelArrayList= new ArrayList<>(VehicleModellists);
        VehicleModellists.clear();
        return vehicleModelArrayList;
    }

    public static synchronized void addTraj(List<Trajectory> Trajlists) {
        if(trajectories!=null){
            log.info("vehicle数据加载至缓冲区");
        }
        RealDataBuffer.trajectories.addAll(Trajlists);
    }
    public static synchronized List<Trajectory>  getTraj() {
//         System.out.println("从缓冲区取出数据");
        ArrayList<Trajectory> trajectoryList= new ArrayList<>(trajectories);
        trajectories.clear();
        return trajectoryList;
    }

    public static synchronized void addCarplate(CarplateV3Model CarplateV3Models) {
        if(CarplateV3Models!=null){
            log.info("CarplateV3Models数据加载至缓冲区");
        }
        RealDataBuffer.carplateV3Modellists.add(CarplateV3Models);
    }
    public static synchronized List<CarplateV3Model>  getCarplate() {
//        System.out.println("从缓冲区取出数据");
        ArrayList<CarplateV3Model> carplateV3Models = new ArrayList<>(carplateV3Modellists);
        carplateV3Modellists.clear();
        return carplateV3Models;
    }




}
