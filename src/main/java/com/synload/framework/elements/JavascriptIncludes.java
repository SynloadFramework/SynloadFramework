package com.synload.framework.elements;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Response;
import com.synload.framework.js.Javascript;
import com.synload.framework.modules.ModuleLoader;

public class JavascriptIncludes extends Response {
    public List<Javascript> javascripts = SynloadFramework.javascripts;
    public String js_template = "";
    public int jscount = SynloadFramework.javascripts.size();
    public JavascriptIncludes(){
    	super();
    	try {
			js_template = new String(ModuleLoader.resources.get("synloadframework").get("js/include_script.js"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
}