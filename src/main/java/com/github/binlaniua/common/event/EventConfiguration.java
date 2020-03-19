package com.github.binlaniua.common.event;

import cn.tkk.common.event.publish.InternalEventPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    @ConditionalOnProperty(value = "app.event.publisher", matchIfMissing = true)
    EventPublisher eventPublisher() {
        return new InternalEventPublisher();
    }
}
