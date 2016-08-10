package vega.event;

import vega.event.topic.Topic;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public interface EventHandler {

    public void handle(Topic<?> topic);
}
