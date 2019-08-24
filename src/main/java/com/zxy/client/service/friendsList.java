package com.zxy.client.service;

import com.zxy.utils.CommUtils;
import com.zxy.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class friendsList {
    private JPanel friendsPanel;
    private JScrollPane friendsList;
    private JScrollPane groupListPanel;
    private JButton creatGroupButton;
    private JFrame frame;

    private String userName;
    //存储所有在线好友
    private Set<String> users;
    private ConnetToServer connetToServer;
    //存储私聊界面
    private Map<String, privateChatGUI> privateChatGUIList = new ConcurrentHashMap<>();
    //存储所有群名称以及群好友
    private Map<String,Set<String>> groupList = new ConcurrentHashMap<>();
    // 缓存所有群聊界面
    private Map<String,GroupChatGUI> groupChatGUIList = new ConcurrentHashMap<>();
    // 好友列表后台任务，不断监听服务器发来的信息
    // 好友上线信息、用户私聊、群聊
    private class DaemonTask implements Runnable{
        private Scanner in = new Scanner(connetToServer.getIn());
        @Override
        public void run() {
            while (true){
                //收到服务器发来的消息
                if(in.hasNextLine()){
                    String strFromServer = in.nextLine();
                    if(strFromServer.startsWith("{")){
                        //将json转为object
                        MessageVO messageVO = (MessageVO) CommUtils.jsonToObject(strFromServer,MessageVO.class);
                        if(messageVO.getType().equals("2")){
                            //服务器发来的私聊信息
                            String friendName = messageVO.getContent().split("-")[0];
                            String msg = messageVO.getContent().split("-")[1];
                            //判断此私聊是否是第一次创建
                            if(privateChatGUIList.containsKey(friendName)){
                                privateChatGUI privateChatGUI = privateChatGUIList.get(friendName);
                                privateChatGUI.getFrame().setVisible(true);
                                privateChatGUI.readFromServer(friendName+"说:"+msg);
                            }else {
                                privateChatGUI privateChatGUI = new privateChatGUI(friendName,
                                        userName,connetToServer);
                                privateChatGUIList.put(friendName,privateChatGUI);
                                privateChatGUI.readFromServer(friendName+"说"+msg);
                            }
                        }
                        else if (messageVO.getType().equals("4")){
                            /**
                             * 收到服务器发来的群聊信息  type:4 content:sender-msg to:groupName-[1,2,3...]
                             */
                            String groupName = messageVO.getTo().split("-")[0];
                            String senderName = messageVO.getContent().split("-")[0];
                            String groupMsg = messageVO.getContent().split("-")[1];
                            //若此名称在群聊列表
                            if(groupList.containsKey(groupName)){
                                if(groupChatGUIList.containsKey(groupName)){
                                    //群聊界面弹出
                                    GroupChatGUI groupChatGUI = groupChatGUIList.get(groupName);
                                    groupChatGUI.getFrame().setVisible(true);
                                    groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                                }else {
                                    Set<String> names = groupList.get(groupName);
                                    GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,names,
                                            userName,connetToServer);
                                    groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                                }
                            }else {
                                //若群成员第一次收到群消息
                                //1.将群名称以及群成员保存到当前客户端群聊列表
                                Set<String> friends = (Set<String>) CommUtils.jsonToObject(messageVO.getTo()
                                        .split("-")[1],Set.class);
                                groupList.put(groupName,friends);
                                loadGroupList();
                                //2.弹出群聊界面
                                GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,friends,userName,
                                        connetToServer);
                                groupChatGUIList.put(groupName,groupChatGUI);
                                groupChatGUI.readFromServer(senderName+"说:"+groupMsg);
                            }
                        }
                    }else {
                        //新用户注册  newLogin:userName
                        if(strFromServer.startsWith("newLogin:")){
                            String newFriendName = strFromServer.split(":")[1];
                            users.add(newFriendName);
                            //弹框提示用户上线
                            JOptionPane.showMessageDialog(frame,newFriendName+"上线了！","上线提醒",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadUsers();
                        }
                    }
                }
            }

        }
    }
    //标签点击事件
    private class PrivateLableAction implements MouseListener{
        private String lableName;
        public PrivateLableAction(String lableName){
            this.lableName = lableName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //判断好友列表私聊界面缓存是否已经有指定标签
            if(privateChatGUIList.containsKey(lableName)){
                privateChatGUI privateChatGUI = privateChatGUIList.get(lableName);
                privateChatGUI.getFrame().setVisible(true);
            }else {
                //第一次点击，创建私聊界面
                privateChatGUI privateChatGUI = new privateChatGUI(lableName,userName,
                        connetToServer);
                privateChatGUIList.put(lableName,privateChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //群聊点击事件
    private class GroupLableAction implements MouseListener{
        private String groupName;
        public GroupLableAction(String groupName){
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(groupChatGUIList.containsKey(groupName)){
                GroupChatGUI groupChatGUI = groupChatGUIList.get(groupName);
                groupChatGUI.getFrame().setVisible(true);
            }else {
                Set<String> names = groupList.get(groupName);
                GroupChatGUI groupChatGUI = new GroupChatGUI(groupName,
                        names,userName,connetToServer);
                groupChatGUIList.put(groupName,groupChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //加载所有在线的用户信息
    public void loadUsers(){
        JLabel[] userLables = new JLabel[users.size()];
        JPanel friends = new JPanel();
        friends.setLayout(new BoxLayout(friends,BoxLayout.Y_AXIS));
        //set遍历 users--存储所有在线好友
        Iterator<String> iterator = users.iterator();
        int i = 0;
        while (iterator.hasNext()){
            String userName = iterator.next();
            userLables[i] = new JLabel(userName);
            //添加标签点击事件
            userLables[i].addMouseListener(new PrivateLableAction(userName));
            friends.add(userLables[i]);
            i++;
        }
        friendsList.setViewportView(friends);
        //设置滚动条垂直滚动
        friendsList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        friends.revalidate();
        friendsList.revalidate();
    }
    //好友列表中传入自己，当前在线好友，连接
    public friendsList(String userName,Set<String> users,ConnetToServer connetToServer){
        this.userName = userName;
        this.users = users;
        this.connetToServer = connetToServer;
        frame = new JFrame(userName);
        frame.setContentPane(friendsPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        loadUsers();
        //启动后台线程不断监听服务器发来的消息
        Thread daemonThread = new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();

        creatGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroupGUI(userName,users,connetToServer,friendsList.this);
            }
        });
    }
    public void loadGroupList(){
        //存储所有群名称标签Jpanel
        JPanel groupNamePanel = new JPanel();
        groupNamePanel.setLayout(new BoxLayout(groupNamePanel,
                BoxLayout.Y_AXIS));
        JLabel[] labels = new JLabel[groupList.size()];
        //Map遍历
        Set<Map.Entry<String,Set<String>>> entries = groupList.entrySet();
        Iterator<Map.Entry<String,Set<String>>> iterator = entries.iterator();
        int i = 0;
        while (iterator.hasNext()){
            Map.Entry<String,Set<String>> entry = iterator.next();
            labels[i] = new JLabel(entry.getKey());
            labels[i].addMouseListener(new GroupLableAction(entry.getKey()));
            groupNamePanel.add(labels[i]);
            i++;
        }
        groupListPanel.setViewportView(groupNamePanel);
        groupListPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupListPanel.revalidate();

    }

    public void addGroup(String groupName,Set<String> friends){
        groupList.put(groupName,friends);
    }
}
