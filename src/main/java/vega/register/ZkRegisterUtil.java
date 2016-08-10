package vega.register;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public class ZkRegisterUtil {

    private static final String ROOT = "/vega";
    private static final String PATH_DELIMITER = "/";

    private static final String CONTENT_DELIMITER = ":";

    public static String getProviderPath(String interfaceName, String version) {
        return ROOT + PATH_DELIMITER + interfaceName + PATH_DELIMITER + version;
    }

    public static String getProviderInterface(String zkPath) {
        if (StringUtils.isNotBlank(zkPath)) {
            String[] strs = zkPath.split(PATH_DELIMITER);
            if (strs.length == 3) {
                return strs[1];
            }
        }
        return null;
    }

    public static String getProviderVersion(String zkPath) {
        if (StringUtils.isNotBlank(zkPath)) {
            String[] strs = zkPath.split(PATH_DELIMITER);
            if (strs.length == 3) {
                return strs[2];
            }
        }
        return null;
    }

    public static String getProviderIp(String zkContent) {
        if (StringUtils.isNotBlank(zkContent)) {
            String[] strs = zkContent.split(CONTENT_DELIMITER);
            if (strs.length == 2) {
                return strs[0];
            }
        }
        return null;
    }

    public static String getProviderPort(String zkContent) {
        if (StringUtils.isNotBlank(zkContent)) {
            String[] strs = zkContent.split(CONTENT_DELIMITER);
            if (strs.length == 2) {
                return strs[1];
            }
        }
        return null;
    }
}
