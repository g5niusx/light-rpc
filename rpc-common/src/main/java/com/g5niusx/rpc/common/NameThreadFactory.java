package com.g5niusx.rpc.common;

import java.util.concurrent.atomic.AtomicInteger;

public class NameThreadFactory implements java.util.concurrent.ThreadFactory {

    private final        String        name;
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    public NameThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, String.format("%s - thread - %d", "rpc-" + name, ATOMIC_INTEGER.incrementAndGet()));
    }
}
