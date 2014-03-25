package com.synload.framework.menu;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.handlers.Request;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "class"
	)
public class MenuItem {
	public List<MenuItem> menus = new ArrayList<MenuItem>();
	public String name, uname, flag = "";
	public Request request = null;
	public Integer priority = 0;
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public List<MenuItem> getMenus() {
		return menus;
	}
	public void setMenus(List<MenuItem> menus) {
		this.menus = menus;
	}
	public void addMenus(MenuItem menu) {
		this.menus.add(menu);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
}
