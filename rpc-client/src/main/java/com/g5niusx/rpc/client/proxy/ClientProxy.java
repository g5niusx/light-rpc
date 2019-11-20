package com.g5niusx.rpc.client.proxy;

import com.g5niusx.rpc.client.handler.RpcClientHandler;
import com.g5niusx.rpc.client.manager.ClientManager;
import com.g5niusx.rpc.common.annotation.RpcRegister;
import com.g5niusx.rpc.common.exception.RpcException;
import com.g5niusx.rpc.common.interceptor.InterceptorChain;
import com.g5niusx.rpc.common.message.RpcInvokeInfo;
import com.g5niusx.rpc.common.message.RpcRequest;
import com.g5niusx.rpc.common.message.RpcResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClientProxy implements MethodInterceptor {

    private static final Map<Class, Object> MAP              = new ConcurrentHashMap<>();
    private              InterceptorChain   interceptorChain = new InterceptorChain();
    private final        ClientManager      clientManager;

    public ClientProxy(@NonNull ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    /**
     * 创建class的实例
     *
     * @param clazz 类对象
     * @param <T>   类范型
     * @return 实例
     */
    public <T> T create(Class<T> clazz) {
        Object object = MAP.get(clazz);
        if (object == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(this);
            object = enhancer.create();
            MAP.put(clazz, object);
        }
        return (T) object;
    }

    public ClientProxy addInterceptorChain(InterceptorChain interceptorChain) {
        this.interceptorChain = this.interceptorChain.addAll(interceptorChain);
        return this;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Class<?>    declaringClass = method.getDeclaringClass();
        RpcRegister register       = declaringClass.getAnnotation(RpcRegister.class);
        if (register == null) {
            throw new RpcException("@RpcRegister 没有找到!!!!");
        }
        RpcRequest rpcRequest = RpcRequest.builder()
                .id(UUID.randomUUID().toString())
                .date(new Date())
                .rpcInvokeInfo(RpcInvokeInfo.builder()
                        .clazz(declaringClass)
                        .methodName(method.getName())
                        .parameterTypes(method.getParameterTypes())
                        .parameters(args)
                        .clazzName(register.className()).build())
                .rpcContext(new HashMap<>())
                .build();
        // 前置拦截器
        rpcRequest = interceptorChain.before(rpcRequest);
        Object    object    = null;
        Exception exception = null;
        Object    result;
        try {
            object = invoke(rpcRequest);
        } catch (Exception e) {
            exception = e;
            throw new RpcException(e);
        } finally {
            // 后置拦截器，防止异常发生的时候拦截器不执行
            result = interceptorChain.after(rpcRequest, object, exception);
        }
        return result;
    }

    /**
     * 调用远程服务
     *
     * @param rpcRequest 请求远程服务的对象
     * @return 远程服务的返回对象
     */
    private Object invoke(RpcRequest rpcRequest) {
        RpcClientHandler clientHandler = clientManager.getClientHandler();
        if (clientHandler != null) {
            RpcResponse rpcResponse = clientHandler.send(rpcRequest);
            if (!"0000".equals(rpcResponse.getResultCode())) {
                // 将服务器端的异常在客户端抛出
                if (rpcResponse.getResult() instanceof Throwable) {
                    throw new RpcException((Throwable) rpcResponse.getResult());
                }
            }
            return rpcResponse.getResult();
        }
        throw new RpcException("没有找到对应的handler,远程服务可能已经断开!!!");
    }
}
