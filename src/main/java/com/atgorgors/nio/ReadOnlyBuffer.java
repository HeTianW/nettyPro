package com.atgorgors.nio;

import java.nio.ByteBuffer;

public class ReadOnlyBuffer {
    public static void main(String[] args) throws Exception{

        //创建一个buffer
        ByteBuffer buffer = ByteBuffer.allocate(64);

        for (int i = 0; i < 64; i++) {
            buffer.put((byte)i);
        }

        //读取
        buffer.flip();

        ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.getClass());

        //读取
        while (readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        //报错
        readOnlyBuffer.put((byte)100);
    }
}
