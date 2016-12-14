package vega.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import vega.core.consumer.VegaConsumerProxyFactory;

/**
 * Created by yanmo.yx on 2016/7/11.
 */
public class VegaSpringConsumerFactoryBean implements InitializingBean, FactoryBean {

    private Class<?> targetInterface;
    private String version;
    private long defaultTimeout;

    private Object target;
    private VegaConsumerProxyFactory vegaConsumerProxyFactory;

    public Object getObject() throws Exception {
        return target;
    }

    public Class<?> getObjectType() {
        return targetInterface;
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        vegaConsumerProxyFactory = VegaConsumerProxyFactory.singleton();
        target = vegaConsumerProxyFactory.proxy(targetInterface, version, defaultTimeout);
    }

    public void setTargetInterface(String targetInterface) {
        try {
            this.targetInterface = Class.forName(targetInterface);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(targetInterface);
        }
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDefaultTimeout(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }
}
