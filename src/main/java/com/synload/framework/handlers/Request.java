package com.synload.framework.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class Request implements Serializable {
    public String method, action, reference = "";
    public List<String> templateCache = new ArrayList<String>();
    public Map<String, String> data = new HashMap<String, String>();

    public Request(String method, String action) {
        this.method = method;
        this.action = action;
    }

    public Request(String method, String action, Map<String, String> data) {
        this.method = method;
        this.action = action;
        this.setData(data);
    }

    public List<String> getTemplateCache() {
        return templateCache;
    }

    public void setTemplateCache(List<String> templateCache) {
        this.templateCache = templateCache;
    }

    public Request() {
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}