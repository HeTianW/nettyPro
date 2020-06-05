package com.atgorgors.netty.prototcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyMessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MyMessageDecoder decode 方法被调用");
        //需要得到二进制字节码->MessageProtocol 数据包（对象）
        int lenth = in.readInt();

        byte[] content = new byte[lenth];
        in.readBytes(content);

        //封装成MessageProtocol对象，放入out，传递给下一个handler业务处理
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(lenth);
        messageProtocol.setContent(content);
        out.add(messageProtocol);
    }
}
