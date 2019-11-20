package com.g5niusx.rpc.common.registry.strategy;

import java.util.List;

public interface ClusterStrategy {

    String cluster(List<String> list);

}
