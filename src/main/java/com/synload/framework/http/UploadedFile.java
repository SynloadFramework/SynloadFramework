package com.synload.framework.http;

public class UploadedFile {
	public String name, path, tempName = "";
	public long size = (long)0;
	public UploadedFile(String name, String path, String tempName, long size){
		this.size = size;
		this.name = name;
		this.path = path;
		this.tempName = tempName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTempName() {
		return tempName;
	}
	public void setTempName(String tempName) {
		this.tempName = tempName;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
}
