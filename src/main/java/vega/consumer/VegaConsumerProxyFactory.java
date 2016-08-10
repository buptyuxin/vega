package vega.consumer;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import vega.component.RpcMethodInvokerComponent;
import vega.component.ZkComponent;
import vega.message.MessageCenter;
import vega.manager.SubscribeManager;
import vega.proxy.cglib.RpcInterceptor;
import vega.register.ZkRegister;

import java.lang.reflect.Method;

/**
 * Created by yanmo.yx on 2016/7/11.
 */
public class VegaConsumerProxyFactory {

    private static volatile VegaConsumerProxyFactory singleton;

    private ZkRegister zkRegister;
    private MessageCenter messageCenter;
    private ZkComponent zkComponent;

    private SubscribeManager subscribeManager;

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
            messageCenter = new MessageCenter();
            zkComponent = new ZkComponent();

            zkRegister = new ZkRegister(zkComponent, messageCenter);
            zkRegister.init();

            subscribeManager.init();
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

        RpcMethodInvokerComponent rpcMethodInvokerComponent = new RpcMethodInvokerComponent(group, version, timeout);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallbacks(new Callback[] {
                new RpcInterceptor(rpcMethodInvokerComponent), NoOp.INSTANCE
        });
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                return 0;
            }
        });
        return enhancer.create();
    }
}
