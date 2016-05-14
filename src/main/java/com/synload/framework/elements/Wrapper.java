package com.synload.framework.elements;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleResource;

public class Wrapper extends Response {
    public Wrapper(List<String> templateCache) {
        this.setTemplateId("wp1");
        if (!templateCache.contains(this.getTemplateId())) {
            try {
				this.setTemplate(this.getTemplate(new String(ModuleResource.get("synloadframework","wrapper.html"),"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        String moduleList = "";
        for(String modName: ModuleLoader.modules){
        	moduleList += ((moduleList.equals(""))?"":", ")+modName;
        }
        this.getData().put("modules", moduleList);
        this.getData().put("version", SynloadFramework.version);
        this.getData().put("title", SynloadFramework.getProp().getProperty("name"));
        this.setAction("alone");
        this.setForceParent(true);
        this.setParent("#content");
        this.setParentTemplate("full");
    }
}