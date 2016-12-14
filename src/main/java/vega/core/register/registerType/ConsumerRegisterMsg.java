package vega.core.register.registerType;

import org.apache.commons.lang3.StringUtils;

/**
 * 消费服务注册的消息，内容格式为 interfaceName:version
 *
 * Created by yanmo.yx on 2016/8/9.
 */
public class ConsumerRegisterMsg extends RegisterMsg<String> {

    private ConsumerRegisterMsg(String content) {
        super(content);
    }

    public String getInterface() {
        if (StringUtils.isNotBlank(getContent())) {
            String[] strs = getContent().split(":");
            if (strs.length == 2) {
                return strs[0];
            }
        }
        return null;
    }

    public String getVersion() {
        if (StringUtils.isNotBlank(getContent())) {
            String[] strs = getContent().split(":");
            if (strs.length == 2) {
                return strs[1];
            }
        }
        return null;
    }
}
