package vega.message.info;

/**
 * Created by yanmo.yx on 2016/8/10.
 */
public class ProviderChangeInfo {
    private String interfaceName;
    private String version;
    private Integer type; // 1-删除，2-新增
    private String providerIp;
    private String port;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getProviderIp() {
        return providerIp;
    }

    public void setProviderIp(String providerIp) {
        this.providerIp = providerIp;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isDel() {
        return type == null ? false : type == 1;
    }

    public boolean isAdd() {
        return type == null ? false : type == 2;
    }
}
