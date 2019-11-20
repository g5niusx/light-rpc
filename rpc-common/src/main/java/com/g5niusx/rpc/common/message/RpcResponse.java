package com.g5niusx.rpc.common.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class RpcResponse implements Serializable {
    private String resultCode;
    private Object result;
    private String id;

}
