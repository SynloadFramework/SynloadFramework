package com.synload.framework.elements;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Response;

public class Wrapper extends Response{
	public Wrapper(List<String> templateCache){
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