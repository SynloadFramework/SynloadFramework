package com.synload.framework.http.modules;

public class HTTPResponse {
    public String method = "";
    public Class<?> listener = null;
    public String httpMethod;
    public HTTPResponse(Class<?> listener, String method, String httpMethod) {
        this.listener = listener;
        this.method = method;
        this.httpMethod = httpMethod;
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

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
}