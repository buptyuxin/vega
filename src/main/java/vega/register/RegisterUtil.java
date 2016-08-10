package vega.register;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public class RegisterUtil {

    private static final String ROOT = "/vega";
    private static final String PATH_DELIMITER = "/";

    public static String getMethodPath(String method, String version) {
        return ROOT + PATH_DELIMITER + method + PATH_DELIMITER + version;
    }
}
