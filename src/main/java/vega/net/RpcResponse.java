package vega.net;

import java.io.Serializable;

/**
 * Created by yanmo.yx on 2016/8/30.
 */
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -3931723846377009976L;

    private Integer msgId;
    private Throwable e;
    private Object res;

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }
}
