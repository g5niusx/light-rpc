package com.g5niusx.rpc.common.observer;

import java.util.List;

@FunctionalInterface
public interface NodeObserver {

    void update(List<String> list);

}
