package com.g5niusx.rpc.starter.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rpc.server")
public class ServerConfigProperties {
    private Integer port   = 9000;
    private String  rpcSerialize;
    /**
     * zookeeper地址
     */
    private String  zkIp   = "127.0.0.1";
    /**
     * zookeeper端口
     */
    private Integer zkPort = 2181;

    private Boolean enabled;
}
