package com.atgorgors.netty.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

public class NettyServer {
    public static void main(String[] args) throws Exception{

        //说明
        //1、创建BossGroup和WorkGroup
        //2、bossGroup只处理连接请求，真正的客户端业务处理会交给WorkGroup
        //3、两个都是无限循环
        //4、bossGroup和workerGroup含有的子线程（NioEventLoop）的个数
        //   为cpu核数*2 NettyRuntime.availableProcessors())
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            //创建服务端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            //使用链式编程来进行设置
            bootstrap.group(bossGroup,workerGroup)  //设置两个线程组
                    .channel(NioServerSocketChannel.class)    //使用NioSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128)   //设置线程队列得到链接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true)   //设置保持连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //在服务端pipeline中加入ProtoBufDecoder：
                            //指定对哪一种对象进行解码
                            pipeline.addLast("decoder",new ProtobufDecoder(StudentPOJO.Student.getDefaultInstance()));
                            pipeline.addLast(new NettyServerHandler());

                        }
                    }); //给我们的workGroup的EventLoop对应的管道设置处理器

            System.out.println("....服务器 is ready...");

            //绑定一个端口并同步，生成了一个ChannelFuture对象
            //启动服务器（并绑定端口）
            ChannelFuture cf = bootstrap.bind(6668).sync();

            //给cf 注册监听器，监控我们关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("监听端口 6668 成功");
                    }else {
                        System.out.println("监听端口 6668 失败");
                    }
                }
            });
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully(); //优雅地关闭
        }

    }
}
