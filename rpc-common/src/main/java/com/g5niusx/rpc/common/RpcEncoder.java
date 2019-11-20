package com.g5niusx.rpc.common;

import com.g5niusx.rpc.serialization.RpcSerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc编码器
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<Object> {
    private final RpcSerializationService rpcSerializationService;

    public RpcEncoder(@NonNull RpcSerializationService rpcSerializationService) {
        this.rpcSerializationService = rpcSerializationService;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] serialize = rpcSerializationService.serialize(msg);
        out.writeInt(serialize.length);
        out.writeBytes(serialize);
    }
}
