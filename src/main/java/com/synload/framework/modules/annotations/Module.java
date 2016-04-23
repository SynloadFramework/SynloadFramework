package com.synload.framework.modules.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {
    public enum LogLevel {
        INFO, WARNING, ERROR
    }

    public String author();

    public LogLevel log();

    public String name();

    public String[] depend();

    public String version();
}