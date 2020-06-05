package com.atgorgors.netty.heartbreak;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServer {

    public static void main(String[] args) throws Exception{

        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            //使用链式编程来进行设置
            bootstrap.group(bossGroup,workerGroup)  //设置两个线程组
                    .channel(NioServerSocketChannel.class)    //使用NioSocketChannel 作为服务器的通道实现
                    .handler(new LoggingHandler(LogLevel.INFO))  //在bossGroup增加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            //获取到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入一个netty提供的IdleStateHandler
                            /**
                             * 说明
                             * 1、IdleStateHandler 是 netty提供的处理空闲状态的处理器
                             * 2、long readerIdleTime: 表示多长时间没有读，就会发送一个心跳检测包，检测是否还是连接的状态
                             * 3、long writerIdleTime：表示多长时间没有写，就会发送一个心跳检测包，检测是否还是连接的状态
                             * 4、long allIdleTime：表示多长时间没有读写，就会发送一个心跳检测包，检测是否还是连接的状态
                             * 5、Triggers an {@link IdleStateEvent} when a {@link Channel} has not performed
                             *   read, write, or both operation for a while.
                             ***6、当IdleStateEvent触发后，就会传递给管道的下一个handler去处理
                             *    通过回调（触发）下一个handler的userEventTriggerd，在该方法中去处理
                             */
                            pipeline.addLast(new IdleStateHandler(13,5,2, TimeUnit.SECONDS));

                            //加入自己的业务处理handler:对空闲检测进一步处理
                            pipeline.addLast(new MyServerHandler());
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
