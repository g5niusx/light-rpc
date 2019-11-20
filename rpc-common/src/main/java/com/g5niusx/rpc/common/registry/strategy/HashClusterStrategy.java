package com.g5niusx.rpc.common.registry.strategy;


import com.g5niusx.rpc.common.utils.IpUtils;
import io.netty.util.internal.StringUtil;

import java.util.List;

/**
 * hash值软负载
 *
 * @author g5niusx
 */
public class HashClusterStrategy implements ClusterStrategy {

    private static final String IP = IpUtils.getSystemIp();


    @Override
    public String cluster(List<String> list) {
        if (StringUtil.isNullOrEmpty(IP)) {
            return list.get("127.0.0.1".hashCode() % list.size());
        }
        int hashCode = IP.hashCode();
        return list.get(hashCode % list.size());
    }
}
