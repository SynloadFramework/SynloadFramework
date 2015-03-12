package com.synload.framework.forms;

import java.util.ArrayList;
import java.util.List;

import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;

public class Form extends Response{
	public List<FormItem> form = new ArrayList<FormItem>();
	public String header, identifier = "";
	public Form(){
		this.setTemplate(this.getTemplate("./elements/form.html"));
		this.setAction("alone");
	}
	public Request request = null;
	@Override
	public Request getRequest() {
		return request;
	}
	@Override
	public void setRequest(Request request) {
		this.request = request;
	}
	public List<FormItem> getForm() {
		return form;
	}
	public void setForm(List<FormItem> form) {
		this.form = form;
	}
	public void addFormItem(FormItem form) {
		this.form.add(form);
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
