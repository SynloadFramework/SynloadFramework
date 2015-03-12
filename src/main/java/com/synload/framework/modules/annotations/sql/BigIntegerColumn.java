package com.synload.framework.modules.annotations.sql;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/*
 * 
 */
public @interface BigIntegerColumn {
	public int length();
	public String Type() default "bigint";
	public String CharSet() default "";
	public String Default() default "";
	public String Collation() default "";
	public boolean NULL() default false;
	public boolean AutoIncrement() default false;
	public boolean Index() default false;
	public boolean Key() default false;
}