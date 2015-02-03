package com.synload.framework.ws;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "class"
)
public class Crypt {
	public String data;
	public String iv;
	public Crypt(String data, String iv){
		this.data = data;
		this.iv = iv;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getIv() {
		return iv;
	}
	public void setIv(String iv) {
		this.iv = iv;
	}
}
