package com.g5niusx.rpc.common.registry;

import com.g5niusx.rpc.common.registry.strategy.HashClusterStrategy;
import com.g5niusx.rpc.common.registry.strategy.PollingClusterStrategy;
import com.g5niusx.rpc.common.registry.strategy.RandomClusterStrategy;
import com.g5niusx.rpc.common.registry.strategy.WeightRandomClusterStrategy;

public enum ClusterEnum {
    /**
     * 随机负载
     * {@link RandomClusterStrategy}
     */
    RANDOM,
    /**
     * 权重随机负载
     * {@link WeightRandomClusterStrategy}
     */
    WEIGHT_RANDOM,
    /**
     * ip hash值负载
     * {@link HashClusterStrategy}
     */
    HASH,
    /**
     * 轮询负载
     * {@link PollingClusterStrategy}
     */
    POLLING
}
