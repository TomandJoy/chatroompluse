package com.zxy.server;

import com.zxy.utils.CommUtils;
import com.zxy.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 聊天室的服务端
 */

public class MultiThreadServer {
    private static final String IP;
    private static final int PORT;
    // 缓存当前服务器所有在线的客户端信息
    private static Map<String, Socket> clients = new ConcurrentHashMap<>();
    // 缓存当前服务器注册的所有群名称以及群好友
    private static Map<String,Set<String>> groups = new ConcurrentHashMap<>();
    
    static {
        Properties pros = CommUtils.loadProperities("socket.properties");
        IP = pros.getProperty("address");
        PORT = Integer.parseInt(pros.getProperty("port"));
    }

    private static class ExecutClient implements Runnable{
        private Scanner in;
        private PrintStream out;
        private Socket client;
        public ExecutClient(Socket client){
            this.client = client;
            try {
                //获取客户端的输入流
                this.in = new Scanner(client.getInputStream());
                //打印输出流到客户端，缓冲区自动刷新，设置编码是UTF-8
                this.out = new PrintStream(client.getOutputStream(),true,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            while (true){
                if(in.hasNextLine()){
                    //从客户端发来的是json字符串
                    String jsonStrFromClient = in.nextLine();
                    //MessageVO是服务器与客户端传递消息载体，type  content  to
                    //将json转为object
                    MessageVO msgFromClient = (MessageVO) CommUtils.jsonToObject(jsonStrFromClient,MessageVO.class);
                    //1.新用户注册到服务端  比如用户1注册
                    if(msgFromClient.getType().equals("1")){
                        String userName = msgFromClient.getContent();
                        //2.将当前在线的所有用户名发回客户端，比如现在有三个用户在线，将这三个用户的用户名发回给用户1--更新自己的好友列表
                        MessageVO msgToClient = new MessageVO();
                        msgToClient.setType("1");
                        msgToClient.setContent(CommUtils.objectToJson(clients.keySet()));
                        out.println(CommUtils.objectToJson(msgToClient));
                        //3.将新上线的用户信息发给所有已在线的用户，比如将用户1的信息发给其他三个在线用户--跟新其他用户的好友列表
                        sendUserLogin("newLogin:"+userName);
                        //4.将当前新用户注册到服务器端缓存
                        clients.put(userName,client);
                        System.out.println(userName+"上线了~");
                        System.out.println("当前聊天室共有"+clients.size()+"人");
                    }else if (msgFromClient.getType().equals("2")){
                        /**
                         * 用户私聊  type:2  content:myName-msg  To:friendName
                         */
                        String friendName = msgFromClient.getTo();
                        Socket clientSocket = clients.get(friendName);
                        try {
                            PrintStream out = new PrintStream(clientSocket.getOutputStream(),true,
                                    "UTF-8");
                            MessageVO msgToClient = new MessageVO();
                            msgToClient.setType("2");
                            msgToClient.setContent(msgFromClient.getContent());
                            System.out.println("收到私聊信息，内容为"+msgFromClient.getContent());
                            out.println(CommUtils.objectToJson(msgToClient));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if (msgFromClient.getType().equals("3")){
                        //注册群
                        String groupName = msgFromClient.getContent();
                        //该群的所有群成员
                        Set<String> friends = (Set<String>) CommUtils.jsonToObject(msgFromClient.getTo(),
                                Set.class);
                        groups.put(groupName,friends);
                        System.out.println("有新的群注册成功，群名称为:"+groupName+",共有"+groups.size()
                        +"个群");
                    }else if(msgFromClient.getType().equals("4")){
                        //群聊信息
                        System.out.println("服务器收到的群聊消息为:"+msgFromClient);
                        String groupName = msgFromClient.getTo();
                        Set<String> names = groups.get(groupName);
                        Iterator<String> iterator = names.iterator();
                        while (iterator.hasNext()){
                            String socketName = iterator.next();
                            Socket client = clients.get(socketName);
                            try {
                                PrintStream out = new PrintStream(client.getOutputStream(),true,
                                        "UTF-8");
                                MessageVO messageVO = new MessageVO();
                                messageVO.setType("4");
                                messageVO.setContent(msgFromClient.getContent());
                                //群名-[1,2,3...]
                                messageVO.setTo(groupName+"-"+CommUtils.objectToJson(names));
                                out.println(CommUtils.objectToJson(messageVO));
                                System.out.println("服务端发送的群聊消息为:"+messageVO);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
        /**
         * 向所有在线用户发送新用户的上线信息
         */
        private void sendUserLogin(String msg){
            for (Map.Entry<String,Socket> entry:clients.entrySet()) {
                Socket socket = entry.getValue();
                try {
                    PrintStream out = new PrintStream(socket.getOutputStream(),true,"UTF-8");
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            System.out.println("等待客户端连接...");
            Socket client = serverSocket.accept();
            System.out.println("有新的连接，端口号为"+client.getPort());
            executorService.submit(new ExecutClient(client));
        }
    }
}
