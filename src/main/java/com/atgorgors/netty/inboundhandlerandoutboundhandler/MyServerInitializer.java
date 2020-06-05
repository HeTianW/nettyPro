package com.atgorgors.netty.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class MyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //入站的handler进行解码MyByteToLongDecoder
        pipeline.addLast(new MyByteToLongDecoder2());

        pipeline.addLast(new MyLongtoByteEncoder());
        //出站的handler进行编码

        //加入自定义的handler
        pipeline.addLast(new MyServerHandler());
    }
}
