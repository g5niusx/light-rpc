package com.g5niusx.rpc.common.registry.strategy;

import com.g5niusx.rpc.common.registry.ClusterEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载均衡引擎
 */
public final class ClusterEngine {
    private static final Map<ClusterEnum, ClusterStrategy> CLUSTER_STRATEGY_MAP = new ConcurrentHashMap<>();

    public ClusterEngine(int weight) {
        CLUSTER_STRATEGY_MAP.put(ClusterEnum.WEIGHT_RANDOM, new WeightRandomClusterStrategy(weight));
    }

    static {
        CLUSTER_STRATEGY_MAP.put(ClusterEnum.HASH, new HashClusterStrategy());
        CLUSTER_STRATEGY_MAP.put(ClusterEnum.POLLING, new PollingClusterStrategy());
        CLUSTER_STRATEGY_MAP.put(ClusterEnum.RANDOM, new RandomClusterStrategy());

    }

    public ClusterStrategy clusterStrategy(ClusterEnum clusterEnum) {
        return CLUSTER_STRATEGY_MAP.getOrDefault(clusterEnum, CLUSTER_STRATEGY_MAP.get(ClusterEnum.RANDOM));
    }
}
