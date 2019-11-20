package com.g5niusx.rpc.common.exception;

public class RpcTimeoutException extends RuntimeException {
    public RpcTimeoutException(Throwable cause) {
        super(cause);
    }

    public RpcTimeoutException(String message) {
        super(message);
    }
}
