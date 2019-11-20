package com.g5niusx.rpc.example;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello() {
        return "Hello";
    }

    @Override
    public String niHao() {
        throw new RuntimeException("模拟异常");
    }
}
