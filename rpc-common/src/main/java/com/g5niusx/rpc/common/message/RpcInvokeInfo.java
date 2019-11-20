package com.g5niusx.rpc.common.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * rpc调用所需要的参数
 *
 * @author g5niusx
 */
@Builder
@Getter
@Setter
public class RpcInvokeInfo implements Serializable {
    /**
     * 远程调用类名
     */
    private Class clazz;

    /**
     * 调用方法的入参类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 调用方法的入参
     */
    private Object[] parameters;
    /**
     * 调用的方法名
     */
    private String   methodName;
    /**
     * 远程调用类实现类名
     */
    private String   clazzName;
}
