package com.zxy.client.service;

import com.zxy.client.dao.Account;
import com.zxy.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserReg {
    private JPanel userReg;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JTextField briefText;
    private JButton RegButton;

    //关于数据库操作的对象
    private Account account = new Account();

    //注册页面是要从登录页面启动，不能单独启动
    public UserReg() {
        JFrame frame = new JFrame("用户注册");
        frame.setContentPane(userReg);
        //JFrame.EXIT_ON_CLOSE点X窗口关闭，线程停止
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        //点击注册按钮，将数据写入到数据库中，成功弹出提示框
        RegButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取用户的输入的注册信息
                String userName = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                String brief = briefText.getText();
                //将输入信息包装为User类，保存到数据库中
                User user = new User();
                user.setUserName(userName);
                user.setPassword(password);
                user.setBrief(brief);
                //调用dao对象
                if(account.userReg(user)){
                    //注册成功，弹出提示框，返回登录页面
                    JOptionPane.showMessageDialog(frame,"注册成功",
                            "提示信息",JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(false);
                }else {
                    //注册失败，弹出提示框，保留当前注册页面
                    JOptionPane.showMessageDialog(frame,"注册失败",
                            "错误信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

}
