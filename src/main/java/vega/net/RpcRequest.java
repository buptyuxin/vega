package vega.net;

import java.io.Serializable;

/**
 * Created by yanmo.yx on 2016/7/12.
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -4677033288339109317L;

    private Integer msgId;
    private String interfaceName;
    private String version;
    private String methodMD5; // methodName + args MD5
    private Serializable[] args;
    private long timeout;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Serializable[] getArgs() {
        return args;
    }

    public void setArgs(Serializable[] args) {
        this.args = args;
    }

    public String getMethodMD5() {
        return methodMD5;
    }

    public void setMethodMD5(String methodMD5) {
        this.methodMD5 = methodMD5;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Integer getMsgId() {
        if (msgId == null) {
            msgId = RpcProtocolUtil.getMsgId();
        }
        return msgId;
    }
}
