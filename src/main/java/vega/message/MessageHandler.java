package vega.message;

import vega.message.topic.Topic;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public interface MessageHandler {

    public void handle(Topic<?> topic);
}
