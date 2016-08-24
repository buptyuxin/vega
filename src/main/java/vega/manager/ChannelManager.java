package vega.manager;

import vega.component.ClientChannelComponent;
import vega.consumer.ConsumerService;
import vega.message.MessageCenter;
import vega.net.RpcRequest;

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
public class ChannelManager {

    private ClientChannelComponent clientChannelComponent;
    private MessageCenter messageCenter;
    private ConsumerService consumerService;

    public ChannelManager(ClientChannelComponent clientChannelComponent, MessageCenter messageCenter, ConsumerService consumerService) {
        this.clientChannelComponent = clientChannelComponent;
        this.messageCenter = messageCenter;
        this.consumerService = consumerService;
    }

    public void init() {
        clientChannelComponent.init();
    }

    /**
     * interfaceName:version -> serverIp:port
     */
    private Map<String, List<String>> interfaceMap = new ConcurrentHashMap<>();

    public void addChannel(String interfaceName, String version, String serverIp, String port) {
        clientChannelComponent.addChannel(interfaceName, version, serverIp, Integer.valueOf(port));
    }

    public void delChannel(String interfaceName, String version, String serverIp, String port) {
        clientChannelComponent.delChannel(interfaceName, version, serverIp, Integer.valueOf(port));
    }

    public void sendReq(RpcRequest rpcRequest) {


    }
}
