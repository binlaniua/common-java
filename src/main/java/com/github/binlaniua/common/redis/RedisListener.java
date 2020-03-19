package com.github.binlaniua.common.redis;

public interface RedisListener<T> {

    String topic();

    void onMessage(T t);
}
