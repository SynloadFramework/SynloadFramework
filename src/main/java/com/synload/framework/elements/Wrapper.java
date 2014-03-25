package com.synload.framework.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Response;
import com.synload.framework.menu.MenuItem;

public class Wrapper extends Response{
	public List<MenuItem> menus = new ArrayList<MenuItem>();
	public Wrapper(List<String> templateCache){
		this.menus = SynloadFramework.menus;
		this.setTemplateId("wp1");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./pages/wrapper.html"));
		}
		this.setAction("alone");
		this.setForceParent(true);
		this.setParent("#content");
		this.setParentTemplate("full");
	}
}