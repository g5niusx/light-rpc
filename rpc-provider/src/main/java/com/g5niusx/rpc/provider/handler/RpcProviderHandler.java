package com.g5niusx.rpc.provider.handler;

import com.g5niusx.rpc.common.exception.RpcException;
import com.g5niusx.rpc.common.interceptor.InterceptorChain;
import com.g5niusx.rpc.common.message.RpcInvokeInfo;
import com.g5niusx.rpc.common.message.RpcRequest;
import com.g5niusx.rpc.common.message.RpcResponse;
import com.g5niusx.rpc.provider.cache.MethodCache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc接受消息的详细处理
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Map<String, FastClass>       CLASS_MAP  = new ConcurrentHashMap<>();
    private static final Map<MethodCache, FastMethod> METHOD_MAP = new ConcurrentHashMap<>();
    private final        InterceptorChain             interceptorChain;

    public RpcProviderHandler(@NonNull InterceptorChain interceptorChain) {
        this.interceptorChain = interceptorChain;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        // 使用netty内置的线程池来减小开销
        EventExecutor                 executor          = ctx.executor();
        CompletableFuture<RpcRequest> completableFuture = CompletableFuture.completedFuture(request);
        completableFuture.thenApplyAsync(this::handle, executor)
                .whenCompleteAsync((response, throwable) -> complete(ctx, response, request, throwable), executor);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush(RpcResponse.builder().resultCode("9999").result(cause.getCause()).build());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("通道已经被关闭:{}", ctx.channel().config().toString());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("通道已经注册:{}", ctx.channel().config().toString());
    }

    /**
     * 处理结果
     *
     * @param ctx       channel处理的上下文
     * @param response  返回对象
     * @param throwable 异常信息
     */
    private void complete(ChannelHandlerContext ctx, RpcResponse response, RpcRequest request, Throwable throwable) {
        if (throwable == null) {
            ctx.writeAndFlush(response);
            return;
        }
        // 异常的情况下，需要使用request里面的请求id
        log.error("处理异常", throwable);
        ctx.writeAndFlush(RpcResponse.builder().resultCode("9999").id(request.getId()).result(throwable.getCause()).build());
    }

    /**
     * 具体调用方法
     *
     * @param request 请求对象
     * @return 封装给客户端的返回对象
     */
    private RpcResponse handle(RpcRequest request) {
        // id为-1代表是心跳消息，需要忽略
        if ("-1".equals(request.getId())) {
            log.info("心跳时间:{},心跳信息:{}", new Date(), request.toString());
            return RpcResponse.builder().resultCode("0000").id(request.getId()).build();
        }
        // 获取缓存的class
        RpcInvokeInfo rpcInvokeInfo = request.getRpcInvokeInfo();
        FastClass     fastClass     = getObject(rpcInvokeInfo.getClazzName());
        // 获取缓存的method
        FastMethod cachedMethod = getCachedMethod(fastClass, rpcInvokeInfo.getMethodName(), rpcInvokeInfo.getParameterTypes());
        request = interceptorChain.before(request);
        Object      invoke    = null;
        Exception   exception = null;
        RpcResponse response  = RpcResponse.builder().resultCode("0000").id(request.getId()).build();
        try {
            invoke = cachedMethod.invoke(fastClass.newInstance(), rpcInvokeInfo.getParameters());
        } catch (Exception e) {
            exception = e;
            response.setResult(e);
            throw new RpcException(e);
        } finally {
            invoke = interceptorChain.after(request, invoke, exception);
            response.setResult(invoke);
        }
        return response;
    }


    /**
     * 获取缓存的方法
     *
     * @param fastClass  缓存的class
     * @param methodName 方法名
     * @param parameters 入参类型
     * @return 可以调用的方法
     */
    private FastMethod getCachedMethod(FastClass fastClass, String methodName, Class[] parameters) {
        MethodCache methodCache = MethodCache.builder().fastClass(fastClass).methodName(methodName).parameterTypes(parameters).build();
        FastMethod  fastMethod  = METHOD_MAP.get(methodCache);
        if (fastMethod != null) {
            return fastMethod;
        }
        FastMethod method = fastClass.getMethod(methodName, parameters);
        METHOD_MAP.put(methodCache, method);
        return method;
    }

    private FastClass getObject(String className) {
        if (CLASS_MAP.get(className) == null) {
            FastClass serviceFastClass = null;
            try {
                serviceFastClass = FastClass.create(Class.forName(className));
            } catch (ClassNotFoundException e) {
                log.error("类名没有找到", e);
            }
            CLASS_MAP.put(className, serviceFastClass);
            return serviceFastClass;
        }
        return CLASS_MAP.get(className);
    }
}
