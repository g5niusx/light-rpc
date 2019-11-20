package com.g5niusx.rpc.client.handler;

import com.g5niusx.rpc.client.ClientConfig;
import com.g5niusx.rpc.client.ConnectionHolder;
import com.g5niusx.rpc.common.exception.RpcException;
import com.g5niusx.rpc.common.exception.RpcTimeoutException;
import com.g5niusx.rpc.common.message.RpcRequest;
import com.g5niusx.rpc.common.message.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Getter
    private final ClientConfig                clientConfig;
    private       Channel                     channel;
    private       RpcResponse                 rpcResponse;
    private final Map<String, CountDownLatch> map = new ConcurrentHashMap<>();

    public RpcClientHandler(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
        if (!"-1".equals(this.rpcResponse.getId())) {
            map.get(rpcResponse.getId()).countDown();
        } else {
            log.info("心跳时间:{},心跳信息:{}", new Date(), rpcResponse.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端异常:", cause);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channel = ctx.channel();
    }

    public RpcResponse send(RpcRequest request) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        map.put(request.getId(), countDownLatch);
        channel.writeAndFlush(request);
        try {
            // 设置请求超时时间
            boolean await = countDownLatch.await(clientConfig.getTimeOut().toMillis(), TimeUnit.MILLISECONDS);
            if (!await) {
                throw new RpcTimeoutException(String.format("连接[%s]超时，超时时间为[%s]", ConnectionHolder.get(), clientConfig.getTimeOut().toString()));
            }
            // 删除已经调用过的请求id
            map.remove(request.getId());
        } catch (InterruptedException e) {
            throw new RpcException(e);
        } finally {
            // 移除线程变量
            ConnectionHolder.remove();
        }
        return rpcResponse;
    }
}
