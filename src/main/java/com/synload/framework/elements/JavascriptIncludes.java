package com.synload.framework.elements;

import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Response;
import com.synload.framework.js.Javascript;

public class JavascriptIncludes extends Response{
	public List<Javascript> javascripts = SynloadFramework.javascripts;
	public String js_template = this.getFileData("./elements/js/include_script.js");
	public int jscount = SynloadFramework.javascripts.size();
}