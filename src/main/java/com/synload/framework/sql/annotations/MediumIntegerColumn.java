package com.synload.framework.sql.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MediumIntegerColumn {
    public int length();

    public String Type() default "mediumint";

    public String CharSet() default "";

    public String Default() default "";

    public String Collation() default "";

    public boolean NULL() default false;

    public boolean AutoIncrement() default false;

    public boolean Index() default false;

    public boolean Key() default false;
}