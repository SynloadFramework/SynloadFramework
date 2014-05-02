package com.synload.framework.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.SynloadFramework;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
public class Response {
	public String template, parent, pri, action, parentTemplate, callEvent, pageId, pageTitle, templateId = "";
	public String transitionOut = "drop";
	public String transitionIn = "slide";
	public boolean forceParent = true;
	public Request request = null;
	public List<String> javascript = new ArrayList<String>();
	public Map<String,String> redirect, data = new HashMap<String,String>();
	public List<DelayedRequest> delayedRequests = new ArrayList<DelayedRequest>();
	public int sleep = 0;
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = SynloadFramework.prop.getProperty("name")+pageTitle;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	public String getPageRI() {
		return pri;
	}
	public void setPageRI(String pri) {
		this.pri = pri;
	}
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public String getTransitionOut() {
		return transitionOut;
	}
	public void setTransitionOut(String transitionOut) {
		this.transitionOut = transitionOut;
	}
	public String getTransitionIn() {
		return transitionIn;
	}
	public void setTransitionIn(String transitionIn) {
		this.transitionIn = transitionIn;
	}
	public void setJavascript(List<String> javascript) {
		this.javascript = javascript;
	}
	public boolean isForceParent() {
		return forceParent;
	}
	public void setForceParent(boolean forceParent) {
		this.forceParent = forceParent;
	}
	public List<String> getJavascript() {
		return javascript;
	}
	public void addJavascript(String javascript) {
		this.javascript.add(javascript);
	}
	public String getCallEvent() {
		return callEvent;
	}
	public void setCallEvent(String callEvent) {
		this.callEvent = callEvent;
	}
	public List<DelayedRequest> getDelayedRequests() {
		return delayedRequests;
	}
	public void setDelayedRequests(List<DelayedRequest> delayedRequests) {
		this.delayedRequests = delayedRequests;
	}
	public Map<String, String> getRedirect() {
		return redirect;
	}
	public void setRedirect(Map<String, String> redirect) {
		this.redirect = redirect;
	}
	public int getSleep() {
		return sleep;
	}
	public void setSleep(int sleep) {
		this.sleep = sleep;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public String getParentTemplate() {
		return parentTemplate;
	}
	public void setParentTemplate(String parentTemplate) {
		this.parentTemplate = parentTemplate;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	public String getTemplate(String tmpl){
		return this.getFileData(tmpl);
	}
	public String getFileData(String tmpl){
		String dataOut = "";
		boolean isCached = false;
		HashMap<String, Object> htmlf = null;
		if(SynloadFramework.htmlFiles.containsKey(tmpl)){
			htmlf = SynloadFramework.htmlFiles.get(tmpl);
			isCached = htmlf.get("modified").equals(( new File(tmpl)).lastModified());
		}
		if(!isCached){
			try {
				File htmlFile = ( new File(tmpl));
				InputStream is = new FileInputStream(htmlFile);
				HashMap<String, Object> tmpf = new HashMap<String, Object>(); 
				tmpf.put("modified", ( new File(tmpl)).lastModified());
				@SuppressWarnings("unused")
				int bytesRead;
				byte[] buffer = new byte[8 * 1024];
				while ((bytesRead = is.read(buffer)) != -1) {
					String dataM = new String(buffer);
					dataOut += dataM;
				}
				tmpf.put("data", dataOut.trim());
				SynloadFramework.htmlFiles.put(tmpl, tmpf);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return dataOut.trim();
		}else{
			return (String) htmlf.get("data");
		}
	}
}