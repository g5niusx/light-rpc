package com.g5niusx.rpc.common.registry.strategy;


import lombok.NonNull;

import java.security.SecureRandom;
import java.util.List;

/**
 * 权重随机算法
 *
 * @author g5niusx
 */
public class WeightRandomClusterStrategy implements ClusterStrategy {

    /**
     * 权重
     */
    private int weight;

    public WeightRandomClusterStrategy(int weight) {
        this.weight = weight;
    }

    @Override
    public String cluster(@NonNull List<String> registryMetas) {
        for (int i = 0; i < registryMetas.size(); i++) {
            for (int j = 0; j < weight; j++) {
                registryMetas.add(registryMetas.get(i));
            }
        }
        SecureRandom secureRandom = new SecureRandom();
        return registryMetas.get(secureRandom.nextInt(registryMetas.size()));
    }
}
