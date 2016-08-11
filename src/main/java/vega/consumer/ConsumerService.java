package vega.consumer;

import vega.component.ZkComponent;
import vega.message.MessageCenter;
import vega.message.MessageHandler;
import vega.message.info.ProviderChangeInfo;
import vega.message.topic.ConsumerTopic;
import vega.message.topic.Topic;
import vega.manager.ZkConsumerManager;

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
    }

    @Override
    public void handle(Topic<?> topic) {
        if (topic instanceof ConsumerTopic) {
            ConsumerTopic consumerTopic = (ConsumerTopic) topic;
            ProviderChangeInfo providerChangeInfo = consumerTopic.getContent();
            if (providerChangeInfo.isAdd()) {
                handleProvideAdd(providerChangeInfo);
            } else if (providerChangeInfo.isDel()) {
                handleProvideDel(providerChangeInfo);
            }
        }
    }

    public void handleProvideAdd(ProviderChangeInfo providerChangeInfo) {

    }

    public void handleProvideDel(ProviderChangeInfo providerChangeInfo) {

    }
}
