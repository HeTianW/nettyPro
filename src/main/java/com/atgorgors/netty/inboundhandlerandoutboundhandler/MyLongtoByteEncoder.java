package com.atgorgors.netty.inboundhandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyLongtoByteEncoder extends MessageToByteEncoder<Long> {

    //编码方法
    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
        System.out.println("MyLongtoByteEncoder encode 被调用");
        System.out.println("msg = " + msg);
        out.writeLong(msg);
    }
}
