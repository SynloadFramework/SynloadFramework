package com.synload.framework.ws;

import java.util.ArrayList;
import java.util.List;

public class WSResponse {
	public Class<?> listener = null;
	public String method = "";
	public List<String> requiredFlags = new ArrayList<String>();
	public WSResponse(Class<?> listener, String method, List<String> requiredFlags ){
		this.listener = listener;
		this.method = method;
		this.requiredFlags = requiredFlags;
	}
	public Class<?> getListener() {
		return listener;
	}
	public void setListener(Class<?> listener) {
		this.listener = listener;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public List<String> getRequiredFlags() {
		return requiredFlags;
	}
	public void setRequiredFlags(List<String> requiredFlags) {
		this.requiredFlags = requiredFlags;
	}
}
