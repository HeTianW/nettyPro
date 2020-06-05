package com.atgorgors.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel01 {
    public static void main(String[] args) throws Exception{

        String str = "hello, gorgor";
        //创建一个输出流->channel
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\file01.txt");

        //通过fileOutputStream获取对应的FileChannel
        //这个fileChanne的真实类型是FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        //创建一个缓冲区ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将 str 放入到 byteBuffer
        byteBuffer.put(str.getBytes());


        //对bytebuffer进行flip
        byteBuffer.flip();

        //把缓冲区中的数据写到通道fileChannel中去
        fileChannel.write(byteBuffer);
        fileOutputStream.close();


    }
}
