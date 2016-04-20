package com.synload.framework.http.annotations;

public @interface OnlyIf {
	public String property() default "";
	public boolean is() default false;
}
