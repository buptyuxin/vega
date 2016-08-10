package vega.register;

import common.CallBack;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import vega.component.ZkComponent;
import vega.message.MessageCenter;
import vega.message.MessageHandler;
import vega.message.topic.ConsumerTopic;
import vega.message.topic.Topic;
import vega.register.registerType.ConsumerRegisterMsg;
import vega.register.registerType.RegisterMsg;

/**
 * 这个是服务的注册中心，用来注册服务的提供者和消费者，ZK实现
 *
 * Created by yanmo.yx on 2016/8/3.
 */
public class ZkRegister implements Register, MessageHandler {

    private ZkComponent zkComponent;
    private MessageCenter messageCenter;

    public ZkRegister(ZkComponent zkComponent, MessageCenter messageCenter) {
        this.zkComponent = zkComponent;
        this.messageCenter = messageCenter;
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
                public void doCallBack(PathChildrenCacheEvent.Type type, PathChildrenCacheEvent pathChildrenCacheEvent) {
                    String path = pathChildrenCacheEvent.getData().getPath();
                    String content = new String(pathChildrenCacheEvent.getData().getData());
                    if (type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                        return;
                    } else if (type.equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                        // 新增加了服务提供者
                        messageCenter.fire();
                    } else if (type.equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                        // 删除了服务提供者

                    } else {
                        return;
                    }
                }
            });

            ConsumerTopic consumerTopic = new ConsumerTopic(method + "/" + version);
            messageCenter.register(consumerTopic, this);
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
