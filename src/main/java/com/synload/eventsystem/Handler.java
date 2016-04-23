package com.synload.eventsystem;

import com.synload.eventsystem.events.annotations.Event;
import com.synload.framework.modules.annotations.Module;
import com.synload.framework.ws.annotations.WSEvent;

public enum Handler {
	EVENT(Event.class), WSEVENT(WSEvent.class), MODULE(Module.class);
    @SuppressWarnings("rawtypes")
    private Class annotationClass;

    @SuppressWarnings("rawtypes")
    private Handler(Class clazz) {
        this.annotationClass = clazz;
    }

    @SuppressWarnings("rawtypes")
    public Class getAnnotationClass() {
        return annotationClass;
    }
}
