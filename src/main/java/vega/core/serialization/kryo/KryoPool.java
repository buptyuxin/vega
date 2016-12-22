package vega.core.serialization.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yanmo.yx on 2016/8/23.
 */
public class KryoPool {

    private KryoFactory kryoFactory;

    private int maxTotal;

    private int minIdle;

    private long maxWaitMillis;

    private long minEvictableIdleTimeMillis;

    public KryoPool(int maxTotal, int minIdle, long maxWaitMillis, long minEvictableIdleTimeMillis) {
        this.maxTotal = maxTotal;
        this.minIdle = minIdle;
        this.maxWaitMillis = maxWaitMillis;
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void init() {
        kryoFactory = new KryoFactory(maxTotal, minIdle, maxWaitMillis, minEvictableIdleTimeMillis);
    }

    public void encode(final OutputStream out, final Object message) throws IOException {
        KryoSerialization kryoSerialization = new KryoSerialization(kryoFactory);
        kryoSerialization.serialize(out, message);
    }

    public Object decode(final InputStream in) throws IOException {
        KryoSerialization kryoSerialization = new KryoSerialization(kryoFactory);
        return kryoSerialization.deserialize(in);
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
}
