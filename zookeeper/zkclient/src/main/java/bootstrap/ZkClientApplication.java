package bootstrap;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ZkClientApplication {

    private static final String CONNECT_STR = "127.0.0.1:2181", PARENT_PATH = "/top", PATH = "/top/codewood";

    public static void main(String[] args) {

        ZkClient zk = new ZkClient(CONNECT_STR);

        zk.subscribeDataChanges(PATH, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println(String.format("data-change, path: %s, data: %s", dataPath, data));
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("date-deleted, path: " + dataPath);
            }
        });

        zk.subscribeChildChanges(PARENT_PATH, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(String.format("%s child change.", PARENT_PATH));

                currentChilds.stream().forEach(child -> System.out.println("change: " + child));

            }
        });

        List<String> rootNodes = zk.getChildren("/");

        rootNodes.stream().forEach(System.out::println);

        if (!zk.exists(PARENT_PATH)) {
            zk.createPersistent(PARENT_PATH);
        }

        String result = zk.create(PATH, "代码坞", CreateMode.PERSISTENT);
        System.out.println("创建结果：" + result);
        zk.writeData(PATH, "代码坞-codewood");
        System.out.println("修改结果：" + result);

        result = zk.readData(PATH);
        System.out.println("读取结果：" + result);

        zk.delete(PATH);

    }

}
