package com.synload.framework.handlers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class CallEvent {
    public String event = "";

    public CallEvent setEvent(String event) {
        this.event = event;
        return this;
    }
}
