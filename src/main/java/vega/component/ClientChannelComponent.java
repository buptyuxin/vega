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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ClientChannelComponent implements Component {

    Logger log = LoggerFactory.getLogger("vega");

    /**
     * interface:version -> ChannelWrapper
     */
    private Map<String, List<ChannelWrapper>> channelMap = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    private KryoPool kryoPool;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void init() {
        kryoPool = new KryoPool(10, 1, 500, 500);
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

    public void addChannel(String interfaceName, String version, String serverIp, int port) {

        ChannelWrapper channelWrapper = new ChannelWrapper();

        ChannelFuture f = bootstrap.connect(serverIp, port).awaitUninterruptibly();

        channelWrapper.setChannel(f.channel());
        channelWrapper.setServerIp(serverIp);
        channelWrapper.setPort(port + "");
        channelWrapper.setInterfaceName(interfaceName);
        channelWrapper.setVersion(version);

        lock.writeLock().lock();
        try {
            String key = interfaceName + ":" + version;
            List<ChannelWrapper> channels = channelMap.get(key);
            if (channels == null) {
                channels = new ArrayList<>();
                channelMap.put(key, channels);
            }
            channels.add(channelWrapper);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delChannel(String interfaceName, String version, String serverIp, int port) {
        String key = interfaceName + ":" + version;
        List<ChannelWrapper> channels = channelMap.get(key);
        if (channels == null) {
            return;
        }

        lock.writeLock().lock();
        try {
            for (Iterator<ChannelWrapper> iter = channels.iterator(); iter.hasNext(); ) {
                ChannelWrapper channel = iter.next();
                if (channel.getServerIp().equals(serverIp) && channel.getPort().equals(port + "")) {
                    iter.remove();
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String sendReq(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();
        String version = rpcRequest.getVersion();
        String key = interfaceName + ":" + version;

        lock.readLock().lock();
        try {
            List<ChannelWrapper> channels = channelMap.get(key);
            if (channels == null) {
                // TODO
                return null;
            }
            ChannelWrapper channel = selectChannel(channels);
            ChannelFuture f = channel.getChannel().writeAndFlush(rpcRequest);
            f.awaitUninterruptibly();
        } finally {
            lock.readLock().unlock();
        }

    }

    private ChannelWrapper selectChannel(List<ChannelWrapper> channels) {

    }

    private class KryoDecoder extends LengthFieldBasedFrameDecoder {

        private final KryoPool kryoPool;

        public KryoDecoder(KryoPool kryoPool) {
            super(1024 * 1024, 0, 4, 0, 4);
            this.kryoPool = kryoPool;
        }

        @Override
        protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
            ByteBuf frame = (ByteBuf) super.decode(ctx, in);
            if (frame == null) {
                return null;
            }
            try {
                RpcRequest rpcRequest = (RpcRequest) kryoPool.decode(frame);
            } finally {
                if (null != frame) {
                    frame.release();
                }
            }
        }

    }

    public static class ChannelWrapper {
        private Channel channel;
        private String interfaceName;
        private String version;
        private String serverIp;
        private String port;

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public String getInterfaceName() {
            return interfaceName;
        }

        public void setInterfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getServerIp() {
            return serverIp;
        }

        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }
}
