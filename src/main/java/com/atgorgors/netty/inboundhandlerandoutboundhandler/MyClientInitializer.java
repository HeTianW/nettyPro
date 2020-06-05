package com.atgorgors.netty.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class MyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        //这时加入一个入站的handler
        pipeline.addLast(new MyByteToLongDecoder2());
        //加入一个出站的handler，对数据进行编码
        pipeline.addLast(new MyLongtoByteEncoder());
        //在加入自定义的handler，处理业务
        pipeline.addLast(new MyClientHandler());
    }
}
