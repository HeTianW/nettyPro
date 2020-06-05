package com.atgorgors.netty.codec2;

import com.atgorgors.netty.codec.StudentPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 说明
 * 1、我们自定义一个Handler，需要继承netty绑定好的某个HandlerAdapter（规范）
 * 2、这时我们自定义一个Handler
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {

        //根据dataType 来显示不同的信息

        MyDataInfo.MyMessage.DataType dataType = msg.getDataType();
        if(dataType == MyDataInfo.MyMessage.DataType.StudentType) {

            MyDataInfo.Student student = msg.getStudent();
            System.out.println("学生id=" + student.getId() + " 学生名字=" + student.getName());

        } else if(dataType == MyDataInfo.MyMessage.DataType.WorkerType) {
            MyDataInfo.Worker worker = msg.getWorker();
            System.out.println("工人的名字=" + worker.getName() + " 年龄=" + worker.getAge());
        } else {
            System.out.println("传输的类型不正确");
        }
    }


    //    /**
//     * ChannelHandlerContext:读取客户端发送的消息
//     * @param ctx:上下对象，含有管道pipeline，通道channel，地址
//     * @param msg：客户端发送的数据，默认Object
//     * @throws Exception
//     */
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        //读取从客户端发送的StudentPOJO.Student
//
//        StudentPOJO.Student student = (StudentPOJO.Student)msg;
//
//        System.out.println("客户端发送的数据 id="+ student.getId() + "名字="+ student.getName());
//    }

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
