package com.atgorgors.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * 说明
 * 1、SimpleChannelInboundHandler是ChannelInboundHandlerAdapter
 * 2、HttpObject：客户端和服务器互相通讯的数据被封装成HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    //读取客户端数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        //判断msg是不是HttpRequest请求
        if(msg instanceof HttpRequest){

            System.out.println("ctx 类型 = " + ctx.getClass());

            System.out.println("ctx.channel() = " + ctx.channel());
            System.out.println("ctx.pipeline() = " + ctx.pipeline());
            System.out.println("通过pipeline获得的channel = " + ctx.pipeline().channel());

            System.out.println("ctx.handler() = " + ctx.handler());
            
            System.out.println("ctx.pipeline().hashCode() = " + ctx.pipeline().hashCode());
            System.out.println("TestHttpServerHandler.hashCode() = " + this.hashCode());

            System.out.println("msg 类型= " + msg.getClass());
            System.out.println("客户端地址= " + ctx.channel().remoteAddress());

            //获取到
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取到uri 统一资源标识符（Uniform Resource Identifier，URI)
            URI uri = new URI(httpRequest.uri());
            //回复信息给浏览器 [Http协议]
            if("/favicon.ico" .equals(uri .getPath())){
                System.out.println("请求了favicon.ico, 不做响应");
                return;
            };


            ByteBuf content = Unpooled.copiedBuffer("hello,我是服务器", CharsetUtil.UTF_16);

            //构造一个Http的响应，即HttpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,HttpResponseStatus.OK,content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,content.readableBytes());

            //将构建好的response返回
            ctx.writeAndFlush(response);

        }
    }
}
