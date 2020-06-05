package com.atgorgors.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Selector 能够检测多个注册的通道上是否有事件发生
 * 多个Channel以时间的方式可以注册到同一个Selector
 * 不用维护多个线程，避免了多线程的开销
 * 由于读写都是非阻塞的，这就可充分提升IO线程的运行效率
 * 编写一个NIO入门案例，实现服务器端和客户端之间的数据简单通讯(非阻塞)
 */

public class NIOServer {
    public static void main(String[] args) throws Exception{

        //创建ServerSocketChannel -> ServerSocket

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //得到一个Selector对象
        Selector selector = Selector.open();

        //绑定一个端口6666
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //把 serverSocketChannel 注册 selector 关心事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true){

            //这里我们等待1秒，如果没有事件发生就继续
            if (selector.select(1000) ==0){
                //没有事件发生
                System.out.println("服务器等了一秒，无连接");
                continue;
            }

            //如果返回的不是0,就获取到相关的selectionKey集合
            //通过 selectionKeys 反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //使用迭代器遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {

                //获取到SelectionKey
                SelectionKey key = keyIterator.next();
                //根据key对应的通道发生的事件做相应的处理
                if(key.isAcceptable()){
                    //该客户端生成一个SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功 生成了一个 socketChannel " + socketChannel.hashCode());

                    //将 SocketChannel 设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将socketChannel 注册到 selector, 关注事件为 OP_READ， 同时给SocektChannel关联一个buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }
                if(key.isReadable()){

                    //通过key反向获取到对应的channel
                    SocketChannel channel = (SocketChannel)key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer byteBuffer = (ByteBuffer)key.attachment();
                    channel.read(byteBuffer);
                    System.out.println("from 客户端" + new String(byteBuffer.array()));
                }

                //手动从集合中移除当前的selectionKey，防止重复操作
                keyIterator.remove();
            }
        }
    }
}
