package vega.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import vega.component.RpcMethodInvokerComponent;

import java.lang.reflect.Method;

/**
 * Created by yanmo.yx on 2016/7/12.
 */
public class RpcInterceptor implements MethodInterceptor {

    private RpcMethodInvokerComponent rpcMethodInvokerComponent;

    public RpcInterceptor(RpcMethodInvokerComponent rpcMethodInvokerComponent) {
        this.rpcMethodInvokerComponent = rpcMethodInvokerComponent;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        return rpcMethodInvokerComponent.invoke(method, objects);
    }
}
