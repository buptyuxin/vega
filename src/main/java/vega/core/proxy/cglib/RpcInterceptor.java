package vega.core.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by yanmo.yx on 2016/7/12.
 */
public class RpcInterceptor implements MethodInterceptor {

    private RpcInvoker rpcInvoker;

    public RpcInterceptor(RpcInvoker rpcInvoker) {
        this.rpcInvoker = rpcInvoker;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        return rpcInvoker.invoke(method, objects);
    }
}
