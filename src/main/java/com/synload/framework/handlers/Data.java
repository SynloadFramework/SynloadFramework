package com.synload.framework.handlers;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "class"
	)
public class Data {
	public Map<String, Object> p = new HashMap<String, Object>();
	public String trigger = "";
	public Data(HashMap<String, Object> params){
		this.p = params;
	}
	public Map<String, Object> getParams() {
		return p;
	}
	public void setParams(Map<String, Object> p) {
		this.p = p;
	}
	public Object get(String k){
		return p.get(k);
	}
	public Object put(String k, Object o){
		return p.put(k,o);
	}
	public String getTrigger() {
		return trigger;
	}
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
}
