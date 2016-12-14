package vega.core.register.registerType;

/**
 * Created by yanmo.yx on 2016/8/3.
 */
public class RegisterMsg<T> {

    private final T content;

    public RegisterMsg(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
