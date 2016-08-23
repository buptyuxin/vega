package vega.message.topic;

/**
 * Created by yanmo.yx on 2016/8/23.
 */
public class ConsumerRegisterTopic extends ConsumerTopic<ConsumerRegisterTopic.ConsumerRegisterInfo> {

    public ConsumerRegisterTopic(ConsumerRegisterInfo content) {
        super(content);
    }

    public static final class ConsumerRegisterInfo {
        private String interfaceName;
        private String version;
    }
}
