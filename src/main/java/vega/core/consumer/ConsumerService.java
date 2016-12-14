package vega.core.consumer;

import vega.core.component.ClientChannelComponent;
import vega.core.component.ZkComponent;
import vega.core.manager.ChannelManager;
import vega.core.manager.ZkConsumerManager;
import vega.core.message.MessageCenter;
import vega.core.message.MessageHandler;
import vega.core.message.topic.ConsumerTopic;
import vega.core.message.topic.ProviderChangeTopic;
import vega.core.message.topic.Topic;
import vega.core.net.RpcRequest;
import vega.core.net.RpcResponse;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ConsumerService implements MessageHandler {

    private MessageCenter messageCenter;

    private ZkConsumerManager zkConsumerManager;
    private ChannelManager channelManager;

    public void init() {
        ZkComponent zkComponent = new ZkComponent();

        messageCenter = new MessageCenter();

        zkConsumerManager = new ZkConsumerManager(zkComponent, messageCenter, this);
        zkConsumerManager.init();

        ClientChannelComponent clientChannelComponent = new ClientChannelComponent();

        channelManager = new ChannelManager(clientChannelComponent, messageCenter, this);
        channelManager.init();
    }

    @Override
    public void handle(Topic<?> topic) {
        if (!acceptTopic(topic)) {
            return;
        }
        if (topic instanceof ProviderChangeTopic) {
            ProviderChangeTopic providerChangeTopic = (ProviderChangeTopic) topic;
            ProviderChangeTopic.ProviderChangeInfo providerChangeInfo = providerChangeTopic.getContent();
            if (providerChangeInfo.isAdd()) {
                handleProvideAdd(providerChangeInfo);
            } else if (providerChangeInfo.isDel()) {
                handleProvideDel(providerChangeInfo);
            }
//        } else if () {

        }
    }

    public RpcResponse sendReq(RpcRequest rpcRequest) {
        return channelManager.sendReq(rpcRequest);
    }

    private void handleProvideAdd(ProviderChangeTopic.ProviderChangeInfo providerChangeInfo) {
        String interfaceName = providerChangeInfo.getInterfaceName();
        String serverIp = providerChangeInfo.getProviderIp();
        String port = providerChangeInfo.getPort();
        String version = providerChangeInfo.getVersion();
        channelManager.addChannel(interfaceName, version, serverIp, port);
    }

    private void handleProvideDel(ProviderChangeTopic.ProviderChangeInfo providerChangeInfo) {
        String interfaceName = providerChangeInfo.getInterfaceName();
        String serverIp = providerChangeInfo.getProviderIp();
        String port = providerChangeInfo.getPort();
        String version = providerChangeInfo.getVersion();
        channelManager.delChannel(interfaceName, version, serverIp, port);
    }

    private boolean acceptTopic(Topic topic) {
        return ConsumerTopic.class.isAssignableFrom(topic.getClass());
    }
}
