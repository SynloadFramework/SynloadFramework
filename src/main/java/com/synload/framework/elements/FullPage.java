package com.synload.framework.elements;

import java.util.List;

import com.synload.framework.handlers.Response;

public class FullPage extends Response{
	public FullPage(List<String> templateCache){
		this.setTemplateId("fp1");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./pages/full.html"));
		}
		this.setAction("alone");
		this.setParent("#inner[element='body']");
		this.setParentTemplate("full");
	}
}