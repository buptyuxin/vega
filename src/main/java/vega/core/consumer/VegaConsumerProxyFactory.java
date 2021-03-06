package vega.core.consumer;

import net.sf.cglib.proxy.Enhancer;
import vega.core.proxy.cglib.RpcInterceptor;
import vega.core.proxy.cglib.RpcInvoker;

/**
 * Created by yanmo.yx on 2016/7/11.
 */
public class VegaConsumerProxyFactory {

    private static volatile VegaConsumerProxyFactory singleton;

    private ConsumerService consumerService;

    /**
     * 单例
     *
     * @return
     * @throws Exception
     */
    public static VegaConsumerProxyFactory singleton() throws Exception {
        if (null == singleton) {
            synchronized (VegaConsumerProxyFactory.class) {
                if (null == singleton) {
                    singleton = new VegaConsumerProxyFactory();
                }
            }
        }
        return singleton;
    }

    private VegaConsumerProxyFactory() {
        try {
            consumerService = new ConsumerService();
            consumerService.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成远程调用的proxy
     * 使用cglib
     *
     * @param clazz
     * @param version
     * @param timeout
     * @return
     */
    public Object proxy(Class clazz, String version, long timeout) {

        RpcInvoker rpcInvoker = new RpcInvoker(consumerService, clazz.getName(), version, timeout);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new RpcInterceptor(rpcInvoker));
        return enhancer.create();
    }
}
