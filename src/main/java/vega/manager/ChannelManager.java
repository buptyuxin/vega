package vega.manager;

import vega.component.ClientChannelComponent;
import vega.consumer.ConsumerService;
import vega.message.MessageCenter;
import vega.net.RpcRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ChannelManager {

    private ClientChannelComponent clientChannelComponent;
    private MessageCenter messageCenter;
    private ConsumerService consumerService;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public ChannelManager(ClientChannelComponent clientChannelComponent, MessageCenter messageCenter, ConsumerService consumerService) {
        this.clientChannelComponent = clientChannelComponent;
        this.messageCenter = messageCenter;
        this.consumerService = consumerService;
    }

    /**
     * interfaceName:version -> serverIp:port
     */
    private Map<String, List<String>> interfaceMap = new ConcurrentHashMap<>();

    public void addChannel(String interfaceName, String version, String serverIp, String port) {

        lock.writeLock().lock();
        try {
            String key = interfaceName + ":" + version;
            List<String> servers = interfaceMap.get(key);
            if (servers == null) {
                servers = new ArrayList<>();
            }
            servers.add(serverIp + ":" + port);

            interfaceMap.put(key, servers);
            clientChannelComponent.addChannel(serverIp, Integer.valueOf(port));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void sendReq(RpcRequest rpcRequest) {

    }
}
