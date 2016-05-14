package com.synload.framework.elements;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleResource;

public class LoginBox extends Response {
    public LoginBox(List<String> templateCache) {
        this.setTemplateId("lb1");
        if (!templateCache.contains(this.getTemplateId())) {
            try {
				this.setTemplate(this.getTemplate(new String(ModuleResource.get("synloadframework","login.html"),"UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        this.setAction("alone");
        this.setPageId("loginBox");
        this.setParent("#content");
        this.setParentTemplate("full");
        Map<String, String> tmp = new HashMap<String, String>();
        tmp.put("username", "username");
        tmp.put("password", "password");
        tmp.put("input", "#username");
        this.setData(tmp);
        this.setRequest(new Request("get", "login"));
        this.addJavascript(this.getFileData("./elements/js/focus_input.js"));
        this.setPageTitle(" .::. Login");
    }
}