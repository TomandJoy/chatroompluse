package com.zxy.client.service;

import com.zxy.utils.CommUtils;
import com.zxy.vo.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;

public class GroupChatGUI {
    private JPanel groupPanel;
    private JTextArea readFromServer;
    private JTextField sendToServer;
    private JPanel friendsPanel;
    private JFrame frame;

    private String groupName;
    private Set<String> friends;
    private String myName;
    private ConnetToServer connetToServer;

    public GroupChatGUI(String groupName,
                        Set<String> friends,
                        String myName,
                        ConnetToServer connetToServer){
        this.groupName = groupName;
        this.friends = friends;
        this.myName = myName;
        this.connetToServer = connetToServer;
        frame = new JFrame(groupName);
        frame.setContentPane(groupPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(400,400);
        frame.setVisible(true);

        friendsPanel.setLayout(new BoxLayout(friendsPanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()){
            String labelName = iterator.next();
            JLabel jLabel = new JLabel(labelName);
            friendsPanel.add(jLabel);
        }

        sendToServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb = new StringBuilder();
                sb.append(sendToServer.getText());
                //捕捉enter键
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    String strToServer = sb.toString();
                    //type:4 content:myName-msg to:groupName
                    MessageVO messageVO = new MessageVO();
                    messageVO.setType("4");
                    messageVO.setContent(myName+"-"+strToServer);
                    messageVO.setTo(groupName);
                    try {
                        PrintStream out = new PrintStream(connetToServer.getOut(),true,"UTF-8");
                        out.println(CommUtils.objectToJson(messageVO));
                        System.out.println("客户端发送的群聊信息为:"+messageVO);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }

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
