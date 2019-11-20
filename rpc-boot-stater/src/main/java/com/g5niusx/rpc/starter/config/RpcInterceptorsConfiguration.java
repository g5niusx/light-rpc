package com.g5niusx.rpc.starter.config;

import com.g5niusx.rpc.common.interceptor.AfterInterceptor;
import com.g5niusx.rpc.common.interceptor.BeforeInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Configuration
public class RpcInterceptorsConfiguration implements BeanPostProcessor {
    private List<BeforeInterceptor> beforeInterceptors = new ArrayList<>();
    private List<AfterInterceptor>  afterInterceptors  = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BeforeInterceptor) {
            beforeInterceptors.add((BeforeInterceptor) bean);
        }
        if (bean instanceof AfterInterceptor) {
            afterInterceptors.add((AfterInterceptor) bean);
        }
        return bean;
    }

    public List<AfterInterceptor> sortAfterInterceptor() {
        afterInterceptors.sort(Comparator.comparingInt(AfterInterceptor::order));
        return afterInterceptors;
    }

    public List<BeforeInterceptor> sortBeforeInterceptor() {
        beforeInterceptors.sort(Comparator.comparingInt(BeforeInterceptor::order));
        return beforeInterceptors;
    }
}
