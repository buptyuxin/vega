package vega.message;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import vega.message.topic.Topic;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.StampedLock;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public class MessageCenter {

    private ConcurrentMap<Topic, List<MessageHandler>> relations = Maps.newConcurrentMap();
    /**
     * http://my.oschina.net/hosee/blog/615927
     */
    private StampedLock lock = new StampedLock();

    public void fire(Topic topic) {
        long stamp = lock.tryOptimisticRead();
        if (!lock.validate(stamp)) {
            try {
                lock.readLock();
                Optional.ofNullable(relations.get(topic)).ifPresent(handlers -> handlers.stream().forEach(handler -> handler.handle(topic)));
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }

    public void register(Topic topic, MessageHandler messageHandler) {
        List<MessageHandler> handlers = relations.get(topic);
        long stamp = lock.writeLock();
        try {
            if (handlers == null) {
                handlers = Lists.newArrayList();
                relations.put(topic, handlers);
            }
            handlers.add(messageHandler);
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
