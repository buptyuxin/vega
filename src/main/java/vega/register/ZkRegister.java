package vega.register;

import common.CallBack;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.Watcher.Event;
import vega.component.ZkComponent;
import vega.event.EventCenter;
import vega.event.EventHandler;
import vega.event.topic.ConsumerTopic;
import vega.event.topic.Topic;
import vega.register.registerType.ConsumerRegisterMsg;
import vega.register.registerType.RegisterMsg;

/**
 * Created by yanmo.yx on 2016/8/3.
 */
public class ZkRegister implements Register, EventHandler {

    private ZkComponent zkComponent;
    private EventCenter eventCenter;

    public ZkRegister(ZkComponent zkComponent, EventCenter eventCenter) {
        this.zkComponent = zkComponent;
        this.eventCenter = eventCenter;
    }

    @Override
    public void init() {
        zkComponent.init();
    }

    @Override
    public void shutdown() {
        zkComponent.shutdown();
    }

    @Override
    public void register(RegisterMsg registerMsg) {
        if (registerMsg instanceof ConsumerRegisterMsg) {
            ConsumerRegisterMsg consumerRegisterType = (ConsumerRegisterMsg) registerMsg;
            ConsumerRegisterMsg.ConsumerMsgContent content = consumerRegisterType.getContent();
            String method = content.getMethod();
            String version = content.getVersion();

            zkComponent.watchChild(RegisterUtil.getMethodPath(method, version), new CallBack<PathChildrenCacheEvent.Type, PathChildrenCacheEvent>() {
                @Override
                public void doCallBack(PathChildrenCacheEvent.Type type) {
                    return;
                }

                @Override
                public void doCallBack(PathChildrenCacheEvent.Type type, PathChildrenCacheEvent pathChildrenCacheEvent) {
                    String path = pathChildrenCacheEvent.getData().getPath();
                    String content = new String(pathChildrenCacheEvent.getData().getData());
                    if (type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                        return;
                    } else if (type.equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                        // 新增加了服务提供者

                    } else if (type.equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                        // 删除了服务提供者

                    } else {
                        return;
                    }
                }
            });

            ConsumerTopic consumerTopic = new ConsumerTopic(method + "/" + version);
            eventCenter.register(consumerTopic, this);
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void handle(Topic topic) {
        if (topic instanceof ConsumerTopic) {
            ConsumerTopic consumerTopic = (ConsumerTopic) topic;
            String content = consumerTopic.getContent();
        }
    }
}
