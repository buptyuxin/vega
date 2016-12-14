package vega.spring;

import org.springframework.beans.factory.InitializingBean;
import vega.core.provider.VegaProviderProxyFactory;

/**
 * Created by yanmo.yx on 2016/8/31.
 */
public class VegaSpringProviderBean implements InitializingBean {

    private Class<?> clazz;
    private String version;
    private Class<?> targetInterface;

    private VegaProviderProxyFactory vegaProviderProxyFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        vegaProviderProxyFactory = VegaProviderProxyFactory.singleton();
        target = vegaProviderProxyFactory.proxy(targetInterface, version);
    }

    public void setTargetInterface(Class<?> targetInterface) {
        this.targetInterface = targetInterface;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
