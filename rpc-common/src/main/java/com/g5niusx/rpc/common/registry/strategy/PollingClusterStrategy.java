package com.g5niusx.rpc.common.registry.strategy;

import com.g5niusx.rpc.common.exception.RpcException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮询算法
 *
 * @author g5niusx
 */
@Slf4j
public class PollingClusterStrategy implements ClusterStrategy {

    private int index;

    private Lock lock = new ReentrantLock();

    @Override
    public String cluster(@NonNull List<String> list) {
        String string = null;
        try {
            lock.tryLock(10, TimeUnit.MILLISECONDS);
            // 如果当前的计数器大于注册的信息则清零
            if (index >= list.size()) {
                index = 0;
            }
            string = list.get(index);
            index++;
        } catch (Exception e) {
            log.error("轮询算法异常", e);
        } finally {
            lock.unlock();
        }
        if (string == null && list.isEmpty()) {
            throw new RpcException("没有可用的远程节点!");
        }
        if (string == null) {
            string = list.get(0);
        }
        return string;
    }
}