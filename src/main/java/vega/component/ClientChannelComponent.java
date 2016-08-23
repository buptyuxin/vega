package vega.component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vega.net.RpcRequest;
import vega.serialization.kryo.KryoPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ClientChannelComponent implements Component {

    Logger log = LoggerFactory.getLogger("vega");

    /**
     * serverip:port -> bootstrap
     */
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    private KryoPool kryoPool;

    @Override
    public void init() {
        kryoPool = new KryoPool();
        kryoPool.init();

        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new KryoDecoder(kryoPool)).addLast(new MessageToByteEncoder<RpcRequest>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
                        int startIdx = out.writerIndex();
                        msg.getMsgId();
                        kryoPool.encode(out, msg);
                        int endIdx = out.writerIndex();
                        out.setInt(startIdx, endIdx - startIdx - 4);
                    }
                });
            }
        });
    }

    @Override
    public void shutdown() {

    }

    public void addChannel(String serverIp, int port) {
        ChannelFuture f = bootstrap.connect(serverIp, port).awaitUninterruptibly();
        channelMap.put(serverIp + ":" + port, f.channel());
    }

    class KryoDecoder extends LengthFieldBasedFrameDecoder {

        private final KryoPool kryoPool;

        public KryoDecoder(KryoPool kryoPool) {
            super(10485760, 0, 4, 0, 4);
            this.kryoPool = kryoPool;
        }

        @Override
        protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
            ByteBuf frame = (ByteBuf) super.decode(ctx, in);
            if (frame == null) {
                return null;
            }
            try {
                return kryoPool.decode(frame);
            } finally {
                if (null != frame) {
                    frame.release();
                }
            }
        }

    }
}
