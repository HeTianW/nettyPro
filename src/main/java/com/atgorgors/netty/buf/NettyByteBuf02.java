package com.atgorgors.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.Buffer;
import java.nio.charset.Charset;

public class NettyByteBuf02 {
    public static void main(String[] args) {

        //创建byteBuf
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!你好", Charset.forName("utf-8"));

        //相关方法
        if(byteBuf.hasArray()) {

            byte[] content = byteBuf.array();

            //将content转换成字符串
            System.out.println(new String(content,Charset.forName("utf-8")));

            System.out.println("byteBuf = " + byteBuf);

            System.out.println("byteBuf.arrayOffset() = " + byteBuf.arrayOffset());//0
            System.out.println("byteBuf.readerIndex() = " + byteBuf.readerIndex());//0
            System.out.println("byteBuf.writerIndex() = " + byteBuf.writerIndex());//18

            System.out.println("byteBuf.capacity() = " + byteBuf.capacity());//64

            System.out.println(byteBuf.readByte());
            System.out.println("byteBuf.readableBytes() = " + byteBuf.readableBytes());//17

            System.out.println(byteBuf.getByte(0));

            System.out.println("byteBuf.readableBytes() = " + byteBuf.readableBytes());//17

            //使用for取出各个字节
            for (int i = 0; i < byteBuf.readableBytes(); i++) {
                System.out.println((char)byteBuf.getByte(i));
            }

            System.out.println(byteBuf.getCharSequence(0,4,Charset.forName("utf-8")));//hell

        }
    }
}
