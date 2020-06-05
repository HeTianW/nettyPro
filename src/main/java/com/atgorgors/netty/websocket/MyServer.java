package com.atgorgors.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
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

                            //因为基于Http协议，因此我们要使用Http的编码和解码器
                            pipeline.addLast(new HttpServerCodec());
                            //以块的方式处理，添加ChunkedWriteHandler处理器
                            pipeline.addLast(new ChunkedWriteHandler());

                            /**
                             * 说明
                             * http在传输过程中会分段，HttpObjectAggregator可以将多个段聚合
                             * 浏览器发送大量数据时数据会分段
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192 ));

                            /**
                             * 说明
                             * 1、websocket的数据是以帧（frame）形式传递的
                             * 2、WebSocketFrame下有6个子类
                             * 3、浏览器请求时 ws://localhost:7000/xxx (xxx:请求的uri表示)
                             * 4、WebSocketServerProtocolHandler的核心功能：将Http协议升级为websocket协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            //加入自己的业务处理handler
                            pipeline.addLast(new MyTextWebSocketFrameHandler());
                        }
                    }); //给我们的workGroup的EventLoop对应的管道设置处理器

            System.out.println("....服务器 is ready...");

            //绑定一个端口并同步，生成了一个ChannelFuture对象
            //启动服务器（并绑定端口）
            ChannelFuture cf = bootstrap.bind(7000).sync();

            //给cf 注册监听器，监控我们关心的事件
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(cf.isSuccess()){
                        System.out.println("监听端口 7000 成功");
                    }else {
                        System.out.println("监听端口 7000 失败");
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
