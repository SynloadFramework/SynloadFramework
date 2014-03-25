package com.synload.framework.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "class"
	)
public class Request{
	public String request, page = "";
	public List<String> templateCache = new ArrayList<String>();
	public Map<String,String> data = new HashMap<String,String>();
	public Request(String request, String page){
		this.setRequest(request);
		this.setPage(page);
	}
	public Request(String request, String page, Map<String,String> data){
		this.setRequest(request);
		this.setPage(page);
		this.setData(data);
	}
	public List<String> getTemplateCache() {
		return templateCache;
	}
	public void setTemplateCache(List<String> templateCache) {
		this.templateCache = templateCache;
	}
	public Request() {}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
}