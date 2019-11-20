package com.g5niusx.rpc.starter.exmaple;

import com.g5niusx.rpc.client.RpcClient;
import com.g5niusx.rpc.common.interceptor.AfterInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Slf4j
public class RpcBootStarterExampleApplication {

    @Autowired
    private RpcClient rpcClient;

    public static void main(String[] args) {
        SpringApplication.run(RpcBootStarterExampleApplication.class, args);
    }

    @GetMapping("/test.json")
    public String test() {
        TestService testService = rpcClient.create(TestService.class);
        String      test        = testService.test();
        log.info("接收到:{}", test);
        return test;
    }

    @Bean
    public AfterInterceptor beforeInterceptor() {
        return (request, result, e) -> {
            log.info("拦截到返回:{}", result);
            return result;
        };
    }
}

