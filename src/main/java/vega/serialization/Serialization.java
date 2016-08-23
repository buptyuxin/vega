package vega.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yanmo.yx on 2016/8/23.
 */
public interface Serialization {
    void serialize(OutputStream out, Object message) throws IOException;
    Object deserialize(InputStream in) throws IOException;
}
