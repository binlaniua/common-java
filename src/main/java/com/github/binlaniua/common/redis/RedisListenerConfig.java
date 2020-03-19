package com.github.binlaniua.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

@Slf4j
@Configuration
public class RedisListenerConfig implements ApplicationContextAware {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisMessageListenerContainer redisMessageListenerContainer;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansOfType(RedisListener.class)
                          .forEach((k, v) -> {
                              this.addListener(v);
                          });
    }

    private void addListener(RedisListener v) {
        Class type = (Class) ((ParameterizedType) v.getClass()
                                                   .getGenericInterfaces()[0]).getActualTypeArguments()[0];
        redisMessageListenerContainer.addMessageListener((m, p) -> {
            Object o = null;
            try {
                o = objectMapper.readValue(m.getBody(), type);
                v.onMessage(o);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }, new ChannelTopic(v.topic()));
    }
}
