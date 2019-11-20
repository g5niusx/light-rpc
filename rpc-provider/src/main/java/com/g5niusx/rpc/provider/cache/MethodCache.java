package com.g5niusx.rpc.provider.cache;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sf.cglib.reflect.FastClass;

import java.io.Serializable;

/**
 * 缓存方法
 */
@Getter
@Setter
@Builder
public class MethodCache implements Serializable {
    private FastClass  fastClass;
    private String     methodName;
    private Class<?>[] parameterTypes;
}
