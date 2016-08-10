package vega.net;

import java.io.Serializable;

/**
 * Created by yanmo.yx on 2016/7/12.
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -4677033288339109317L;

    private String group;
    private String version;
    private String appName;
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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
