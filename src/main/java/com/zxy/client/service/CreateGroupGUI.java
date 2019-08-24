package com.zxy.client.service;

import com.zxy.utils.CommUtils;
import com.zxy.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 创建群聊的界面
 */

public class CreateGroupGUI {
    private JPanel CreateGroupPanel;
    private JPanel friendLablePanel;
    private JTextField groupNameText;
    private JButton conformBtn;

    private String myName;
    private Set<String> friends;
    private ConnetToServer connetToServer;
    private friendsList friendsList;

    public CreateGroupGUI(String myName,Set<String> friends,
                          ConnetToServer connetToServer,friendsList friendsList){
        this.myName = myName;
        this.friends = friends;
        this.connetToServer = connetToServer;
        this.friendsList = friendsList;
        JFrame frame = new JFrame("创建群组");
        frame.setContentPane(CreateGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //将在线好友以checkBox展示到界面中
        friendLablePanel.setLayout(new BoxLayout(friendLablePanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()){
            String lableName = iterator.next();
            JCheckBox checkBox = new JCheckBox(lableName);
            friendLablePanel.add(checkBox);
        }
        //点击提交信息按钮将信息提交到服务端


        conformBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断哪些好友被选中加入群聊
                Set<String> selectedFriends = new HashSet<>();
                Component[] comps = friendLablePanel.getComponents();
                for (Component comp:comps) {
                    JCheckBox checkBox = (JCheckBox) comp;
                    if(checkBox.isSelected()){
                        String labelName = checkBox.getText();
                        selectedFriends.add(labelName);
                    }

                }
                selectedFriends.add(myName);
                //2.获取群名输入框，输入群的名称
                String groupName = groupNameText.getText();
                //3.将群名+选中好友信息发送到服务器端
                /**
                 * type:3   content:groupName   to:[user1,user2,user3...]
                 */
                MessageVO messageVO = new MessageVO();
                messageVO.setType("3");
                messageVO.setContent(groupName);
                messageVO.setTo(CommUtils.objectToJson(selectedFriends));
                try {
                    PrintStream out = new PrintStream(connetToServer.getOut(),true,"UTF-8");
                    out.println(CommUtils.objectToJson(messageVO));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                //4.将当前创建群界面隐藏，刷新好友列表界面的群列表
                frame.setVisible(false);
                //addGroupInfo
                //loadGroup
                friendsList.addGroup(groupName,selectedFriends);
                friendsList.loadGroupList();
            }
        });
    }

}
