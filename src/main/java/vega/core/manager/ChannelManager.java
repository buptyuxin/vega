package vega.core.manager;

import vega.core.component.ClientChannelComponent;
import vega.core.consumer.ConsumerService;
import vega.core.message.MessageCenter;
import vega.core.net.RpcRequest;
import vega.core.net.RpcResponse;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    private Map<String, Ring<String>> interfaceMap = new ConcurrentHashMap<>();

    public void addChannel(String interfaceName, String version, String serverIp, String port) {
        Ring<String> rings = interfaceMap.get(interfaceName + ":" + version);
        if (rings == null) {
            rings = new Ring<>();
            interfaceMap.put(interfaceName + ":" + version, rings);
        }
        clientChannelComponent.addChannel(serverIp, Integer.valueOf(port));

        rings.insert(serverIp + ":" + port);
    }

    public void delChannel(String interfaceName, String version, String serverIp, String port) {

        Ring<String> rings = interfaceMap.get(interfaceName + ":" + version);
        if (rings == null) {
            return;
        }

        for (Iterator<String> iter = rings.iterator(); iter.hasNext(); ) {
            String server = iter.next();
            if (server.equals(serverIp + ":" + port)) {
                iter.remove();
                break;
            }
        }

        clientChannelComponent.delChannel(serverIp, Integer.valueOf(port));
    }

    public RpcResponse sendReq(RpcRequest rpcRequest) {

        String interfaceName = rpcRequest.getInterfaceName();
        String version = rpcRequest.getVersion();
        String key = interfaceName + ":" + version;

        Ring<String> ring = interfaceMap.get(key);
        String server = ring.ring();

        String[] strs = server.split(":");
        String serverIp = strs[0];
        String port = strs[1];

        return clientChannelComponent.sendReq(serverIp, port, rpcRequest);
    }

    private class Ring<T> {

        private Node current;
        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        private class Node {
            private T data;
            private Node next;
            private Node prev;
        }

        public T ring() {
            if (null == current) {
                return null;
            }
            lock.readLock().lock();
            try {
                current = current.next;
                return current.data;
            } finally {
                lock.readLock().unlock();
            }
        }

        public void insert(T data) {
            Node node = new Node();
            node.data = data;

            lock.writeLock().lock();
            try {
                if (null == current) {
                    current = node;
                    current.prev = current;
                    current.next = current;
                } else {
                    current.prev.next = node;
                    node.prev = current.prev;
                    node.next = current;
                    current.prev = node;
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void clean() {
            lock.writeLock().lock();
            try {
                current = null;
            } finally {
                lock.writeLock().unlock();
            }
        }

        public Iterator<T> iterator() {

            final Node node = current;

            return new Iterator<T>() {

                private Node first = null;
                private Node idx = node;

                @Override
                public boolean hasNext() {
                    return first != idx;
                }

                @Override
                public T next() {
                    if (first == null) {
                        first = idx;
                    }
                    T data = idx.data;
                    idx = idx.next;
                    return data;
                }

                @Override
                public void remove() {
                    lock.writeLock().lock();
                    try {
                        if (idx.next == idx) {
                            clean();
                        } else {
                            idx.next.prev = idx.prev;
                            idx.prev.next = idx.next;
                        }
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
            };
        }
    }
}
