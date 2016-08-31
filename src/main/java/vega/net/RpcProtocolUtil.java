package vega.net;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yanmo.yx on 2016/8/19.
 */
public class RpcProtocolUtil {
    private static final AtomicInteger msgId = new AtomicInteger(1);

    public static Integer getMsgId() {
        return msgId.getAndIncrement();
    }
}
