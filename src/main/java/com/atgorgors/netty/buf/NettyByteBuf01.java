package com.atgorgors.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuf01 {
    public static void main(String[] args) {

        //创建一个ByteBuf
        //说明
        //1、创建对象，该对象包含一个数组arr，是一个byte[10]
        //2、在netty的bufffer中不需要flip进行反转
        //  底层维护了readerindex和writeindex
        //0--readindex：可读
        //wtriteindex--capacity：可写
        ByteBuf buffer = Unpooled.buffer(10);

        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        System.out.println("buffer.capacity() = " + buffer.capacity());

        //输出
        for (int i = 0; i < buffer.capacity(); i++) {
//            System.out.println(buffer.getByte(i));
            System.out.println(buffer.readByte());
        }
    }
}
