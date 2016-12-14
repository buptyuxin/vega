package vega.core.message.topic;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public abstract class ConsumerTopic<T> extends Topic<T> {

    public ConsumerTopic(T content) {
        super(content);
    }
}
