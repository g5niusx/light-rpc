package com.g5niusx.rpc.client;

import com.g5niusx.rpc.common.registry.ClusterEnum;
import com.g5niusx.rpc.serialization.DefaultRpcSerializationService;
import com.g5niusx.rpc.serialization.RpcSerializationService;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;

/**
 * 客户端配置
 *
 * @author g5niusx
 */
@Builder
@ToString
@Getter
public class ClientConfig {

    @Builder.Default
    private RpcSerializationService rpcSerialize = new DefaultRpcSerializationService();
    /**
     * 连接超时时间
     */
    @Builder.Default
    private Duration                timeOut      = Duration.ofSeconds(10);
    /**
     * zookeeper地址
     */
    @Builder.Default
    private String                  zkIp         = "127.0.0.1";
    /**
     * zookeeper端口
     */
    @Builder.Default
    private Integer                 zkPort       = 2181;
    /**
     * 权重负载的时候启用
     */
    @Builder.Default
    private int                     weight       = 1;
    /**
     * 负载算法
     */
    @Builder.Default
    private ClusterEnum             clusterEnum  = ClusterEnum.HASH;
}
