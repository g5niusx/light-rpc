package com.g5niusx.rpc.example;

import com.g5niusx.rpc.common.annotation.RpcRegister;

@RpcRegister(className = "com.g5niusx.rpc.example.HelloServiceImpl")
public interface HelloService {
    String hello();

    String niHao();
}
