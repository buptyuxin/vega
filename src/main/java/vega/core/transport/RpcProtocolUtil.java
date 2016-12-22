package vega.core.transport;

import vega.common.MD5;
import vega.config.ConfigUtil;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yanmo.yx on 2016/8/19.
 */
public class RpcProtocolUtil {

    private static final String SUB_DELIMITER = "+";

    private static final AtomicInteger msgId = new AtomicInteger(1);

    public static String getMsgId() {
        return ConfigUtil.getInetSocketAddress() + SUB_DELIMITER + msgId.getAndIncrement();
    }

    public static String calcMethodMD5(Method method) {

        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append(SUB_DELIMITER);
        sb.append(method.getName());
        for (Class cls : method.getParameterTypes()) {
            sb.append(SUB_DELIMITER);
            sb.append(cls.getName());
        }

        return MD5.sign(sb.toString(), "", "utf-8");
    }
}
