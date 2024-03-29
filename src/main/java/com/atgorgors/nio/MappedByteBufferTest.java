package com.atgorgors.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/*
说明
1、MappedByteBuffer 可以让文件在内存中修改，操作系统不需要再拷贝一次
 */
public class MappedByteBufferTest {
    public static void main(String[] args) throws Exception{

        RandomAccessFile randomAccessFile = new RandomAccessFile("1.txt", "rw");
        //获取对应的通道
        FileChannel channel = randomAccessFile.getChannel();

        /**
         * 参数1：FileChannel.MapMode.READ_WRITE：使用的是读写模式
         * 参数2：0： 可以直接修改的起始位置
         * 参数3：5： 是映射到内存的大小，即将1.txt的多少个字节映射到内存
         * 可以直接修改的范围就是 0-5
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        mappedByteBuffer.put(0,(byte)'H');
        mappedByteBuffer.put(3,(byte)'9');
//        mappedByteBuffer.put(5,(byte)'Y'); //越界

        randomAccessFile.close();
        System.out.println("修改成功~~~");
    }
}
