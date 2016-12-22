package vega.core.proxy.cglib;

import org.apache.commons.lang3.StringUtils;
import vega.core.consumer.ConsumerService;
import vega.core.transport.RpcProtocolUtil;
import vega.core.transport.RpcRequest;
import vega.core.transport.RpcResponse;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanmo.yx on 2016/8/24.
 */
public class RpcInvoker {

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

        String msgId = RpcProtocolUtil.getMsgId();
        String md5 = methodMD5Maps.get(method);
        if (StringUtils.isBlank(md5)) {
            md5 = RpcProtocolUtil.calcMethodMD5(method);
            methodMD5Maps.put(method, md5);
        }

        RpcRequest rpcRequest = RpcRequest.RpcRequestBuilder.build(msgId, interfaceName, version, md5, convert2Serializable(objects), timeout);

        RpcResponse rpcResponse = consumerService.sendReq(rpcRequest);

        Throwable e = rpcResponse.getE();
        if (e != null) {
            throw e;
        }

        return rpcResponse.getRes();
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
