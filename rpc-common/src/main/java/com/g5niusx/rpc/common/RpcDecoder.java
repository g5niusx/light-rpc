package com.g5niusx.rpc.common;

import com.g5niusx.rpc.serialization.RpcSerializationService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * rpc解码器
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    private final RpcSerializationService rpcSerializationService;
    private final Class                   clazz;

    public RpcDecoder(RpcSerializationService rpcSerializationService, Class clazz) {
        this.rpcSerializationService = rpcSerializationService;
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object deserialize = rpcSerializationService.deserialize(bytes, clazz);
        out.add(deserialize);
    }
}
