package bootstrap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;

public class CuratorApplication {

    private static final String CONNECT_STR = "127.0.0.1:2181", PARENT_PATH = "/top", PATH = "/top/codewood";

    public static void main(String[] args) throws Exception {
        CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_STR)
                .connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        curator.start();

        PathChildrenCache cache = new PathChildrenCache(curator, PARENT_PATH, true);

        cache.start();
        cache.rebuild();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                System.out.println(String.format("节点发生了变化：%s, 数据：%s", event.getType(), new String(event.getData().getData())));
            }
        });


//        CuratorCache curatorCache = CuratorCache.build(curator, PARENT_PATH);
//        curatorCache.start();
//        curatorCache.listenable().addListener(CuratorCacheListener.builder()
//                .forChanges(new CuratorCacheListenerBuilder.ChangeListener() {
//                    @Override
//                    public void event(ChildData oldNode, ChildData node) {
//                        System.out.println(String.format("node change, old: %s, now: %s", oldNode, node));
//                    }
//                })
//                .build());

        Stat stat = curator.checkExists().forPath(PATH);
        if (stat != null) {
            curator.delete().forPath(PATH);
        }

        System.in.read();
        curator.create()
                .withMode(CreateMode.PERSISTENT)
                .forPath(PATH, "代码坞".getBytes(StandardCharsets.UTF_8));

        byte[] bytes = curator.getData().forPath(PATH);
        System.out.println("数据：" + new String(bytes));

        curator.delete().forPath(PATH);
    }

}
