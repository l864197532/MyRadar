package com.xqq.myradar.task.FileUpload;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Client {

    public void upload()throws IOException {
        String SERVERIP=fileUtil.getSERVERIP();
        int port=fileUtil.getupport();
        String backupuploadfolder=fileUtil.getbackupuploadfolder();

        Socket socket = new Socket(SERVERIP, port);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time =df.format(System.currentTimeMillis());
        System.out.println(time+" 连接"+SERVERIP+":"+port+"成功，正在上传...");

        Calendar cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   -1);
        String yesterday = new SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());

        //获取通道输出流
        DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
//        long oneDayMillis =  24*60*60*1000;
//        long yesterdayMills = System.currentTimeMillis()-oneDayMillis;
        String fileName = new SimpleDateFormat("yyyyMMdd").format(cal.getTime())+".zip";
//        String filePath = "E:\\TrashFile\\upload\\";
        String filePath = backupuploadfolder+"\\";
        FileInputStream fin = new FileInputStream(filePath + fileName);
        byte[] b = new byte[1024];
        int i;
        while((i=fin.read(b))!=-1){
            outStream.write(b);
        }
        fin.close();
        socket.shutdownOutput();

        InputStream in = socket.getInputStream();
        byte[] bytes = new byte[1024 * 8];
        int len = in.read(bytes);
        String s = new String(bytes, 0, len);
        System.out.println(s);
        socket.close();
    }
}
