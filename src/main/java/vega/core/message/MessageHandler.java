package vega.core.message;

import vega.core.message.topic.Topic;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public interface MessageHandler<T extends Topic> {

    void handle(T topic);
}
