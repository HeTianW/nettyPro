package com.atgorgors.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class MyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int count;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

     //使用客户端发送10条数据 编号

    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);

        //将buffer转换成字符串
        String message = new String(buffer, Charset.forName("utf-8"));
        System.out.println("ctx.channel().remoteAddress() = " + ctx.channel().remoteAddress());
        System.out.println("message = " + message);
        System.out.println("客户端接收到数据的次数" + (++this.count));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
