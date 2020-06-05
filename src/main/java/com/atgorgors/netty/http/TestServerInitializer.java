package com.atgorgors.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //向管道加入处理器

        //得到管道
        ChannelPipeline pipeline = ch.pipeline();

        //加入一个netty提供的HttpServerCodec codec =》[coder - decoder]
        //说明：
        // 1、一个处理Http的编解码器
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        // 2、增加一个自定义的handler
        pipeline.addLast("MyTestHttpServerHandler",new TestHttpServerHandler());

        System.out.printf("ok~~~~~~~~");
    }
}
