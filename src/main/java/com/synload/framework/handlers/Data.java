package com.synload.framework.handlers;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class Data {
    public Map<String, Object> p = new HashMap<String, Object>();
    public String trigger, reference = "";

    public Data(HashMap<String, Object> params, String trigger) {
        this.p = params;
        this.trigger = trigger;
    }

    public Map<String, Object> getParams() {
        return p;
    }

    public void setParams(Map<String, Object> p) {
        this.p = p;
    }

    public Object get(String k) {
        return p.get(k);
    }

    public Object put(String k, Object o) {
        return p.put(k, o);
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
}
