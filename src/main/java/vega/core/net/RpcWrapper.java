package vega.core.net;

import java.util.concurrent.locks.Condition;

/**
 * Created by yanmo.yx on 2016/8/30.
 */
public class RpcWrapper {
    private String msgId;
    private Condition condition;
    private RpcResponse rpcResponse;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public RpcResponse getRpcResponse() {
        return rpcResponse;
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
    }
}
