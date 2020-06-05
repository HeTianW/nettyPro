package com.atgorgors.netty.inboundhandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyByteToLongDecoder extends ByteToMessageDecoder {
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
        System.out.println("MyByteToLongDecoder decode 被调用");
        //因为long 8个字节，需要判断8个字节，才能读取一个long
        if(in.readableBytes() >= 8){
            out.add(in.readLong());
        }
    }
}
