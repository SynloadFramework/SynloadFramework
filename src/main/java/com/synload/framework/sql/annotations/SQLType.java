package com.synload.framework.sql.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLType {
    public String Type();

    public String CharSet();

    public String Default();

    public String Collation();

    public boolean NULL();

    public boolean AutoIncrement();

    public boolean Index();

    public boolean Key();
}
