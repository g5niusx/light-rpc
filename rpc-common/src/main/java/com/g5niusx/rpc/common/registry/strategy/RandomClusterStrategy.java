package com.g5niusx.rpc.common.registry.strategy;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.List;

/**
 * 随机负载均衡算法
 *
 * @author g5niusx
 */
@Slf4j
public class RandomClusterStrategy implements ClusterStrategy {
    @Override
    public String cluster(List<String> registryMetas) {
        SecureRandom secureRandom = new SecureRandom();
        int          index        = secureRandom.nextInt(registryMetas.size());
        return registryMetas.get(index);
    }
}
