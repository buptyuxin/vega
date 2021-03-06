package vega.core.manager;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import vega.common.CallBack;
import vega.core.component.ZkComponent;
import vega.core.consumer.ConsumerService;
import vega.core.message.MessageCenter;
import vega.core.message.topic.ProviderChangeTopic;
import vega.core.message.topic.Topic;
import vega.core.register.Register;
import vega.core.register.ZkRegisterUtil;
import vega.core.register.registerType.ConsumerRegisterMsg;
import vega.core.register.registerType.RegisterMsg;

/**
 * 这个是服务的注册中心，用来注册服务的提供者和消费者，ZK实现
 *
 * Created by yanmo.yx on 2016/8/3.
 */
public class ZkConsumerManager implements Register {

    private ZkComponent zkComponent;
    private MessageCenter messageCenter;
    private ConsumerService consumerService;

    public ZkConsumerManager(ZkComponent zkComponent, MessageCenter messageCenter, ConsumerService consumerService) {
        this.zkComponent = zkComponent;
        this.messageCenter = messageCenter;
        this.consumerService = consumerService;
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
            ConsumerRegisterMsg consumerRegisterMsg = (ConsumerRegisterMsg) registerMsg;
            String interfaceName = consumerRegisterMsg.getInterface();
            String version = consumerRegisterMsg.getVersion();

            zkComponent.watchChild(ZkRegisterUtil.getProviderPath(interfaceName, version), new CallBack<PathChildrenCacheEvent.Type, PathChildrenCacheEvent>() {

                @Override
                public void doCallBack(PathChildrenCacheEvent.Type type, PathChildrenCacheEvent pathChildrenCacheEvent) {
                    String path = pathChildrenCacheEvent.getData().getPath();
                    String content = new String(pathChildrenCacheEvent.getData().getData());

                    if (type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                        return;
                    } else {
                        ProviderChangeTopic.ProviderChangeInfo providerChangeInfo = new ProviderChangeTopic.ProviderChangeInfo();
                        providerChangeInfo.setInterfaceName(ZkRegisterUtil.getProviderInterface(path));
                        providerChangeInfo.setVersion(ZkRegisterUtil.getProviderVersion(path));
                        providerChangeInfo.setProviderIp(ZkRegisterUtil.getProviderIp(content));
                        providerChangeInfo.setPort(ZkRegisterUtil.getProviderPort(content));
                        if (type.equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                            // 新增加了服务提供者
                            providerChangeInfo.setType(2);

                        } else if (type.equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                            // 删除了服务提供者
                            providerChangeInfo.setType(1);
                        } else {
                            return;
                        }
                        messageCenter.fire(new ProviderChangeTopic(providerChangeInfo));
                    }
                }
            });

            Topic topic = new ProviderChangeTopic(null);
            messageCenter.register(topic, consumerService);
        }
    }

    @Override
    public void cancel(RegisterMsg registerMsg) {

    }
}
