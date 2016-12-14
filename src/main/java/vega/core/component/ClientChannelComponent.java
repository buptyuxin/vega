package vega.core.component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import vega.core.net.RpcRequest;
import vega.core.net.RpcResponse;
import vega.core.net.RpcWrapper;
import vega.core.net.code.KryoDecoder;
import vega.core.serialization.kryo.KryoPool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ClientChannelComponent implements Component {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    /**
     * serverIp:port -> ChannelWrapper
     */
    private ConcurrentMap<String, ChannelWrapper> channelMap = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    private KryoPool kryoPool;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private ConcurrentHashMap<String, RpcWrapper> messageMap = new ConcurrentHashMap<>();

    @Override
    public void init() {
        kryoPool = new KryoPool(10, 1, 500, 500);
        kryoPool.init();

        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new KryoDecoder(kryoPool, messageMap)).addLast(new MessageToByteEncoder<RpcRequest>() {
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
                });
            }
        });
    }

    @Override
    public void shutdown() {

    }

    public void addChannel(String serverIp, int port) {

        String key = serverIp + ":" + port;

        lock.readLock().lock();
        try {
            ChannelWrapper channelWrapper = channelMap.get(key);
            if (channelWrapper != null) {
                channelWrapper.inc();
                return;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            ChannelWrapper channelWrapper = new ChannelWrapper();
            ChannelFuture f = bootstrap.connect(serverIp, port).awaitUninterruptibly();

            channelWrapper.setChannel(f.channel());
            channelWrapper.setServerIp(serverIp);
            channelWrapper.setPort(port + "");
            channelWrapper.inc();
            channelMap.put(key, channelWrapper);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delChannel(String serverIp, int port) {
        String key = serverIp + ":" + port;

        lock.readLock().lock();
        try {
            ChannelWrapper channelWrapper = channelMap.get(key);
            if (channelWrapper == null) {
                return;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            ChannelWrapper channelWrapper = channelMap.get(key);
            if (0 == channelWrapper.dec()) {
                channelWrapper.getChannel().close();
                channelMap.remove(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public RpcResponse sendReq(String serverIp, String port, RpcRequest rpcRequest) {

        String key = serverIp + ":" + port;
        ChannelWrapper channel = channelMap.get(key);
        ChannelFuture f = channel.getChannel().writeAndFlush(rpcRequest);
        f.awaitUninterruptibly();

        ReentrantLock lock = new ReentrantLock();

        /**
         * 这里使用condition实现，也可以用CountDownLatch来做
         */
        Condition condition = lock.newCondition();

        RpcWrapper rpcWrapper = new RpcWrapper();
        rpcWrapper.setCondition(condition);
        rpcWrapper.setMsgId(rpcRequest.getMsgId());
        messageMap.put(rpcWrapper.getMsgId(), rpcWrapper);

        lock.lock();
        try {
            condition.await(rpcRequest.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        RpcResponse rpcResponse = rpcWrapper.getRpcResponse();
        if (rpcResponse == null) {
            // TODO
            messageMap.remove(rpcWrapper.getMsgId());
            throw new RuntimeException("timeout");
        }

        return rpcResponse;
    }

    public static class ChannelWrapper {
        private Channel channel;
        private String serverIp;
        private String port;
        private AtomicInteger count = new AtomicInteger(0);

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
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

        public int inc() {
            return count.incrementAndGet();
        }

        public int dec() {
            return count.decrementAndGet();
        }
    }
}
