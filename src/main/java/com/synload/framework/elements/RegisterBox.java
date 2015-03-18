package com.synload.framework.elements;

import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;

public class RegisterBox extends Response {
    public RegisterBox(List<String> templateCache) {
        this.setTemplateId("rb1");
        if (!templateCache.contains(this.getTemplateId())) {
            this.setTemplate(this.getTemplate("./elements/register/box.html"));
        }
        this.setAction("alone");
        this.setPageId("registerBox");
        this.setParent("#content");
        this.setRequest(new Request("get", "register"));
        this.setParentTemplate("full");
        this.setPageTitle(" .::. Register");
    }
}