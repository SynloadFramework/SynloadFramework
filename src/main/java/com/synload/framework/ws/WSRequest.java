package com.synload.framework.ws;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class WSRequest {
    /**
	 * 
	 */
    public String pri, request = "";

    public WSRequest(String page, String request) {
        this.request = request;
        this.pri = page;
    }

    public String getPri() {
        return pri;
    }

    public void setPri(String page) {
        this.pri = page;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}