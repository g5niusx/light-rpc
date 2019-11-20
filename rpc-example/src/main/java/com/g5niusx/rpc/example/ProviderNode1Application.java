package com.g5niusx.rpc.example;

import com.g5niusx.rpc.provider.ProviderConfig;
import com.g5niusx.rpc.provider.RpcProvider;

public class ProviderNode1Application {
    public static void main(String[] args) {
        RpcProvider provider = new RpcProvider(ProviderConfig.builder().port(9001).build());
        provider.start();
    }
}
