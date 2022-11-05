package com.xqq.myradar.task.FileUpload;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class fileUtil {


    //需要上传的文件的存储地址  client
    private static String backupuploadfolder;//"D:\\FileBackup"
    public static String getbackupuploadfolder() { return backupuploadfolder; }
    @Value("${fileupload.backupuploadfolder}")
    public void setbackupuploadfolder(String backupuploadfolder) { fileUtil.backupuploadfolder=backupuploadfolder; }

    //client port
    private static int upport;
    public static int getupport() { return upport; }
    @Value("${fileupload.upport}")
    public void setupport(int upport) { fileUtil.upport=upport; }


    //需要将文件下载到的存储地址  server
    private static String backupdownloadfolder;//"D:\\FileBackup"
    public static String getbackupdownloadfolder() { return backupdownloadfolder; }
    @Value("${fileupload.backupdownloadfolder}")
    public void setbackupdownloadfolder(String backupdownloadfolder) { fileUtil.backupdownloadfolder=backupdownloadfolder; }


    //serve port
    private static int downport;
    public static int getdownport() { return downport; }
    @Value("${fileupload.downport}")
    public void setdownport(int downport) { fileUtil.downport=downport; }

    //服务器server ip
    private static String SERVERIP;
    public static String getSERVERIP() { return SERVERIP; }
    @Value("${fileupload.SERVERIP}")
    public void setSERVERIP(String SERVERIP) { fileUtil.SERVERIP=SERVERIP; }



}
