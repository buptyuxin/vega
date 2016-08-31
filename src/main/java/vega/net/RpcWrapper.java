package vega.net;

import java.util.concurrent.locks.Condition;

/**
 * Created by yanmo.yx on 2016/8/30.
 */
public class RpcWrapper {
    private Integer msgId;
    private Condition condition;
    private RpcResponse rpcResponse;

    public Integer getMsgId() {
        return msgId;
    }

    public void setMsgId(Integer msgId) {
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
