package com.synload.framework.ws.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WSEvent {
    public String name();

    public String description();

    public boolean enabled();
    
    public String method();
    
	public String action();
}