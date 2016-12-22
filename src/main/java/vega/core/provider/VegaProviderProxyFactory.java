package vega.core.provider;

import net.sf.cglib.proxy.Enhancer;
import vega.core.proxy.cglib.RpcInterceptor;
import vega.core.proxy.cglib.RpcInvoker;

/**
 * Created by yanmo.yx on 2016/7/19.
 */
public class VegaProviderProxyFactory {

    private static class SingletonHolder {
        private static final VegaProviderProxyFactory INSTATNCE = new VegaProviderProxyFactory();
    }

    private VegaProviderProxyFactory() {

    }

    /**
     * 单例
     * @return
     */
    public static VegaProviderProxyFactory singleton() {
        return SingletonHolder.INSTATNCE;
    }

    public Object proxy(Class clazz, String version) {

//        RpcInvoker rpcInvoker = new RpcInvoker(consumerService, clazz.getName(), version, timeout);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
//        enhancer.setCallback(new RpcInterceptor(rpcInvoker));
        return enhancer.create();
    }

}
