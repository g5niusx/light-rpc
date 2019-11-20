package com.g5niusx.rpc.starter.properties;

import com.g5niusx.rpc.common.registry.ClusterEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "rpc.client")
public class ClientConfigProperties {
    /**
     * 连接超时时间
     */
    private Duration    timeOut     = Duration.ofSeconds(10);
    /**
     * zookeeper地址
     */
    private String      zkIp        = "127.0.0.1";
    /**
     * zookeeper端口
     */
    private Integer     zkPort      = 2181;
    /**
     * 权重负载的时候启用
     */
    private int         weight      = 1;
    /**
     * 负载算法
     */
    private ClusterEnum clusterEnum = ClusterEnum.HASH;

    private Boolean enabled;
    /**
     * bean名称
     */
    private String  rpcSerialize;
}
