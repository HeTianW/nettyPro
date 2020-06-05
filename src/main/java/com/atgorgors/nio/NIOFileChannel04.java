package com.atgorgors.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

//拷贝一个图片
public class NIOFileChannel04 {
    public static void main(String[] args) throws Exception{

        //创建相关流
        FileInputStream fileInputStream = new FileInputStream("a.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("a2.jpg");

        //获取各个流对应的fileChannel
        FileChannel sourceCh = fileInputStream.getChannel();
        FileChannel destCh = fileOutputStream.getChannel();

        //使用transform完成拷贝
        destCh.transferFrom(sourceCh,0,sourceCh.size());

        //关闭相关通道和流
        sourceCh.close();
        destCh.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
