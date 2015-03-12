package com.synload.framework.sql;

import com.synload.framework.modules.annotations.SQLType;
import com.synload.framework.modules.annotations.sql.BigIntegerColumn;
import com.synload.framework.modules.annotations.sql.BooleanColumn;
import com.synload.framework.modules.annotations.sql.DoubleColumn;
import com.synload.framework.modules.annotations.sql.FloatColumn;
import com.synload.framework.modules.annotations.sql.LongBlobColumn;
import com.synload.framework.modules.annotations.sql.MediumIntegerColumn;
import com.synload.framework.modules.annotations.sql.StringColumn;

import java.lang.reflect.Field;

public class ColumnData {
	public boolean nullV = false, autoIncrement = false, index = false;
	public String type = "", defaultV = "", collation = "";
	public ColumnData(Field f){
		if(f.isAnnotationPresent(SQLType.class)){
			SQLType sqt = f.getAnnotation(SQLType.class);
			nullV = sqt.NULL();
			type = sqt.Type();
			autoIncrement = sqt.AutoIncrement();
			defaultV = sqt.Default();
			collation = sqt.Collation();
			index = sqt.Index();
		}else if(f.isAnnotationPresent(BigIntegerColumn.class)){
			BigIntegerColumn sqt = f.getAnnotation(BigIntegerColumn.class);
			nullV = sqt.NULL();
			type = sqt.Type()+"("+sqt.length()+")";
			autoIncrement = sqt.AutoIncrement();
			defaultV = sqt.Default();
			collation = sqt.Collation();
			index = sqt.Index();
		}else if(f.isAnnotationPresent(FloatColumn.class)){
			FloatColumn sqt = f.getAnnotation(FloatColumn.class);
			nullV = sqt.NULL();
			type = sqt.Type();
			autoIncrement = sqt.AutoIncrement();
			defaultV = sqt.Default();
			collation = sqt.Collation();
			index = sqt.Index();
		}else if(f.isAnnotationPresent(DoubleColumn.class)){
			DoubleColumn sqt = f.getAnnotation(DoubleColumn.class);
			nullV = sqt.NULL();
			type = sqt.Type();
			autoIncrement = sqt.AutoIncrement();
			defaultV = sqt.Default();
			collation = sqt.Collation();
			index = sqt.Index();
		}else if(f.isAnnotationPresent(MediumIntegerColumn.class)){
			MediumIntegerColumn sqt = f.getAnnotation(MediumIntegerColumn.class);
			nullV = sqt.NULL();
			type = sqt.Type()+"("+sqt.length()+")";
			defaultV = sqt.Default();
			autoIncrement = sqt.AutoIncrement();
			collation = sqt.Collation();
			index = sqt.Index();
		}else if(f.isAnnotationPresent(StringColumn.class)){
			StringColumn sqt = f.getAnnotation(StringColumn.class);
			nullV = sqt.NULL();
			type = sqt.Type()+"("+sqt.length()+")";
			defaultV = sqt.Default();
			autoIncrement = sqt.AutoIncrement();
			collation = sqt.Collation();
			index = sqt.Index();
		}else if(f.isAnnotationPresent(BooleanColumn.class)){
			BooleanColumn sqt = f.getAnnotation(BooleanColumn.class);
			nullV = sqt.NULL();
			type = sqt.Type();
			defaultV = sqt.Default();
			collation = sqt.Collation();
			autoIncrement = sqt.AutoIncrement();
			index = sqt.Index();
		}else if(f.isAnnotationPresent(LongBlobColumn.class)){
			LongBlobColumn sqt = f.getAnnotation(LongBlobColumn.class);
			nullV = sqt.NULL();
			type = sqt.Type();
			defaultV = sqt.Default();
			collation = sqt.Collation();
			autoIncrement = sqt.AutoIncrement();
			index = sqt.Index();
		}
	}
	public boolean isNullV() {
		return nullV;
	}
	public void setNullV(boolean nullV) {
		this.nullV = nullV;
	}
	public boolean isAutoIncrement() {
		return autoIncrement;
	}
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDefaultV() {
		return defaultV;
	}
	public void setDefaultV(String defaultV) {
		this.defaultV = defaultV;
	}
	public String getCollation() {
		return collation;
	}
	public void setCollation(String collation) {
		this.collation = collation;
	}
	public boolean isIndex() {
		return index;
	}
	public void setIndex(boolean index) {
		this.index = index;
	}
}
