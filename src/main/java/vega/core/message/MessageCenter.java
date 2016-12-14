package vega.core.message;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import vega.core.message.topic.Topic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

/**
 * 内部事件总线
 *
 * Created by yanmo.yx on 2016/8/9.
 */
public class MessageCenter {

    private Map<Topic, List<MessageHandler>> relations = Maps.newHashMap();
    /**
     * {@see http://my.oschina.net/hosee/blog/615927}
     */
    private StampedLock lock = new StampedLock();

    public void fire(Topic topic) {
        long stamp = lock.tryOptimisticRead();  // 乐观锁
        List<MessageHandler> lists = Optional.ofNullable(relations.get(topic)).orElse(Lists.newArrayList());
        if (!lock.validate(stamp)) {    // 有写操作，所以上读锁
            try {
                lock.readLock();
                lists = Optional.ofNullable(relations.get(topic)).orElse(Lists.newArrayList());
            } finally {
                lock.unlockRead(stamp);
            }
        }
        lists.forEach(handler -> handler.handle(topic));
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
