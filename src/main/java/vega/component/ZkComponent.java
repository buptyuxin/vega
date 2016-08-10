package vega.component;

import common.CallBack;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.Watcher;
import vega.config.ConfigUtil;

/**
 * Created by yanmo.yx on 2016/8/3.
 */
public class ZkComponent implements Component{

    private CuratorFramework zkClient;

    @Override
    public void init() {
        zkClient = CuratorFrameworkFactory.newClient(ConfigUtil.getZkServers(), new ExponentialBackoffRetry(1000, 3));
        zkClient.start();
    }

    @Override
    public void shutdown() {
        zkClient.close();
    }

    public void watch(String path, CallBack callBack) {
        try {
            if (zkClient.checkExists().forPath(path) == null) {
                // 节点不存在
                zkClient.create().creatingParentsIfNeeded().forPath(path);
            }
            zkClient.getChildren().usingWatcher((Watcher) event -> callBack.doCallBack(event)).inBackground().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void watchChild(String path, CallBack callBack) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pathChildrenCache.getListenable().addListener((client, event) -> {
            switch (event.getType()) {
                case CHILD_ADDED:
                    callBack.doCallBack(PathChildrenCacheEvent.Type.CHILD_ADDED, event);
                    break;
                case CHILD_UPDATED:
                    callBack.doCallBack(PathChildrenCacheEvent.Type.CHILD_UPDATED, event);
                    break;
                case CHILD_REMOVED:
                    callBack.doCallBack(PathChildrenCacheEvent.Type.CHILD_REMOVED, event);
                    break;
                default:
                    break;
            }
        });

    }

    public static void main(String[] args) {
        CuratorFramework zkClient = CuratorFrameworkFactory.newClient("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", new ExponentialBackoffRetry(1000, 3));
        zkClient.start();

        zkClient.close();
    }
}
