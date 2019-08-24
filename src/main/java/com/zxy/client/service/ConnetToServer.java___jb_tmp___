package com.zxy.client.service;

import com.zxy.utils.CommUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

public class ConnetToServer {
    private static final String IP;
    private static final int PORT;
    static {
        Properties pros = CommUtils.loadProperities("socket.properties");
        IP = pros.getProperty("address");
        PORT = Integer.parseInt(pros.getProperty("port"));
    }
    private Socket client;
    private InputStream in;
    private OutputStream out;
    public ConnetToServer() {
        try {
            client = new Socket(IP,PORT);
            in = client.getInputStream();
            out = client.getOutputStream();
        } catch (IOException e) {
            System.out.println("与服务器建立连接失败...");
            e.printStackTrace();
        }

    }
    public InputStream getIn(){
        return in;
    }
    public OutputStream getOut(){
        return out;
    }
}
