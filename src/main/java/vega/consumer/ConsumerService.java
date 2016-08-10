package vega.consumer;

import vega.component.ZkComponent;
import vega.message.MessageCenter;
import vega.message.MessageHandler;
import vega.message.info.ProviderChangeInfo;
import vega.message.topic.ConsumerTopic;
import vega.message.topic.Topic;
import vega.register.ZkConsumerRegister;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ConsumerService implements MessageHandler {

    private MessageCenter messageCenter;

    private ZkConsumerRegister zkConsumerRegister;

    public void init() {
        ZkComponent zkComponent = new ZkComponent();

        messageCenter = new MessageCenter();

        zkConsumerRegister = new ZkConsumerRegister(zkComponent, messageCenter, this);
        zkConsumerRegister.init();
    }

    @Override
    public void handle(Topic<?> topic) {
        if (topic instanceof ConsumerTopic) {
            ConsumerTopic consumerTopic = (ConsumerTopic) topic;
            ProviderChangeInfo providerChangeInfo = consumerTopic.getContent();
        }
    }
}
