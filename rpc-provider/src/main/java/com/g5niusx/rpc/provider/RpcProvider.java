package com.g5niusx.rpc.provider;

import com.g5niusx.rpc.common.NameThreadFactory;
import com.g5niusx.rpc.common.RpcDecoder;
import com.g5niusx.rpc.common.RpcEncoder;
import com.g5niusx.rpc.common.interceptor.AfterInterceptor;
import com.g5niusx.rpc.common.interceptor.BeforeInterceptor;
import com.g5niusx.rpc.common.interceptor.InterceptorChain;
import com.g5niusx.rpc.common.message.RpcRequest;
import com.g5niusx.rpc.common.registry.ServerDiscovery;
import com.g5niusx.rpc.common.registry.ServerRegistry;
import com.g5niusx.rpc.common.utils.IpUtils;
import com.g5niusx.rpc.provider.handler.RpcProviderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class RpcProvider {
    private final InterceptorChain interceptorChain = new InterceptorChain();
    private final ProviderConfig   providerConfig;

    public RpcProvider(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }

    public RpcProvider start() {
        new Thread(this::asyncStart).start();
        return this;
    }

    /**
     * 注册后置拦截器
     *
     * @param afterInterceptors 后置拦截器
     */
    public RpcProvider registerAfterInterceptors(AfterInterceptor... afterInterceptors) {
        InterceptorChain interceptorChain = new InterceptorChain();
        Stream.of(afterInterceptors).forEach(interceptorChain::addAfterInterceptor);
        this.interceptorChain.addAll(interceptorChain);
        return this;
    }

    public RpcProvider asyncStart() {
        EventLoopGroup  acceptEventLoopGroup = new NioEventLoopGroup(4, new NameThreadFactory("accept"));
        EventLoopGroup  workEventLoopGroup   = new NioEventLoopGroup(4, new NameThreadFactory("work"));
        ServerBootstrap serverBootstrap      = new ServerBootstrap();
        // 设置连接超时时间
        serverBootstrap
                // 保持连接数
                .option(ChannelOption.SO_BACKLOG, 128);
        serverBootstrap.group(acceptEventLoopGroup, workEventLoopGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new RpcDecoder(providerConfig.getRpcSerialize(), RpcRequest.class));
                        pipeline.addLast(new RpcEncoder(providerConfig.getRpcSerialize()));
                        pipeline.addLast(new RpcProviderHandler(interceptorChain));
                    }
                });
        try {
            ChannelFuture sync = serverBootstrap.bind(providerConfig.getPort()).sync();
            log.info("服务端启动成功...");
            log.info("正在监听{}端口", providerConfig.getPort());
            sync.channel().eventLoop().execute(() -> {
                ServerRegistry serverRegistry = new ServerRegistry(providerConfig.getZkIp(), providerConfig.getZkPort());
                String         systemIp       = IpUtils.getSystemIp();
                String         data           = systemIp + ":" + providerConfig.getPort();
                serverRegistry.create(ServerDiscovery.PATH + "/" + data, data.getBytes(UTF_8));
            });
        } catch (InterruptedException e) {
            log.error("异步关闭channel异常", e);
        }
        return this;
    }

    /**
     * 注册前置拦截器
     *
     * @param beforeInterceptors 前置拦截器
     */
    public RpcProvider registerBeforeInterceptors(BeforeInterceptor... beforeInterceptors) {
        InterceptorChain interceptorChain = new InterceptorChain();
        Stream.of(beforeInterceptors).forEach(interceptorChain::addBeforeInterceptor);
        this.interceptorChain.addAll(interceptorChain);
        return this;
    }

}
