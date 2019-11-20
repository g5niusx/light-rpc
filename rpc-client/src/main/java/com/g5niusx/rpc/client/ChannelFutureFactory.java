package com.g5niusx.rpc.client;

import com.g5niusx.rpc.client.handler.RpcClientHandler;
import com.g5niusx.rpc.common.RpcDecoder;
import com.g5niusx.rpc.common.RpcEncoder;
import com.g5niusx.rpc.common.message.RpcResponse;
import com.g5niusx.rpc.serialization.RpcSerializationService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ChannelFutureFactory {

    public static ChannelFuture getClient(String ip, int port, RpcSerializationService serializationService, RpcClientHandler clientHandler) {
        EventLoopGroup acceptEventLoopGroup = new NioEventLoopGroup(1);
        Bootstrap      bootstrap            = new Bootstrap();
        bootstrap.group(acceptEventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        //设置保持连接
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                // 有数据立即发送
                .option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new RpcDecoder(serializationService, RpcResponse.class));
                pipeline.addLast(new RpcEncoder(serializationService));
                pipeline.addLast(clientHandler);
            }
        });
        try {
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
            if (channelFuture.isSuccess()) {
                log.info("客户端连接[{}:{}]成功", ip, port);
            }
            return channelFuture;
        } catch (InterruptedException e) {
            log.error("客户端异常", e);
        }
        return null;
    }
}
