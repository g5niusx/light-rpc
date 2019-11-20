package com.g5niusx.rpc.client.manager;

import com.g5niusx.rpc.client.ChannelFutureFactory;
import com.g5niusx.rpc.client.ClientConfig;
import com.g5niusx.rpc.client.ConnectionHolder;
import com.g5niusx.rpc.client.handler.RpcClientHandler;
import com.g5niusx.rpc.common.exception.RpcException;
import com.g5niusx.rpc.common.observer.NodeObserver;
import com.g5niusx.rpc.common.registry.strategy.ClusterEngine;
import com.g5niusx.rpc.common.registry.strategy.ClusterStrategy;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClientManager implements NodeObserver {
    private Map<String, ChannelFuture> channelFutureMap    = new ConcurrentHashMap<>();
    /**
     * 已经过期的
     * channel
     */
    private Map<String, ChannelFuture> expireChannelFuture = new ConcurrentHashMap<>();
    private ClientConfig               clientConfig;
    private RpcClientHandler           clientHandler;
    private ClusterStrategy            clusterStrategy;

    public ClientManager(ClientConfig clientConfig, RpcClientHandler clientHandler) {
        this.clientConfig = clientConfig;
        this.clientHandler = clientHandler;
        clusterStrategy = new ClusterEngine(clientConfig.getWeight()).clusterStrategy(clientConfig.getClusterEnum());
    }

    @Override
    public void update(List<String> list) {
        // 清理过期数据
        clearExpireChannel();
        // 将上一次的数据放入到过期列表中
        expireChannelFuture.putAll(channelFutureMap);
        // 清理数据,以当前最新的信息为准
        channelFutureMap.clear();
        list.forEach(key -> {
            // 已经存在的channel有可能会过期，需要移动到过期的map，后续来释放
            if (channelFutureMap.get(key) != null) {
                expireChannelFuture.put(key, channelFutureMap.get(key));
            }
            String[] split = key.split(":");
            channelFutureMap.put(key, ChannelFutureFactory.getClient(split[0], Integer.parseInt(split[1]), clientConfig.getRpcSerialize(), clientHandler));
        });
        log.info("当前可用节点为:{}", channelFutureMap.toString());
    }

    /**
     * 清理上一次失效的channel
     */
    private void clearExpireChannel() {
        expireChannelFuture.forEach((s, channelFuture) -> channelFuture.channel().close());
    }

    public RpcClientHandler getClientHandler() {
        String cluster = clusterStrategy.cluster(new ArrayList<>(channelFutureMap.keySet()));
        ConnectionHolder.set(cluster);
        log.info("负载请求地址为:{}", cluster);
        ChannelFuture channelFuture = channelFutureMap.get(cluster);
        if (channelFuture == null) {
            throw new RpcException(String.format("[%s]没有找到对应的连接", cluster));
        }
        try {
            channelFuture = channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!channelFuture.channel().isActive()) {
            String[] split = cluster.split(":");
            channelFuture.channel().connect(new InetSocketAddress(split[0], Integer.parseInt(split[1])));
        }
        return channelFuture.channel().pipeline().get(RpcClientHandler.class);
    }


}
