package cn.tkk.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class RedisEventListener {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @TransactionalEventListener()
    public void onEvent(RedisEvent event) throws JsonProcessingException {
        String s = objectMapper.writeValueAsString(event);
        stringRedisTemplate.convertAndSend(
                event.topic(),
                s
        );
    }
}
