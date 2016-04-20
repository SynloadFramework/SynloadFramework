package com.synload.framework.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public class HttpRequest {
	public String target = null;
	public Request baseRequest = null;
	public HttpServletRequest request = null;
	public HttpServletResponse response = null;
	public String[] URI = null;
	public HttpRequest(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, String[] URI){
		this.target = target;
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
		this.URI = URI;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Request getBaseRequest() {
		return baseRequest;
	}
	public void setBaseRequest(Request baseRequest) {
		this.baseRequest = baseRequest;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public String[] getURI() {
		return URI;
	}
	public void setURI(String[] uRI) {
		URI = uRI;
	}
}
