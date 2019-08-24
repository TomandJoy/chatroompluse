package com.zxy.client.service;

import com.zxy.client.dao.Account;
import com.zxy.client.entity.User;
import com.zxy.utils.CommUtils;
import com.zxy.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

public class userLogin {
    private JPanel userLogin;
    private JPanel qqPanel;
    private JLabel picture;
    private JPanel usernPanel;
    private JLabel userNameLable;
    private JTextField userNameText;
    private JButton registButton;
    private JButton loginButton;
    private JLabel passwordLable;
    private JPasswordField passwordText;
    private JFrame frame;

    private Account account = new Account();
    public userLogin() {
        frame = new JFrame("用户登录");
        frame.setContentPane(userLogin);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        //注册按钮
        registButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出注册页面
                new UserReg();
            }
        });

        /**
         * 点击登录按钮：
         * 1.校验用户输入信息
         * 2.
         * 如果登录失败，提示用户重新输入信息
         * 如果登录成功，需要与服务器建立连接，将用户名与用户的socket注册到服务器端，服务端同时发回当前所有在线信息，
         * 加载用户列表页面，监听服务端发来的用户上线信息更新用户列表
         * @param args
         */
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //校验用户信息，先获取到userName,password
                String userName = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                  //校验
                User user = account.userLogin(userName,password);
                if(user !=null){
                    //成功，加载用户列表
                    JOptionPane.showMessageDialog(frame,"登录成功","提示信息",
                            JOptionPane.INFORMATION_MESSAGE);
                    //登录成功，登录页面消失
                    frame.setVisible(false);
                    //与服务器建立连接，将当前用户的用户名与密码发送到服务端
                    ConnetToServer connetToServer = new ConnetToServer();
                    MessageVO msgToServer = new MessageVO();
                    msgToServer.setType("1");
                    msgToServer.setContent(userName);
                    String jsonToServer = CommUtils.objectToJson(msgToServer);

                    try {
                        PrintStream out = new PrintStream(connetToServer.getOut(),true,
                                "UTF-8");
                        out.println(jsonToServer);
                        //读取服务器发回的所有在线用户信息
                        Scanner in = new Scanner(connetToServer.getIn());
                        if(in.hasNextLine()){
                            String msgFromServerStr = in.nextLine();
                            MessageVO msgFromServer = (MessageVO) CommUtils.jsonToObject(msgFromServerStr,MessageVO.class);
                            Set<String> users = (Set<String>) CommUtils.jsonToObject(msgFromServer.getContent(),Set.class);
                            System.out.println("所有在线用户为:"+users);
                            //加载用户列表界面
                            //将当前用户名，所有在线好友，与服务器建立连接传递到好友列表界面
                            new friendsList(userName,users,connetToServer);
                        }
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    //失败，停留在当前登录页面，提示用户信息错误
                    JOptionPane.showMessageDialog(frame,"登录失败","错误信息",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });

    }



    public static void main(String[] args) {
        userLogin userLogin = new userLogin();
    }
}
