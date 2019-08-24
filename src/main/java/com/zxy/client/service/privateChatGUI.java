package com.zxy.client.service;

import com.zxy.utils.CommUtils;
import com.zxy.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class privateChatGUI {
    private JPanel privateChatPanel;
    private JTextArea readFromServer;
    private JTextField sendToServer;

    private String friendName;
    private String myName;
    private ConnetToServer connetToServer;
    private JFrame frame;
    private PrintStream out;

    public privateChatGUI(String friendName,String myName,
                          ConnetToServer connetToServer){
        this.friendName = friendName;
        this.connetToServer = connetToServer;
        this.myName = myName;
        try {
            this.out = new PrintStream(connetToServer.getOut(),true,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        frame = new JFrame("与"+friendName+"私聊中...");
        frame.setContentPane(privateChatPanel);
        //设置窗口关闭的操作，将其设置为隐藏
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);

        //捕捉输入框的键盘输入
        sendToServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(sendToServer.getText());
                //1.当按下enter键时
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    //2.将当前信息发送到服务端
                    String msg = sb.toString();
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("2");
                    messageVO.setContent(myName+"-"+msg);
                    messageVO.setTo(friendName);
                    privateChatGUI.this.out.println(CommUtils.objectToJson(messageVO));
                    //3.将自己发送的信息展示到当前私聊界面
                    readFromServer(myName+"说:"+msg);
                    sendToServer.setText("");
                }
            }
        });
    }
    public void readFromServer(String msg){
        readFromServer.append(msg+"\n");
    }
    public JFrame getFrame(){
        return frame;
    }
}
