package com.synload.framework.elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;

public class LoginBox extends Response{
	public LoginBox(List<String> templateCache){
		this.setTemplateId("lb1");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/login/box.html"));
		}
		this.setAction("alone");
		this.setPageId("loginBox");
		this.setParent("#content");
		this.setParentTemplate("full");
    	Map<String,String> tmp = new HashMap<String,String>();
    	tmp.put("username", "username");
    	tmp.put("password", "password");
    	tmp.put("input", "#username");
		this.setData(tmp);
		this.setRequest(new Request("get","login"));
		this.addJavascript(this.getFileData("./elements/js/focus_input.js"));
		this.setPageTitle("AnimeCap .::. Login");
	}
}