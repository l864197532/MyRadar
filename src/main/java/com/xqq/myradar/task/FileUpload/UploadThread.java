package com.xqq.myradar.task.FileUpload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.xqq.myradar.task.FileUpload.ZipUtil.unzip;

public class UploadThread extends Thread{

    private Socket accept;
    private String backupdownloadfolder;
    public UploadThread(Socket accept, String backupdownloadfolder) {
        this.accept=accept;
        this.backupdownloadfolder=backupdownloadfolder;
    }

    @Override
    public void run() {
        try {

            SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
            Calendar cal   =   Calendar.getInstance();
            cal.add(Calendar.DATE,   -1);
            String yesterday = new SimpleDateFormat( "yyyyMMdd").format(cal.getTime());

            String hostAddress = accept.getInetAddress().getHostAddress();//获取主机ip
            String path= backupdownloadfolder+"\\"+yesterday+"\\";



//            String path= "E:\\TrashFile\\accept\\"+yesterday+"\\";
            String fileName = hostAddress+"_"+df2.format(cal.getTime())+".zip";
            File dir = new File(path);
            if(!dir.exists()) {
                dir.mkdirs();//创建目录
            }

            String fileAcceptTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
            System.out.println(fileAcceptTime+" 客户端地址为"+hostAddress+"发送来数据");
            DataInputStream inStream = new DataInputStream(accept.getInputStream());

            FileOutputStream fout = new FileOutputStream(path +fileName);
            try{
                byte[] b = new byte[1024];
                int i;
                while((i=inStream.read(b))!=-1){
                    fout.write(b);
                }
                fout.flush();
                fout.close();
            }catch(Exception e){
                System.out.println(e.toString());
            }

            String fileAcceptEndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
            System.out.println(fileAcceptEndTime+" 客户端地址为"+hostAddress+"数据发送完毕");


            //服务端反馈
            OutputStream out = accept.getOutputStream();
            String fileEndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
            out.write((fileEndTime+" 上传成功").getBytes());

            File unzipandload = new File(path+hostAddress);
            if(!unzipandload.exists()) {
                unzipandload.mkdirs();//创建目录
            }
            System.out.println(fileEndTime+" 准备解压缩文件"+fileName);
            unzip(path +fileName,unzipandload+"\\");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
