package boostrap;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ApachZookeeperApplication {

    private static final String CONNECT_STR = "127.0.0.1:2181", PARENT_PATH = "/top", PATH = "/top/codewood";

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println(String.format("event: %s, path: %s", event.getType(), event.getPath()));
                }
            }
        };

        ZooKeeper zooKeeper = new ZooKeeper(CONNECT_STR, 5000, watcher);


        Stat stat = zooKeeper.exists(PARENT_PATH, watcher);
        if (stat == null) {
            zooKeeper.create(PARENT_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        String result = zooKeeper.create(PATH, "apache".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        byte[] bytes = zooKeeper.getData(PATH, true, null);
        result = new String(bytes);
        System.out.println("result: " + result);

        zooKeeper.setData(PATH, "apache-zookeeper".getBytes(StandardCharsets.UTF_8), -1);

        bytes = zooKeeper.getData(PATH, true, null);
        result = new String(bytes);
        System.out.println("result: " + result);

        zooKeeper.delete(PATH, -1);
        zooKeeper.delete(PARENT_PATH, -1);

        System.in.read();

    }

}
