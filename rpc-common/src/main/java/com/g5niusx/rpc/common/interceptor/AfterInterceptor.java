package com.g5niusx.rpc.common.interceptor;


import com.g5niusx.rpc.common.message.RpcRequest;

/**
 * 方法执行后拦截器
 *
 * @author g5niusx
 */
@FunctionalInterface
public interface AfterInterceptor {
    /**
     * 后置拦截方法
     *
     * @param request 请求对象
     * @param result  返回对象
     * @param e       异常信息
     * @return 返回对象
     */
    Object after(RpcRequest request, Object result, Exception e);

    /**
     * 多个拦截器的情况下，按照order返回值来做排序
     *
     * @return 排序级别
     */
    default int order() {
        return 0;
    }
}
