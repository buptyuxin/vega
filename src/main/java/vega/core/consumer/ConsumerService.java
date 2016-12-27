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
import vega.core.transport.RpcRequest;
import vega.core.transport.RpcResponse;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ConsumerService implements MessageHandler<ConsumerTopic<ProviderChangeTopic.ProviderChangeInfo>> {

    private MessageCenter messageCenter;

    private ZkConsumerManager zkConsumerManager;
    private ChannelManager channelManager;

    public void init() {
        ZkComponent zkComponent = ZkComponent.singleton();

        messageCenter = new MessageCenter();

        zkConsumerManager = new ZkConsumerManager(zkComponent, messageCenter, this);
        zkConsumerManager.init();

        ClientChannelComponent clientChannelComponent = new ClientChannelComponent();

        channelManager = new ChannelManager(clientChannelComponent, messageCenter, this);
        channelManager.init();
    }

    @Override
    public void handle(ConsumerTopic<ProviderChangeTopic.ProviderChangeInfo> topic) {
        if (topic instanceof ProviderChangeTopic) {
            ProviderChangeTopic.ProviderChangeInfo providerChangeInfo = topic.getContent();
            if (providerChangeInfo.isAdd()) {
                handleProvideAdd(providerChangeInfo);
            } else if (providerChangeInfo.isDel()) {
                handleProvideDel(providerChangeInfo);
            }
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
}
