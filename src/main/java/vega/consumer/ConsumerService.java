package vega.consumer;

import vega.component.ZkComponent;
import vega.manager.ChannelManager;
import vega.manager.ZkConsumerManager;
import vega.message.MessageCenter;
import vega.message.MessageHandler;
import vega.message.topic.ConsumerTopic;
import vega.message.topic.ProviderChangeTopic;
import vega.message.topic.Topic;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ConsumerService implements MessageHandler {

    private MessageCenter messageCenter;

    private ZkConsumerManager zkConsumerManager;

    public void init() {
        ZkComponent zkComponent = new ZkComponent();

        messageCenter = new MessageCenter();

        zkConsumerManager = new ZkConsumerManager(zkComponent, messageCenter, this);
        zkConsumerManager.init();

        ChannelManager channelManager = new ChannelManager();
    }

    @Override
    public void handle(Topic<?> topic) {
        if (acceptTopic(topic)) {
            if (topic instanceof ProviderChangeTopic) {
                ProviderChangeTopic providerChangeTopic = (ProviderChangeTopic) topic;
                ProviderChangeTopic.ProviderChangeInfo providerChangeInfo = providerChangeTopic.getContent();
                if (providerChangeInfo.isAdd()) {
                    handleProvideAdd(providerChangeInfo);
                } else if (providerChangeInfo.isDel()) {
                    handleProvideDel(providerChangeInfo);
                }
//            } else if () {

            }
        }
    }

    private void handleProvideAdd(ProviderChangeTopic.ProviderChangeInfo providerChangeInfo) {

    }

    private void handleProvideDel(ProviderChangeTopic.ProviderChangeInfo providerChangeInfo) {

    }

    private boolean acceptTopic(Topic topic) {
        return ConsumerTopic.class.isAssignableFrom(topic.getClass());
    }
}
