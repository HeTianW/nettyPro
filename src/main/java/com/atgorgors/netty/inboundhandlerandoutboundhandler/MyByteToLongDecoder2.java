package com.atgorgors.netty.inboundhandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyByteToLongDecoder2 extends ReplayingDecoder<Void> {
    /**
     *
     * decode会根据接受的数据被调用多次
     *
     * @param ctx 上下文
     * @param in  入站的ByteBuf
     * @param out  List集合，将解码后的数据传给写一个handler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        System.out.println("MyByteToLongDecoder2 decode 被调用");
        //在ReplayingDecoder中 不需要判断数据是否足够读取，内部会进行判断
        out.add(in.readLong());
    }
}
