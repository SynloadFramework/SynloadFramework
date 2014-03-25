package com.synload.framework.forms;

public class Text extends FormItem {
	public String value = "";
	public Text(){
		this.setType("text");
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
