package com.synload.framework.modules;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class ModuleData{
	public String name;
	@JsonIgnore
	private String file;
	public String version;
	@JsonIgnore
	private List<String> classes = new ArrayList<String>();
	@JsonIgnore
	private List<String> resources = new ArrayList<String>();
	public ModuleData(){
		
	}
	public ModuleData(String name, List<String> classes, List<String> resources){
		this.name=name;
		this.classes=classes;
		this.resources=resources;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getClasses() {
		return classes;
	}
	public void setClasses(List<String> classes) {
		this.classes = classes;
	}
	public List<String> getResources() {
		return resources;
	}
	public void setResources(List<String> resources) {
		this.resources = resources;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
}