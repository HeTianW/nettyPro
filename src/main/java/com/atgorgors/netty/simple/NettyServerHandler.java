package com.atgorgors.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 说明
 * 1、我们自定义一个Handler，需要继承netty绑定好的某个HandlerAdapter（规范）
 * 2、这时我们自定义一个Handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * ChannelHandlerContext:读取客户端发送的消息
     * @param ctx:上下对象，含有管道pipeline，通道channel，地址
     * @param msg：客户端发送的数据，默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //加入有一个特别耗时长的业务->异步执行->提交channel对应的NIOEventLoop的taskQueue中

        /*
        //解决方案1 用户自定义的普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(10*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端~汪2",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常："+ e.getMessage());
                }

            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(20*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端~汪3",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常："+ e.getMessage());
                }

            }
        });

        //用户自定义定时任务-》该任务是提交到scheduleTaskQueue中
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20*1000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端~汪4",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    System.out.println("发生异常："+ e.getMessage());
                }
            }
        },5, TimeUnit.SECONDS);
        */


        System.out.println("go on...");
        /*
        System.out.println("服务器读取线程" + Thread.currentThread().getName() );
        System.out.println("server ctx="+ctx);
        System.out.println("看看channel和piple的关系");
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();
        //将msg转成一个ByteBuf(netty提供的，性能更高)
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是："+buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址："+ctx.channel().remoteAddress());

         */
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //writeAndFlash ： write+flash
        //将数据写入到缓存并刷新
        //一般我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端~汪1",CharsetUtil.UTF_8));
    }

    //处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
