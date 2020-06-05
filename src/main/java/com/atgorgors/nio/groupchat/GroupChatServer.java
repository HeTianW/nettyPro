package com.atgorgors.nio.groupchat;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    //定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6666;

    //构造器
    public GroupChatServer(){

        try {
            //得到选择器
            selector = Selector.open();
            //ServerSocketChannel
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            listenChannel.configureBlocking(false);
            //将listenChannel 注册到 selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //监听
    public void listen(){

        try {

            //循环处理
            while (true){
                //等待2秒
                int count = selector.select();
                if(count > 0){
                    //有事件处理

                    //遍历得到SelectionKey 集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        //取出SelectionKey
                        SelectionKey key = iterator.next();

                        //监听到accept
                        if(key.isAcceptable()){
                            SocketChannel sc = listenChannel.accept();
                            sc.configureBlocking(false);
                            //将该 sc 注册到Selector
                            sc.register(selector,SelectionKey.OP_READ);
                            //提示
                            System.out.println(sc.getRemoteAddress() + " 上线");
                            sendInfoToOtherClients(sc.getRemoteAddress() + " 上线",sc);
                        }
                        if(key.isReadable()) { //通道发送read事件，即通道时可读的状态
                            readData(key);
                        }

                        //当前的key删除，防止重复操作
                        iterator.remove();
                    }
                }
                else{
                    System.out.println("等待...");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //读取客户端消息
    private void readData(SelectionKey key){

        //定义一个SocketChannel
        SocketChannel channel = null;
        try {
            //得到channel
            channel = (SocketChannel) key.channel();
            //创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int count = channel.read(buffer);
            //根据count的值做处理
            if(count > 0){
                //把缓冲区的数据转换成字符串
                String msg = new String(buffer.array());
                int point = 0;
                for (int i = 0; i < msg.length(); i++) {
                    if(msg.charAt(i)==','){
                        point=i;
                        break;
                    }
                }
                if(point!=0){

                    String destPort = msg.substring(0,point);
                    System.out.println("destPort: "+destPort);
                    msg="【私聊】"+msg.substring(point+1);
                    System.out.println("message:"+msg);
                    sendInfoToSomeClient(msg,destPort,channel);
                }
                else{
                    msg="【群聊】"+msg;
                    //输出消息
                    System.out.println("from 客户端：" + msg);

                    //向其他客户转发信息
                    sendInfoToOtherClients(msg,channel);
                }
            }
        }catch (IOException e){
            try {
                System.out.println(channel.getRemoteAddress() + " 离线了");
                sendInfoToOtherClients(channel.getRemoteAddress() + " 离线了",channel);
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    //向特定用户转发消息
    private void sendInfoToSomeClient(String msg,String destPort,SocketChannel self) throws IOException{

        //标志位 是否有目标端口号用户
        boolean flag = true;
        msg+='\n';
        System.out.println("服务器转发消息中");
        //遍历 所有注册到Selector上的SocketCannel，并排除self
        for (SelectionKey key: selector.keys()){

            //通过key取出对应的SocketChannel
            Channel targetChannel = key.channel();

            //实际类型是SocketChannel(可能是ServerSocketChannel）并排除自己
            if(targetChannel instanceof SocketChannel){

                //转型
                SocketChannel dest =(SocketChannel)targetChannel;
                if(dest.getRemoteAddress().toString().substring(11).endsWith(destPort)){
                    //将msg存储到buffer
                    ByteBuffer buffer =ByteBuffer.wrap(msg.getBytes());
                    //将buffer的数据写入到通道
                    dest.write(buffer);
                    flag = false;
                }
            }
        }

        if(flag){
            self.write(ByteBuffer.wrap("目标端口号错误或用户未上线".getBytes()));
        }
    }

    //转发消息给其他客户（通道）
    private void sendInfoToOtherClients(String msg,SocketChannel self) throws IOException{

        msg+='\n';
        System.out.println("服务器转发消息中");
        //遍历 所有注册到Selector上的SocketCannel，并排除self
        for (SelectionKey key: selector.keys()){

            //通过key取出对应的SocketChannel
            Channel targetChannel = key.channel();

            //实际类型是SocketChannel(可能是ServerSocketChannel）并排除自己
            if(targetChannel instanceof SocketChannel && targetChannel != self){

                //转型
                SocketChannel dest =(SocketChannel)targetChannel;
                //将msg存储到buffer
                ByteBuffer buffer =ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入到通道
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {

        //创建服务器对象
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
