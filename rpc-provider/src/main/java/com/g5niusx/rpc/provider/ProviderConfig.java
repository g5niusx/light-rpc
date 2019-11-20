package com.g5niusx.rpc.provider;

import com.g5niusx.rpc.serialization.DefaultRpcSerializationService;
import com.g5niusx.rpc.serialization.RpcSerializationService;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class ProviderConfig {
    @Builder.Default
    private Integer                 port         = 9000;
    @Builder.Default
    private RpcSerializationService rpcSerialize = new DefaultRpcSerializationService();
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
}
