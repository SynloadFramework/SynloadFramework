package com.synload.talksystem.eventShare;

/**
 * Created by Nathaniel on 7/29/2016.
 */

public class ESSharedEvent {
    public String annotation;
    public String[] trigger;
    public ESSharedEvent(String annotation, String[] trigger){
    this.annotation = annotation;
        this.trigger = trigger;
    }

    public String[] getTrigger() {
        return trigger;
    }

    public void setTrigger(String[] trigger) {
        this.trigger = trigger;
    }

    public String getAnnotation() {

        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
