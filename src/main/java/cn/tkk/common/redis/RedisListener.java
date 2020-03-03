package cn.tkk.common.redis;

public interface RedisListener<T> {

    String topic();

    void onMessage(T t);
}
