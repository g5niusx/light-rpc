package com.g5niusx.rpc.common.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * rpc请求对象
 *
 * @author g5niusx
 */
@Getter
@Setter
@Builder
@ToString
public class RpcRequest implements Serializable {
    /**
     * 请求调用的id
     */
    private String              id;
    /**
     * rpc调用的具体信息
     */
    private RpcInvokeInfo       rpcInvokeInfo;
    /**
     * rpc扩展信息
     */
    private Map<Object, Object> rpcContext;
    /**
     * 请求时间
     */
    private Date                date;
}
