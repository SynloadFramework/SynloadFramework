package com.synload.framework.modules.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {
    public String name() default "";

    public String description() default "";

    public enum Type {
        HTTP(1), WEBSOCKET(2), OTHER(3);
        int val;

        Type(int val) {
            this.val = val;
        }

        public int getVal() {
            return val;
        }

    }

    public Type type();

    public boolean enabled() default true;

    public String[] trigger() default {};

    public String[] flags() default {};
}