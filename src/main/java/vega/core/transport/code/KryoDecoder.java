package vega.core.transport.code;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import vega.core.transport.RpcResponse;
import vega.core.transport.RpcWrapper;
import vega.core.serialization.kryo.KryoPool;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yanmo.yx on 2016/12/13.
 */
public class KryoDecoder extends LengthFieldBasedFrameDecoder {

    private final KryoPool kryoPool;

    /**
     * 需要与channel组件共享下messageMap，用来notify相应的发送线程
     */
    private ConcurrentHashMap<String, RpcWrapper> messageMap;

    public KryoDecoder(KryoPool kryoPool, ConcurrentHashMap<String, RpcWrapper> messageMap) {
        // 包头定义长度，4byte，获得包内容需要adjust 4byte
        super(1024 * 1024, 0, 4, 0, 4);
        this.kryoPool = kryoPool;
        this.messageMap = messageMap;
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        try {
            RpcResponse rpcResponse = (RpcResponse) kryoPool.decode(new ByteBufInputStream(frame));
            RpcWrapper rpcWrapper = messageMap.get(rpcResponse.getMsgId());
            if (rpcWrapper == null) {
                // TODO fix
                return null;
            }
            rpcWrapper.setRpcResponse(rpcResponse);
            rpcWrapper.getCondition().signalAll();
            return rpcResponse;
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
    }

}
