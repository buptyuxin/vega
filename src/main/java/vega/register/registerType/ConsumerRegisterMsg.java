package vega.register.registerType;

/**
 * Created by yanmo.yx on 2016/8/9.
 */
public class ConsumerRegisterMsg extends RegisterMsg<ConsumerRegisterMsg.ConsumerMsgContent> {

    private ConsumerRegisterMsg(ConsumerRegisterMsg.ConsumerMsgContent content) {
        super(content);
    }

    public class ConsumerMsgContent {
        private String method;
        private String version;

        public ConsumerMsgContent(String method, String version) {
            this.method = method;
            this.version = version;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
