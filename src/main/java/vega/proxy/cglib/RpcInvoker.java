package vega.proxy.cglib;

import org.apache.commons.lang3.StringUtils;
import vega.common.MD5;
import vega.consumer.ConsumerService;
import vega.net.RpcRequest;
import vega.net.RpcResponse;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanmo.yx on 2016/8/24.
 */
public class RpcInvoker {

    private static final String SUB_DELIMITER = "+";

    private ConsumerService consumerService;
    private String interfaceName;
    private String version;
    private long timeout = 3000L; // 默认3s

    private Map<Method, String> methodMD5Maps = new HashMap<>();

    public RpcInvoker(ConsumerService consumerService, String interfaceName, String version, long timeout) {
        this.consumerService = consumerService;
        this.interfaceName = interfaceName;
        this.version = version;
        this.timeout = timeout;
    }

    public Object invoke(Method method, Object[] objects) throws Throwable {

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName(interfaceName);
        rpcRequest.setVersion(version);
        rpcRequest.setTimeout(timeout);
        rpcRequest.setArgs(convert2Serializable(objects));

        String md5 = methodMD5Maps.get(method);
        if (StringUtils.isBlank(md5)) {
            md5 = calcMethodMD5(method);
            methodMD5Maps.put(method, md5);
        }

        rpcRequest.setMethodMD5(md5);

        RpcResponse rpcResponse = consumerService.sendReq(rpcRequest);

        Throwable e = rpcResponse.getE();
        if (e != null) {
            throw e;
        }

        return rpcResponse.getRes();
    }

    private String calcMethodMD5(Method method) {

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

    private Serializable[] convert2Serializable(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return null;
        }

        Serializable[] res = new Serializable[objects.length];

        int i = 0;

        for (Object o : objects) {
            if (!Serializable.class.isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException("Serialization fail");
            }
            res[i++] = ((Serializable) o);
        }

        return res;
    }
}
