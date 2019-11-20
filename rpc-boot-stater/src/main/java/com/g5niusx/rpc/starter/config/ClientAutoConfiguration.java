package com.g5niusx.rpc.starter.config;

import com.g5niusx.rpc.client.ClientConfig;
import com.g5niusx.rpc.client.RpcClient;
import com.g5niusx.rpc.common.interceptor.AfterInterceptor;
import com.g5niusx.rpc.common.interceptor.BeforeInterceptor;
import com.g5niusx.rpc.starter.SpringContextHolder;
import com.g5niusx.rpc.starter.properties.ClientConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@ConditionalOnProperty(name = {"rpc.client.enabled"}, havingValue = "true")
@Configuration
@EnableConfigurationProperties(ClientConfigProperties.class)
@ImportAutoConfiguration(RpcInterceptorsConfiguration.class)
public class ClientAutoConfiguration {
    @Autowired
    private ClientConfigProperties clientConfigProperties;

    @Bean("rpcSpringContextHolder")
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    @DependsOn("rpcSpringContextHolder")
    public ClientConfig clientConfig() {
        return ClientConfig.builder().clusterEnum(clientConfigProperties.getClusterEnum())
                .rpcSerialize(ServerAutoConfiguration.getRpcSerialization(clientConfigProperties.getRpcSerialize()))
                .timeOut(clientConfigProperties.getTimeOut())
                .weight(clientConfigProperties.getWeight())
                .zkIp(clientConfigProperties.getZkIp())
                .zkPort(clientConfigProperties.getZkPort()).build();
    }

    /**
     * 初始化rpc客户端
     *
     * @param clientConfig 客户端配置
     * @return 客户端实例
     */
    @Bean
    public RpcClient rpcClient(ClientConfig clientConfig, RpcInterceptorsConfiguration interceptorsConfiguration) {
        RpcClient client = new RpcClient(clientConfig).start();
        return client.registerAfterInterceptors(interceptorsConfiguration.sortAfterInterceptor().toArray(new AfterInterceptor[0]))
                .registerBeforeInterceptors(interceptorsConfiguration.sortBeforeInterceptor().toArray(new BeforeInterceptor[0]));
    }
}
