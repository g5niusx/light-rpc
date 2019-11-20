package com.g5niusx.rpc.common.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 提供给服务端的注册类
 *
 * @author g5niusx
 */
@Slf4j
public final class ServerRegistry {
    private static final String    PATH = "/g5niusx-rpc";
    private final        String    url;
    private              ZooKeeper zooKeeper;

    public ServerRegistry(String ip, Integer port) {
        this.url = ip + ":" + port;
        this.zooKeeper = connect();
        create(PATH, "rpc根目录".getBytes(UTF_8));
    }

    private ZooKeeper connect() {
        ZooKeeper zooKeeper = null;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(url, 1000 * 3, watchedEvent -> countDownLatch.countDown());
            countDownLatch.await();
        } catch (Exception e) {
            log.error("连接zookeeper异常,地址为:{}", url, e);
        }

        return zooKeeper;
    }

    public void create(String path, byte[] bytes) {
        try {
            if (!zooKeeper.getState().isConnected() || !zooKeeper.getState().isAlive()) {
                this.zooKeeper = connect();
            }
            Stat s = zooKeeper.exists(path, true);
            if (s == null) {
                CreateMode createMode = CreateMode.EPHEMERAL;
                if (PATH.equals(path)) {
                    createMode = CreateMode.PERSISTENT;
                }
                String info = zooKeeper.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
                log.info("创建了节点:{}", info);
            } else {
                log.warn(path + "已经存在!!!");
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("创建节点[{}]异常", path, e);
        }
    }
}
