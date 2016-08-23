package vega.serialization.kryo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * Created by yanmo.yx on 2016/8/23.
 */
public class KryoPool {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    private KryoFactory kryoFactory;

    private int maxTotal;

    private int minIdle;

    private long maxWaitMillis;

    private long minEvictableIdleTimeMillis;

    public void init() {
        kryoFactory = new KryoFactory(maxTotal, minIdle, maxWaitMillis, minEvictableIdleTimeMillis);
    }

    public void encode(final ByteBuf out, final Object message) throws IOException {
        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        bout.write(LENGTH_PLACEHOLDER);
        KryoSerialization kryoSerialization = new KryoSerialization(kryoFactory);
        kryoSerialization.serialize(bout, message);
    }

    public Object decode(final ByteBuf in) throws IOException {
        KryoSerialization kryoSerialization = new KryoSerialization(kryoFactory);
        return kryoSerialization.deserialize(new ByteBufInputStream(in));
    }
}
