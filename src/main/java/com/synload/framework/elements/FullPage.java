package com.synload.framework.elements;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;

public class FullPage extends Response {
    public FullPage(List<String> templateCache) {
        this.setTemplateId("fp1");
        if (!templateCache.contains(this.getTemplateId())) {
            try {
				this.setTemplate(this.getTemplate(new String(ModuleLoader.resources.get("synloadframework").get("full.html"),"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        this.setAction("alone");
        this.setParent("body");
        //this.setParentTemplate("full");
    }
}