package com.g5niusx.rpc.common.interceptor;

import com.g5niusx.rpc.common.message.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class InterceptorChain implements BeforeInterceptor, AfterInterceptor {
    private final List<BeforeInterceptor> beforeInterceptors = new LinkedList<>();
    private final List<AfterInterceptor>  afterInterceptors  = new LinkedList<>();

    @Override
    public RpcRequest before(RpcRequest request) {
        for (BeforeInterceptor beforeInterceptor : beforeInterceptors) {
            request = beforeInterceptor.before(request);
        }
        return request;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public Object after(RpcRequest request, Object result, Exception e) {
        for (AfterInterceptor afterInterceptor : afterInterceptors) {
            result = afterInterceptor.after(request, result, e);
        }
        return result;
    }

    public InterceptorChain addAll(InterceptorChain interceptorChain) {
        if (interceptorChain != null) {
            if (!interceptorChain.beforeInterceptors.isEmpty()) {
                this.beforeInterceptors.addAll(interceptorChain.beforeInterceptors);
            }
            if (!interceptorChain.afterInterceptors.isEmpty()) {
                this.afterInterceptors.addAll(interceptorChain.afterInterceptors);
            }
        }
        return this;
    }

    public InterceptorChain addBeforeInterceptor(BeforeInterceptor interceptor) {
        beforeInterceptors.add(interceptor);
        return this;
    }

    public InterceptorChain addAfterInterceptor(AfterInterceptor interceptor) {
        afterInterceptors.add(interceptor);
        return this;
    }
}
