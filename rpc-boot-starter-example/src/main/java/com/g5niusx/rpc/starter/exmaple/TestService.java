package com.g5niusx.rpc.starter.exmaple;

import com.g5niusx.rpc.common.annotation.RpcRegister;

@RpcRegister(className = "com.g5niusx.rpc.starter.exmaple.TestServiceImpl")
public interface TestService {
    String test();
}
