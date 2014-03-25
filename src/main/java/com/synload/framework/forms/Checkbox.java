package com.synload.framework.forms;

public class Checkbox extends FormItem {
	public boolean value = false;
	public String text = "";
	public Checkbox(){
		this.setType("checkbox");
	}
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
