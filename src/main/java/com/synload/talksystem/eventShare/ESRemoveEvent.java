package com.synload.talksystem.eventShare;

/**
 * Created by Nathaniel on 8/2/2016.
 */
public class ESRemoveEvent {
    public String annotation;
    public String[] trigger;
    public ESRemoveEvent(String annotation, String[] trigger){
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
