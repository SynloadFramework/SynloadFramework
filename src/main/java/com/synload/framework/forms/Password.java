package com.synload.framework.forms;

public class Password extends FormItem{
	public String value = "";
	public Password(){
		this.setType("password");
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
