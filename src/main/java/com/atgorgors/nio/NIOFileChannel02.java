package com.atgorgors.nio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileChannel02 {
    public static void main(String[] args) throws Exception{

        //创建文件的输入流
        File file = new File("d:\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        //通过fileInputStream获取对应的FileChannel
        //这个fileChannel的真实类型是FileChannelImpl
        FileChannel fileChannel = fileInputStream.getChannel();

        //创建一个缓冲区ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());

        //从通道中读数据到buffer
        fileChannel.read(byteBuffer);

        System.out.println(new String(byteBuffer.array()));

    }
}
