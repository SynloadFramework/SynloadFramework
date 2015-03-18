package com.synload.eventsystem;

import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Module;

public enum Handler {
    EVENT(Event.class), MODULE(Module.class);
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
