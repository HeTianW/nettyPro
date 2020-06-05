package com.atgorgors.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

//使用一个buffer完成文件读取
public class NIOFileChannel03 {

    public static void main(String[] args) throws Exception{

        FileInputStream fileInputStream = new FileInputStream(("1.txt"));
        FileChannel fileChannel01 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("2.txt");
        FileChannel fileChannel02 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(512);

        while (true){
            //循环读取

            //这里一定不要忘了复位
            /*
            public final Buffer clear() {
                position = 0;
                limit = capacity;
                mark = -1;
                return this;
            }
            */
            byteBuffer.clear(); //清空
            int read = fileChannel01.read(byteBuffer);
            System.out.println("read="+read);
            if (read == -1){
                //表示读完
                break;
            }
            //将buffer中的数据写入fileChannel02
            byteBuffer.flip();
            fileChannel02.write(byteBuffer);
            System.out.println();
        }

    }
}
