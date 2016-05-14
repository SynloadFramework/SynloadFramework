package com.synload.framework.elements;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleResource;

public class RegisterBox extends Response {
    public RegisterBox(List<String> templateCache) {
        this.setTemplateId("rb1");
        if (!templateCache.contains(this.getTemplateId())) {
            try {
				this.setTemplate(this.getTemplate(new String(ModuleResource.get("synloadframework","register.html"),"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        this.setAction("alone");
        this.setPageId("registerBox");
        this.setParent("#content");
        this.setRequest(new Request("get", "register"));
        this.setParentTemplate("full");
        this.setPageTitle(" .::. Register");
    }
}