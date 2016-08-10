package vega.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import vega.event.topic.Topic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public class EventCenter {

    private ConcurrentMap<Topic, List<EventHandler>> relations = Maps.newConcurrentMap();

    public void fire(Topic topic) {
        Optional.ofNullable(relations.get(topic)).ifPresent(handlers -> handlers.stream().forEach(handler -> handler.handle(topic)));
    }

    public void register(Topic topic, EventHandler eventHandler) {
        List<EventHandler> handlers = relations.get(topic);
        if (handlers == null) {
            handlers = Lists.newArrayList();
            relations.put(topic, handlers);
        }
        handlers.add(eventHandler);
    }
}
