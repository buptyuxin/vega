package vega.core.message;

import vega.core.message.topic.Topic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * reflect may hava a low perfermance
 * Created by yanmo.yx on 2016/8/24.
 */
@Deprecated
public abstract class AbstractMessageHandler<U, T extends Topic<U>> implements MessageHandler<T> {

    /**
     * 消息处理子类 -> 对应的topic类
     */
    private Map<Class, Class> handler2TopicMap = new HashMap<>();

    @Override
    public void handle(T topic) {
        Class<?> clazz = getClass(); //获取实际运行的类的 Class
        Class topicClass = handler2TopicMap.get(clazz);
        if (topicClass != null && topicClass == topic.getClass()) {
            processMsg(topic);
            return;
        }
        Type type = clazz.getGenericSuperclass(); //获取实际运行的类的直接超类的泛型类型
        if (type instanceof ParameterizedType) { //如果该泛型类型是参数化类型
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();//获取泛型类型的实际类型参数集
            Class<T> clz = (Class<T>) parameterizedType[0]; //取出第一个(下标为0)参数的值
            if (clz.isAssignableFrom(topic.getClass())) {
                handler2TopicMap.put(clazz, topic.getClass());
                processMsg(topic);
            }
        }
    }

    public abstract void processMsg(Topic topic);
}
