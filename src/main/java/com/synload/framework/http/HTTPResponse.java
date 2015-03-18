package com.synload.framework.http;

public class HTTPResponse {
    public String method = "";
    public Class<?> listener = null;

    public HTTPResponse(Class<?> listener, String method) {
        this.listener = listener;
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?> getListener() {
        return listener;
    }

    public void setListener(Class<?> listener) {
        this.listener = listener;
    }
}