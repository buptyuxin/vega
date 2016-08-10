package vega.register;

import vega.common.CallBack;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import vega.component.ZkComponent;
import vega.consumer.ConsumerService;
import vega.message.MessageCenter;
import vega.message.info.ProviderChangeInfo;
import vega.message.topic.ConsumerTopic;
import vega.register.registerType.ConsumerRegisterMsg;
import vega.register.registerType.RegisterMsg;

/**
 * 这个是服务的注册中心，用来注册服务的提供者和消费者，ZK实现
 *
 * Created by yanmo.yx on 2016/8/3.
 */
public class ZkConsumerRegister implements Register {

    private ZkComponent zkComponent;
    private MessageCenter messageCenter;
    private ConsumerService consumerService;

    public ZkConsumerRegister(ZkComponent zkComponent, MessageCenter messageCenter, ConsumerService consumerService) {
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
                        ProviderChangeInfo providerChangeInfo = new ProviderChangeInfo();
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
                        messageCenter.fire(new ConsumerTopic(providerChangeInfo));
                    }
                }
            });

            ConsumerTopic consumerTopic = new ConsumerTopic(null);
            messageCenter.register(consumerTopic, consumerService);
        }
    }

    @Override
    public void cancel(RegisterMsg registerMsg) {

    }
}
