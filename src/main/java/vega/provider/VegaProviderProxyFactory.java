package vega.provider;

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

}
