package vega.config;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by yanmo.yx on 2016/7/12.
 */
public class ConfigUtil {

    private static final String APPNAME = "appName";
    private static final String SERIALIZATION = "serialization";
    private static final String ZOOKEEPER_SERVERS = "zk.servers";

    private static final String CONFIG_PATH = "/vega.properties";
    private static final Properties properties = new Properties();

    /**
     * 配置的值
     */
    private static String appName;
    private static String serialization;
    private static String zkServers;
    private static InetSocketAddress inetSocketAddress;


    static {
        InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
        try {
            properties.load(is);
            appName = properties.getProperty(APPNAME);
            serialization = properties.getProperty(SERIALIZATION);
            if (StringUtils.isBlank(serialization)) {
                serialization = "hessian";
            }
            zkServers = properties.getProperty(ZOOKEEPER_SERVERS);
            inetSocketAddress = new InetSocketAddress(getLocalNetAddress(), 40099);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLocalNetAddress() {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        netip = ip.getHostAddress();
                    } else if (ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {

        }
        return netip == null ? localip : netip;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getSerialization() {
        return serialization;
    }

    public static String getZkServers() {
        return zkServers;
    }

    public static InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }
}
