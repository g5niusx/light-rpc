package com.g5niusx.rpc.client;

/**
 * 当前连接信息的线程变量
 *
 * @author g5niusx
 */
public final class ConnectionHolder {
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String info) {
        THREAD_LOCAL.set(info);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static String get() {
        return THREAD_LOCAL.get();
    }

}
