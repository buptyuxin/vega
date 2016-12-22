package vega.core.transport.code;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import vega.core.transport.RpcRequest;
import vega.core.serialization.kryo.KryoPool;

/**
 * Created by yanmo.yx on 2016/12/15.
 */
public class KryoCoder extends MessageToByteEncoder<RpcRequest> {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    private KryoPool kryoPool;

    public KryoCoder(KryoPool kryoPool) {
        this.kryoPool = kryoPool;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
        int startIdx = out.writerIndex();
        ByteBufOutputStream bout = new ByteBufOutputStream(out);
        // 占位
        bout.write(LENGTH_PLACEHOLDER);
        kryoPool.encode(bout, msg);
        int endIdx = out.writerIndex();
        out.setInt(startIdx, endIdx - startIdx - 4);
    }
}
