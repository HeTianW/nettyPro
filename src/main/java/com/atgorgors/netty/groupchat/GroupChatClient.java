package com.atgorgors.netty.groupchat;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class GroupChatClient {

    //属性
    private final String host;
    private final int port;

    public GroupChatClient(String host,int port){

        this.port = port;
        this.host = host;
    }

    public void run() throws Exception{

        //客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //创建客户端启动对象
            //注意客户端启动的是Bootstrap而不是ServerBootstrap
            Bootstrap bootstrap = new Bootstrap();

            //设置相关参数
            bootstrap.group(group)    //设置线程组
                    .channel(NioSocketChannel.class) //设置客户端通道的实现类
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //向pipeline加入解码器
                            pipeline.addLast("decoder",new StringDecoder());
                            //向pipeline加入编码器
                            pipeline.addLast("encoder",new StringEncoder());
                            //加入自己的业务处理handler
                            pipeline.addLast(new GroupChatClientHandler());
                        }
                    });
            System.out.println("客户端 ok...");

            //启动客户端去链接服务器端
            //channelFuture涉及到netty的异步模型
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            //得到channel
            Channel channel = channelFuture.channel();
            System.out.printf("----------" + channel.localAddress() + "-----------\n");

            //客户需要输入信息，创建一个扫描器
            Scanner sc = new Scanner(System.in);
            while (sc.hasNextLine()){
                String s = sc.nextLine();
                channel.writeAndFlush(s+"\r\n");
            }

            //给关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{

        new GroupChatClient("127.0.0.1",7000).run();
    }
}
