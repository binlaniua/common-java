package cn.tkk.common.event;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

public class Event extends ApplicationEvent {

    public Event() {
        super(StringUtils.EMPTY);
    }

}
