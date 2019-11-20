package com.g5niusx.rpc.starter.config;

import com.g5niusx.rpc.common.interceptor.AfterInterceptor;
import com.g5niusx.rpc.common.interceptor.BeforeInterceptor;
import com.g5niusx.rpc.provider.ProviderConfig;
import com.g5niusx.rpc.provider.RpcProvider;
import com.g5niusx.rpc.serialization.DefaultRpcSerializationService;
import com.g5niusx.rpc.serialization.RpcSerializationService;
import com.g5niusx.rpc.starter.SpringContextHolder;
import com.g5niusx.rpc.starter.properties.ServerConfigProperties;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = {"rpc.server.enabled"}, havingValue = "true")
@Configuration
@EnableConfigurationProperties(ServerConfigProperties.class)
@Slf4j
@ImportAutoConfiguration(RpcInterceptorsConfiguration.class)
public class ServerAutoConfiguration {
    @Autowired
    private ServerConfigProperties serverConfigProperties;

    @Bean
    public RpcProvider rpcProvider(RpcInterceptorsConfiguration interceptorsConfiguration) {
        ProviderConfig providerConfig = ProviderConfig.builder().port(serverConfigProperties.getPort())
                .zkIp(serverConfigProperties.getZkIp())
                .zkPort(serverConfigProperties.getZkPort())
                .rpcSerialize(ServerAutoConfiguration.getRpcSerialization(serverConfigProperties.getRpcSerialize())).build();
        RpcProvider provider = new RpcProvider(providerConfig).asyncStart();
        return provider.registerAfterInterceptors(interceptorsConfiguration.sortAfterInterceptor().toArray(new AfterInterceptor[0]))
                .registerBeforeInterceptors(interceptorsConfiguration.sortBeforeInterceptor().toArray(new BeforeInterceptor[0]));
    }

    static RpcSerializationService getRpcSerialization(String rpcSerialize) {
        RpcSerializationService rpcSerializationService;
        if (StringUtil.isNullOrEmpty(rpcSerialize)) {
            rpcSerializationService = new DefaultRpcSerializationService();
        } else {
            rpcSerializationService = SpringContextHolder.getBean(rpcSerialize, RpcSerializationService.class);
        }
        return rpcSerializationService;
    }
}
