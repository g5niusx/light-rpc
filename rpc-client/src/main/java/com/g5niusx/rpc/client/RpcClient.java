package com.g5niusx.rpc.client;

import com.g5niusx.rpc.client.handler.RpcClientHandler;
import com.g5niusx.rpc.client.manager.ClientManager;
import com.g5niusx.rpc.client.proxy.ClientProxy;
import com.g5niusx.rpc.common.interceptor.AfterInterceptor;
import com.g5niusx.rpc.common.interceptor.BeforeInterceptor;
import com.g5niusx.rpc.common.interceptor.InterceptorChain;
import com.g5niusx.rpc.common.observer.BootstrapSubject;
import com.g5niusx.rpc.common.registry.ServerDiscovery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class RpcClient {

    private final ClientConfig  clientConfig;
    private final ClientManager clientManager;
    private       ClientProxy   clientProxy;

    public RpcClient(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        RpcClientHandler clientHandler = new RpcClientHandler(clientConfig);
        clientManager = new ClientManager(clientConfig, clientHandler);
        this.clientProxy = new ClientProxy(clientManager);
    }

    public RpcClient start() {
        BootstrapSubject bootstrapSubject = new BootstrapSubject();
        bootstrapSubject.registryObserver(clientManager);
        ServerDiscovery registryClient = new ServerDiscovery(clientConfig.getZkIp(), clientConfig.getZkPort(), bootstrapSubject);
        List<String>    discovery      = registryClient.discovery(ServerDiscovery.PATH);
        if (!(discovery == null) && !discovery.isEmpty()) {
            bootstrapSubject.publish(discovery);
        }
        return this;
    }

    /**
     * 注册后置拦截器
     *
     * @param afterInterceptors 后置拦截器
     */
    public RpcClient registerAfterInterceptors(AfterInterceptor... afterInterceptors) {
        InterceptorChain interceptorChain = new InterceptorChain();
        Stream.of(afterInterceptors).forEach(interceptorChain::addAfterInterceptor);
        clientProxy = clientProxy.addInterceptorChain(interceptorChain);
        return this;
    }

    /**
     * 注册前置拦截器
     *
     * @param beforeInterceptors 前置拦截器
     */
    public RpcClient registerBeforeInterceptors(BeforeInterceptor... beforeInterceptors) {
        InterceptorChain interceptorChain = new InterceptorChain();
        Stream.of(beforeInterceptors).forEach(interceptorChain::addBeforeInterceptor);
        clientProxy = clientProxy.addInterceptorChain(interceptorChain);
        return this;
    }

    public <T> T create(Class<T> clazz) {
        return clientProxy.create(clazz);
    }
}
