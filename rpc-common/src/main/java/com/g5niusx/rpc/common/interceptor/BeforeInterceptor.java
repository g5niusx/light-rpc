package com.g5niusx.rpc.common.interceptor;

import com.g5niusx.rpc.common.message.RpcRequest;

/**
 * 方法执行前拦截器
 *
 * @author g5niusx
 */
@FunctionalInterface
public interface BeforeInterceptor {
    /**
     * 前置拦截方法
     *
     * @param request rpc的请求对象
     * @return rpc请求对象
     */
    RpcRequest before(RpcRequest request);

    /**
     * 多个拦截器的情况下，按照order返回值来做排序
     *
     * @return 排序级别
     */
    default int order() {
        return 0;
    }
}
