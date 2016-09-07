package com.movile.pgle;

import org.springframework.context.ApplicationEvent;

public class Event extends ApplicationEvent {

    public enum EventType {
        PEER_GROUP_REGISTER,
        IS_LEADER,
        IS_NOT_LEADER
    }

    public EventType type;

    public Event(EventType eventType, Object source) {
        super(source);
        this.type = eventType;
    }
}
