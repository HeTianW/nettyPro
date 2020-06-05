package com.atgorgors.netty.simple;

import io.netty.util.NettyRuntime;

public class Test {
    public static void main(String[] args) {
        // bossGroup和workerGroup含有的子线程（NioEventLoop）的个数
        // 为cpu核数*2 NettyRuntime.availableProcessors())
        System.out.println(NettyRuntime.availableProcessors());
    }
}
