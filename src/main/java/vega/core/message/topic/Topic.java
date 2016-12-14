package vega.core.message.topic;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public abstract class Topic<T> {

    private final T content;

    public Topic(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
