package com.g5niusx.rpc.common.registry;

import com.g5niusx.rpc.common.observer.BootstrapSubject;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 给客户端提供的服务发现类
 *
 * @author g5niusx
 */
@Slf4j
public final class ServerDiscovery {
    public static final String           PATH = "/g5niusx-rpc";
    private final       String           url;
    private             ZooKeeper        zooKeeper;
    private final       BootstrapSubject bootstrapSubject;

    public ServerDiscovery(String ip, Integer port, BootstrapSubject bootstrapSubject) {
        this.bootstrapSubject = bootstrapSubject;
        this.url = ip + ":" + port;
        this.zooKeeper = connect();
    }

    private ZooKeeper connect() {
        ZooKeeper zooKeeper = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(url, 1000 * 30, watchedEvent -> {
                countDownLatch.countDown();
                log.info("zookeeper连接成功:{}", watchedEvent.toString());
            });
            countDownLatch.await();
        } catch (Exception e) {
            log.error("连接zookeeper异常,地址为:{}", url, e);
        }

        return zooKeeper;
    }

    public List<String> discovery(String path) {
        if (!zooKeeper.getState().isConnected() || !zooKeeper.getState().isAlive()) {
            this.zooKeeper = connect();
        }
        return getData(path);
    }

    private List<String> getData(String path) {
        try {
            return zooKeeper.getChildren(path, event -> {
                // 节点发生变化或者zk重新连接，需要获取最新的数据
                if (event.getType().equals(Watcher.Event.EventType.NodeChildrenChanged) || Watcher.Event.KeeperState.SyncConnected.equals(event.getState())) {
                    List<String> data = getData(path);
                    if (data != null && !data.isEmpty()) {
                        bootstrapSubject.publish(data);
                    } else {
                        log.warn("没有从zookeeper找到节点，将使用已经存在的节点...");
                    }
                }
            });
        } catch (KeeperException | InterruptedException e) {
            log.error("获取数据异常", e);
        }
        return null;
    }
}
