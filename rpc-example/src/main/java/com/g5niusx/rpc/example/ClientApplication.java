package com.g5niusx.rpc.example;

import com.g5niusx.rpc.client.ClientConfig;
import com.g5niusx.rpc.client.RpcClient;
import com.g5niusx.rpc.common.registry.ClusterEnum;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class ClientApplication {
    public static void main(String[] args) throws InterruptedException {
        RpcClient rpcClient = new RpcClient(ClientConfig.builder().zkPort(2181).clusterEnum(ClusterEnum.POLLING).timeOut(Duration.ofDays(1)).build())
                .registerBeforeInterceptors(request -> {
                    log.info("拦截到请求:{}", request);
                    return request;
                })
                .registerAfterInterceptors((request, result, e) -> {
                    log.info("拦截到返回:{}", result);
                    return result;
                })
                .start();
        HelloService helloService = rpcClient.create(HelloService.class);
        log.info("hello调用的结果:{}", helloService.hello());
    }
}
