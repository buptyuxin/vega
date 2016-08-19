package vega.component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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
    private Map<String, Bootstrap> channelMap = new ConcurrentHashMap<>();

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }

    public void addChannel(String serverIp, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bs = new Bootstrap();
        bs.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(serverIp, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                String msg = ByteBufUtil.hexDump(byteBuf.readBytes(byteBuf.readableBytes()));
                            }
                        });
                    }
                });
        try {
            ChannelFuture f = bs.connect().sync();
        } catch (InterruptedException e) {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
