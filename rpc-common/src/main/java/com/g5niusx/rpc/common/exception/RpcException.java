package com.g5niusx.rpc.common.exception;

public class RpcException extends RuntimeException {
    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message) {
        super(message);
    }
}
