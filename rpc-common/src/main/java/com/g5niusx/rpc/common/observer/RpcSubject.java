package com.g5niusx.rpc.common.observer;


public interface RpcSubject {
    void registryObserver(NodeObserver nodeObserver);

    void removeObserver(NodeObserver nodeObserver);

    void notifyObserver();
}
