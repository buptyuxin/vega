package vega.register;

import vega.register.registerType.RegisterMsg;

/**
 * Created by yanmo.yx on 2016/8/3.
 */
public interface Register {

    public void init();

    public void shutdown();

    // 向注册中心注册消息
    public void register(RegisterMsg registerMsg);

    // 取消注册消息
    public void cancel(RegisterMsg registerMsg);
}
