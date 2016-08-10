package vega.manager;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import vega.config.ConfigUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订阅关系
 *
 * Created by yanmo.yx on 2016/7/19.
 */
public class SubscribeManager {

    private Map<String, CuratorFramework> subscribeRelations = new HashMap<>();

    public void init() {

    }

    public void register(String serviceName) {

        if (StringUtils.isBlank(serviceName)) {
            return;
        }

        CuratorFramework curatorFramework = subscribeRelations.get(serviceName);
        if (curatorFramework == null) {
            try {
                curatorFramework = CuratorFrameworkFactory.newClient(ConfigUtil.getZkServers(), new ExponentialBackoffRetry(1000, 3));
                curatorFramework.start();

                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/test", "sss".getBytes());

                subscribeRelations.put(serviceName, curatorFramework);

                if (curatorFramework.checkExists().forPath(serviceName) == null) {
                    // 节点还没建立

                }

                PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, serviceName, true);
                pathChildrenCache.start();

                pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        switch (event.getType()) {
                            case CHILD_ADDED: {
                                ZKPaths.getNodeFromPath(event.getData().getPath());
                                break;
                            }
                            case CHILD_REMOVED: {
                                break;
                            }
                            case CHILD_UPDATED: {
                                break;
                            }
                        }
                    }
                });

                List<ChildData> datas = pathChildrenCache.getCurrentData();
                if (datas != null) {
                    for (ChildData data : datas) {
                        String s = new String(data.getData());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
