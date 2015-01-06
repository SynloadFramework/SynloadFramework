package com.synload.framework.sql;

public enum COLUMNSET {
	ALL("(?i).*");
	String e;
	COLUMNSET(String e){
		this.e = e;
	}
	public String getVal(){
		return e;
	}
}