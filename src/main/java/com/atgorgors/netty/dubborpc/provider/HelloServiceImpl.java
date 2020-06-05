package com.atgorgors.netty.dubborpc.provider;

import com.atgorgors.netty.dubborpc.publicinterface.HelloService;

public class HelloServiceImpl implements HelloService {

    //当有消费方调用该方法时，就返回一个结果

    @Override
    public String hello(String msg) {
        System.out.println("msg = " + msg);
        //根据msg返回不同的结果
        if (msg !=null){
            return "你好客户端，我已经收到你的消息 [" + msg +"]";
        }else {
            return "你好，我已经收到你的消息";
        }
    }
}
