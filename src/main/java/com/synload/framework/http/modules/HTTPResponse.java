package com.synload.framework.http.modules;

public class HTTPResponse {
    public String method = "";
    public Class<?> listener = null;
    public String httpMethod;
    public String mimetype = null;
    public HTTPResponse(Class<?> listener, String method, String httpMethod, String mimetype) {
        this.listener = listener;
        this.method = method;
        this.httpMethod = httpMethod;
        this.mimetype = mimetype;
    }
    public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
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