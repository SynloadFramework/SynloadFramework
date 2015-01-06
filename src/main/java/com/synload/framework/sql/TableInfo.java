package com.synload.framework.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TableInfo extends Model {
	
	public String Field;
	public String Type;
	public String Collation;
	public String Null;
	public String Key;
	public String Default;
	public String Extra;
	public String Privileges;
	public String Comment;
	
	public TableInfo(ResultSet rs) {
		super(rs);
	}
	
	@SuppressWarnings("rawtypes")
	public static List<TableInfo> getTableInfo(Class c){
		try {
			return TableInfo._sqlFetch( TableInfo.class, "SHOW FULL COLUMNS FROM `"+c.getSimpleName().toLowerCase()+"s`");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getField() {
		return Field;
	}
	public void setField(String field) {
		Field = field;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	public String getCollation() {
		return Collation;
	}
	public void setCollation(String collation) {
		Collation = collation;
	}
	public String getNull() {
		return Null;
	}
	public void setNull(String null1) {
		Null = null1;
	}
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	public String getDefault() {
		return Default;
	}
	public void setDefault(String default1) {
		Default = default1;
	}
	public String getExtra() {
		return Extra;
	}
	public void setExtra(String extra) {
		Extra = extra;
	}
	public String getPrivileges() {
		return Privileges;
	}
	public void setPrivileges(String privileges) {
		Privileges = privileges;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
}
