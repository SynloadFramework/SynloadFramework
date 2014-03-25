package com.synload.framework.forms;

public class Description extends FormItem {
	public String description = "";
	public Description(){
		this.setType("description");
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
