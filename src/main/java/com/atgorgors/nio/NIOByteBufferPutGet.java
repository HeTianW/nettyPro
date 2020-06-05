package com.atgorgors.nio;

import java.nio.ByteBuffer;

public class NIOByteBufferPutGet {
    public static void main(String[] args) throws Exception{

        ByteBuffer buffer = ByteBuffer.allocate(64);

        buffer.putInt(100);
        buffer.putLong(9);
        buffer.putChar('g');
        buffer.putShort((short)4);

        //取出
        buffer.flip();

        System.out.println();

        //不能打乱顺序，否则取出的数据不正常
        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getShort());
    }
}
