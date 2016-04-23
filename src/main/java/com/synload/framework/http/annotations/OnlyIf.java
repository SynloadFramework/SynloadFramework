package com.synload.framework.http.annotations;

public @interface OnlyIf {
	public String property();
	public boolean is();
}
