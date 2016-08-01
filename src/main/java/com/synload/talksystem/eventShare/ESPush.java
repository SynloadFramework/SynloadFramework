package com.synload.talksystem.eventShare;

import com.synload.eventsystem.EventClass;

import java.io.Serializable;

/**
 * Created by Nathaniel on 7/22/2016.
 */
public class ESPush implements Serializable {
    private EventClass event;
    public ESPush(EventClass e){
        event = e;
    }
    public EventClass getEvent() {
        return event;
    }
    public void setEvent(EventClass event) {
        this.event = event;
    }
}