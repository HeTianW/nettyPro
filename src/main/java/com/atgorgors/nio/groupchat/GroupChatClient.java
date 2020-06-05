package com.atgorgors.nio.groupchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class GroupChatClient extends JFrame {

    //图形界面
    private JFrame jFrame;
    private JPanel jPanel;
    private Container container;
    private JLabel
        usernameLabel,
        chatroomLabel,
        inputLabel,
        portLabel,
        destLabel;
    private JTextArea jTextArea;
    private JTextField
        jTextField,
        destPortText;
    private JButton jButton;
    private JScrollPane jScrollPane;

    //定义相关的属性
    private final String HOST = "127.0.0.1"; // 服务器的ip
    private final int SERVER_PORT = 6666; //服务器端口
    private int LOCAL_PORT = 0;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;
    private String message; //用户要发送的消息
    private String destPort; //用户要发送消息的端口号

    //构造器
    public GroupChatClient() throws Exception{

        //得到选择器
        selector = Selector.open();
        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, SERVER_PORT));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //将channel注册到selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println(socketChannel.getRemoteAddress());
        //得到LocalPort
        LOCAL_PORT = Integer.parseInt(socketChannel.getLocalAddress().toString().substring(11));
        //得到username
        username = "Client"+new Random().nextInt(1000);
        System.out.println(username + " is ok...");

        //初始化图形化界面
        jFrame = new JFrame("GroupChatRoom");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jPanel = new JPanel(){
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon ii = new ImageIcon("tianlan.jpg");
                g.drawImage(ii.getImage(), 0, 0, getWidth(), getHeight(), ii.getImageObserver());
            }
        };
        container = jFrame.getContentPane();
        container.setLayout(new BorderLayout());
        jPanel.setLayout(null);
        usernameLabel = new JLabel("当前用户："+username);
        usernameLabel.setForeground(Color.white);
        usernameLabel.setBounds(10, 10, 280, 20);
        usernameLabel.setFont(new java.awt.Font("楷体", 1, 20));
        chatroomLabel = new JLabel("聊天室:");
        chatroomLabel.setBounds(10,50,80,20);
        chatroomLabel.setFont(new java.awt.Font("楷体", 1, 20));
        chatroomLabel.setForeground(Color.white);
        inputLabel = new JLabel("输入：");
        inputLabel.setBounds(10,340,80,20);
        inputLabel.setFont(new java.awt.Font("楷体", 1, 20));
        inputLabel.setForeground(Color.white);
        portLabel = new JLabel("本机端口号："+LOCAL_PORT);
        portLabel.setBounds(300,10,280,20);
        portLabel.setFont(new java.awt.Font("楷体", 1, 20));
        portLabel.setForeground(Color.white);
        destLabel = new JLabel("端口号：");
        destLabel.setBounds(10,280,90,20);
        destLabel.setFont(new java.awt.Font("楷体", 1, 20));
        destLabel.setForeground(Color.white);
        jTextArea = new JTextArea();
        jTextArea.setEnabled(false);    //设置聊天室内容只读
        jTextArea.setDisabledTextColor(Color.black);
        jScrollPane = new JScrollPane();
        jScrollPane.setBounds(100,50,400,200);
        jScrollPane.setViewportView(jTextArea);
        jTextArea.setFont(new java.awt.Font("楷体", 1, 18));
        jTextField = new JTextField();
        jTextField.setBounds(100,330,270,40);
        jTextField.setFont(new java.awt.Font("楷体", 1, 18));
        destPortText = new JTextField();
        destPortText.setBounds(100,270,180,40);
        destPortText.setFont(new java.awt.Font("楷体", 1, 18));
        jButton = new JButton("发送");
        jButton.setBounds(400,330,100,40);
        jButton.setFont(new java.awt.Font("楷体", 1, 18));
        jButton.setFocusPainted(false);//除去焦点的框
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = jTextField.getText();
                destPort = destPortText.getText();
                System.out.println("destPort:"+destPort);
                if((!message.isEmpty())&&destPortText.getText().isEmpty())  //消息不为空并且目标端口号为空
                    sendInfo(message);
                else if ((!destPortText.getText().isEmpty())&&(!message.isEmpty())){    //消息不为空端口号也不为空
                    sendInfo(destPort,message);
                }
            }
        });
        jButton.setBackground(Color.white);
        jPanel.add(usernameLabel);
        jPanel.add(chatroomLabel);
        jPanel.add(inputLabel);
        jPanel.add(jTextField);
        jPanel.add(jButton);
        jPanel.add(jScrollPane);
        jPanel.add(portLabel);
        jPanel.add(destLabel);
        jPanel.add(destPortText);
        jFrame.setLocation(600,350);
        jFrame.setSize(600,450);
        jFrame.setVisible(true);
        container.add(jPanel);

    }

    //向服务器发送消息
    public void sendInfo(String info){

        info = username + "说：" + info;

        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    //向服务器发送消息
    public void sendInfo(String destPort,String info){

        info =destPort + ","+ username + "对你说：" + info;

        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    //读取从通道返回的消息
    public void readInfo(){

        try {

            int readChannels = selector.select(2000);
            if(readChannels > 0){ //有可用的通道

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){

                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        //得到相关的通道
                        SocketChannel sc = (SocketChannel)key.channel();
                        //得到一个Buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        //读取
                        sc.read(buffer);
                        //把读到的缓冲区数据转换成字符串
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                        jTextArea.append(msg.trim()+'\n');

                    }
                }
                iterator.remove();//删除当前的selectionKey，防止重复操作
            }
            else{
//                System.out.println("没有可用的通道.....");

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{

        //启动我们的客户端
        GroupChatClient chatClient = new GroupChatClient();

        //启动一个线程,每隔3s，读取从服务器发送的数据
        new Thread(){
            public void run(){

                while (true){
                    chatClient.readInfo();
                    try {
                        Thread.currentThread().sleep(3000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //发送数据给服务器
//        Scanner scanner = new Scanner(System.in);
//
//        while (scanner.hasNextLine()){
//            String nextLine = scanner.nextLine();
//            chatClient.sendInfo(nextLine);
//        }

    }
}
