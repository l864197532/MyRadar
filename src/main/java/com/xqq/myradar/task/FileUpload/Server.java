package com.xqq.myradar.task.FileUpload;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {

    public void accept()throws IOException {
        int port=fileUtil.getdownport();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("服务器"+port+"已开启等待连接....");
        int i = 0;
        while (true) {
            Socket accept = serverSocket.accept();
            System.out.println("socket建立连接");
             String backupdownloadfolder=fileUtil.getbackupdownloadfolder();
            new UploadThread(accept,backupdownloadfolder).start();
        }
    }
}
