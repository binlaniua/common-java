package com.github.binlaniua.common.event.publish;

import cn.tkk.common.event.Event;
import cn.tkk.common.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class InternalEventPublisher implements EventPublisher, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void publish(final Event event) {
        this.applicationContext.publishEvent(event);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
