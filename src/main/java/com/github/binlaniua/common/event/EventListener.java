package com.github.binlaniua.common.event;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

/**
 *
 */
public abstract class EventListener<T extends Event> implements ApplicationListener<T> {

    @Async
    @Override
    public void onApplicationEvent(final T event) {
        this.onEvent(event);
    }

    /**
     * @param event
     */
    public abstract void onEvent(T event);
}
