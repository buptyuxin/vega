package vega.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by yanmo.yx on 2016/8/23.
 */
public class KryoFactory {
    private final GenericObjectPool<Kryo> kryoPool;

    public KryoFactory(GenericObjectPool<Kryo> kryoPool) {
        this.kryoPool = new GenericObjectPool<Kryo>(new PooledKryoFactory());
    }

    public KryoFactory(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
        kryoPool = new GenericObjectPool<>(new PooledKryoFactory());
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        kryoPool.setConfig(config);
    }

    public Kryo getKryo() {
        try {
            return kryoPool.borrowObject();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void returnKryo(final Kryo kryo) {
        kryoPool.returnObject(kryo);
    }
}
