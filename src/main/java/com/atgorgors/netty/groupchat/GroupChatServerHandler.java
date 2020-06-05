package com.atgorgors.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //若要实现私聊
    //使用一个hashMap管理
    public static Map<ChannelId, Channel> channelMap = new HashMap<ChannelId, Channel>();

    //定义一个channel组，管理所有的channel
    //GlobalEventExecutor.INSTANCE是一个全局的事件执行器，是一个单例
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //handlerAdd 表示一旦链接，第一个被执行
    //将当前channel加入到channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户端加入聊天的信息推送给其他在线的客户
        /*
          该方法会将channelGroup中的所有channel遍历，并发送消息
          我们不需要自己遍历
         */
        channels.writeAndFlush("[客户端]"+channel.remoteAddress()+" 加入聊天 "
                +sdf.format(new java.util.Date()) + '\n');
        channels.add(channel);

        channelMap.put(channel.id(),channel);
    }

    //断开连接，将消息推送在当前在线的客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        channels.writeAndFlush("[客户端]"+channel.remoteAddress()+" 离开了"
            +sdf.format(new java.util.Date()) + '\n');

        //不需要再remove
        System.out.printf("ChannelGoup Size: " + channels.size() + '\n');
    }

    //表示channel处于活动状态，提示xx上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println(ctx.channel().remoteAddress()+ " 上线了");

    }

    //表示channel处于不活动状态，提示xx离线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+" 离线了");

    }

    //读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        //获取到当前channel
        Channel channel = ctx.channel();
        //这是我们遍历channelGroup，根据不同的情况，回送不同的消息

        channels.forEach(ch ->{
            if(channel != ch ){
                //不是当前channel，转发消息
                ch.writeAndFlush("[客户]" + channel.remoteAddress()+" "+sdf.format(new java.util.Date()) + "发送了消息" + msg + "\n");
            }else{
                ch.writeAndFlush(" [自己]发送了消息" + msg + " " + sdf.format(new java.util.Date())+'\n');
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        //关闭
        ctx.close();
    }
}
