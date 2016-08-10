package vega.message.topic;

import vega.message.info.ProviderChangeInfo;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public class ConsumerTopic extends Topic<ProviderChangeInfo> {

    public ConsumerTopic(ProviderChangeInfo content) {
        super(content);
    }
}
