package com.g5niusx.rpc.common.observer;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BootstrapSubject implements RpcSubject {

    private List<NodeObserver> observerList = new ArrayList<>();
    private List<String>       list;

    @Override
    public void registryObserver(NodeObserver nodeObserver) {
        observerList.add(nodeObserver);
    }

    @Override
    public void removeObserver(NodeObserver nodeObserver) {
        observerList.remove(nodeObserver);
    }

    @Override
    public void notifyObserver() {
        observerList.forEach(nodeObserver -> nodeObserver.update(list));
    }

    /**
     * 发布消息
     *
     * @param list ip和端口信息列表
     */
    public void publish(List<String> list) {
        this.list = list;
        notifyObserver();
    }
}
