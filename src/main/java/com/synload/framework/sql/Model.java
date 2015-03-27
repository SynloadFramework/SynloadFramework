package com.synload.framework.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.annotations.HasMany;
import com.synload.framework.modules.annotations.HasOne;
import com.synload.framework.modules.annotations.NonSQL;
import com.synload.framework.modules.annotations.SQLType;
import com.synload.framework.modules.annotations.sql.BigIntegerColumn;
import com.synload.framework.modules.annotations.sql.BooleanColumn;
import com.synload.framework.modules.annotations.sql.DoubleColumn;
import com.synload.framework.modules.annotations.sql.FloatColumn;
import com.synload.framework.modules.annotations.sql.LongBlobColumn;
import com.synload.framework.modules.annotations.sql.MediumIntegerColumn;
import com.synload.framework.modules.annotations.sql.StringColumn;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class Model {
    public Model(ResultSet rs) {
        try {
            for (Field f : this.getClass().getDeclaredFields()) {
                try {
                    if (_annotationPresent(f)
                            || f.isAnnotationPresent(NonSQL.class)) {
                        f.set(this,
                                _convert(f.getType(), rs.getString(f.getName())));
                    }
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Model(Object... data) {
        if ((data.length % 2) == 0) {
            for (int i = 0; i < data.length; i += 2) {
                try {
                    Field f = this.getClass().getField(String.valueOf(data[i]));
                    f.set(this,
                            _convert(f.getType(), String.valueOf(data[i + 1])));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String _tableName(String name) {
        String nm = name.toLowerCase();
        switch (nm.substring(nm.length() - 1)) {
        case "y":
            return nm.substring(0, nm.length() - 1) + "ies";
        default:
            return nm + "s";
        }

    }

    public void _save(String colName, Object data) throws SQLException,
            IllegalArgumentException, IllegalAccessException {
        for (Field f : this.getClass().getDeclaredFields()) {
            ColumnData cd = new ColumnData(f);
            if (cd.isAutoIncrement()) {
                f.setAccessible(true);
                String sql = "UPDATE `"
                        + _tableName(this.getClass().getSimpleName())
                        + "` SET `" + colName + "`=? WHERE `" + f.getName()
                        + "`=? LIMIT 1;";
                PreparedStatement ps = SynloadFramework.sql
                        .prepareStatement(sql);
                ps.setObject(1, data);
                ps.setObject(2, f.get(this));
                ps.execute();
                ps.close();
                return;
            }
        }

    }

    public static <T> String[] _getColumns(Class<T> s) {
        int x = 0;
        for (Field f : s.getDeclaredFields()) {
            if (_annotationPresent(f)) {
                x++;
            }
        }
        String[] columns = new String[x];
        x = 0;
        for (Field f : s.getDeclaredFields()) {
            if (_annotationPresent(f)) {
                columns[x] = f.getName();
                x++;
            }
        }
        return columns;
    }

    public static boolean _annotationPresent(Field f) {
        if (f.isAnnotationPresent(BigIntegerColumn.class)
                || f.isAnnotationPresent(BooleanColumn.class)
                || f.isAnnotationPresent(DoubleColumn.class)
                || f.isAnnotationPresent(FloatColumn.class)
                || f.isAnnotationPresent(LongBlobColumn.class)
                || f.isAnnotationPresent(MediumIntegerColumn.class)
                || f.isAnnotationPresent(StringColumn.class)
                || f.isAnnotationPresent(HasMany.class)
                || f.isAnnotationPresent(HasOne.class)
                || f.isAnnotationPresent(SQLType.class)) {
            return true;
        }
        return false;
    }

    public static <T> QuerySet _find(Class<T> s, String where, Object... data)
            throws InstantiationException, IllegalAccessException {
        return new QuerySet(where, data, _getColumns(s),
                _tableName(s.getSimpleName()));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> List<T> _sqlFetch(Class<T> c, String sql, Object... data)
            throws InstantiationException, IllegalAccessException,
            NoSuchMethodException, SecurityException, SQLException,
            IllegalArgumentException, InvocationTargetException {
        Constructor con = c.getConstructor(ResultSet.class);
        if (con == null) {
            System.out.println("Constructor Not Found For " + c.getName());
            return null;
        }
        List<T> ms = new ArrayList<T>();
        PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
        for (int x = 0; x < data.length; x++) {
            ps.setObject(x + 1, data[x]);
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ms.add((T) con.newInstance(rs));
        }
        rs.close();
        ps.close();
        return ms;
    }

    public <T> QuerySet _related(Class<T> c) throws NoSuchFieldException,
            SecurityException {
        for (Field f : this.getClass().getFields()) {
            if (f.isAnnotationPresent(HasMany.class)) {
                HasMany hsm = f.getAnnotation(HasMany.class);
                if (hsm.of() == c) {
                    try {
                        if (!((String) f.get(this)).equalsIgnoreCase("")) {
                            return new QuerySet("`" + hsm.key() + "` IN ("
                                + ((String) f.get(this)) + ")",
                                new Object[] {}, _getColumns(c),
                                _tableName(c.getSimpleName())
                            );
                        } else {
                            return new QuerySet("`" + hsm.key() + "`=0",
                                    new Object[] {}, _getColumns(c),
                                    _tableName(c.getSimpleName()));
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else if (f.isAnnotationPresent(HasOne.class)) {
                HasOne hso = f.getAnnotation(HasOne.class);
                if (hso.of() == c) {
                    try {
                        return new QuerySet("`" + hso.key() + "`=?",
                                new Object[] { f.get(this) }, _getColumns(c),
                                _tableName(c.getSimpleName()));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.error("No relation for " + _tableName(c.getSimpleName()),
                this.getClass());
        return new QuerySet("",new Object[]{}, _getColumns(c),_tableName(c.getSimpleName()));
    }

    @SuppressWarnings("unused")
    public void _insert() throws IllegalArgumentException,
            IllegalAccessException, SQLException {
        List<Object> values = new ArrayList<Object>();
        int x = 0;
        String sql = "";
        String sqlQs = "";
        Field autoincrement = null;
        for (Field f : this.getClass().getFields()) {
            if (_annotationPresent(f)){
                ColumnData cd = new ColumnData(f);
                if (!cd.isAutoIncrement()) {
                    // if(f.get(this)!=null){
                    sql += ((!sql.equals("")) ? ", " : "") + "`" + f.getName()
                            + "`";
                    sqlQs += ((!sqlQs.equals("")) ? ", ?" : "?");
                    values.add(_getDefault(f.get(this), f.getType()));
                    x++;
                    // }
                } else {
                    autoincrement = f;
                }
            }
        }
        Object[] valuesA = values.toArray();
        sql = "INSERT INTO `" + _tableName(this.getClass().getSimpleName())
                + "` ( " + sql + " ) VALUES ( " + sqlQs + ");";
        PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql,
                java.sql.Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < valuesA.length; i++) {
            ps.setObject(i + 1, valuesA[i]);
        }
        ps.execute();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) {
            Object genId = keys.getObject(1);
            if (autoincrement != null) {
                autoincrement.set(this, genId);
            }
        }
        ps.close();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean _exists(String where, Class c, Object... objs) {
        Object key = null;
        for (Field f : c.getFields()) {
            if (_annotationPresent(f)){
                ColumnData cd = new ColumnData(f);
                if (cd.isAutoIncrement()) {
                    key = f.getName();
                }
            }
        }
        QuerySet qs = new QuerySet(where, objs, _getColumns(c),
                _tableName(c.getSimpleName()));
        qs.ret = new String[] { "COUNT(`" + key + "`) as c" };
        Object obj = null;
        try {
            obj = qs.count();
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | NoSuchMethodException
                | SecurityException | ClassNotFoundException
                | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        }
        if (obj == null) {
            Log.error("s", c);
            return false;
        } else {
            if (Long.valueOf(String.valueOf(obj)) > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public <T> Object _getDefault(Object obj, Class c) {
        if (obj != null) {
            return obj;
        } else {
            if (c == String.class || c == Character.class || c == char.class
                    || c == Byte.class || c == byte.class) {
                return "";
            } else if (c == Integer.class || c == int.class || c == Long.class
                    || c == long.class) {
                return 0;
            } else if (c == Float.class || c == float.class
                    || c == Double.class || c == double.class) {
                return 0;
            } else if (c == Boolean.class) {
                return false;
            }
        }
        return "";
    }

    public void _addToList(Object s, Field f, Object id)
            throws IllegalArgumentException, IllegalAccessException {
        String ids = (String) f.get(s);
        List<String> comb = null;
        if (ids == null || ids.equals("")) {
            comb = new ArrayList<String>();
        } else {
            comb = new ArrayList<String>(Arrays.asList(ids.split(",")));
        }
        if (!comb.contains(String.valueOf(id))) {
            comb.add(String.valueOf(id));
            ids = StringUtils.join(comb, ",");
        }
        f.set(s, ids);
    }

    @SuppressWarnings("rawtypes")
    public Object _getKeyFromAnnotation(Class c, Object s, String key)
            throws IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException, SecurityException {
        return c.getField(key).get(s);
    }

    @SuppressWarnings("rawtypes")
    public <T> void _set(Object obj) throws IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException, SecurityException,
            SQLException {
        Model s = (Model) obj;
        Class c = obj.getClass();

        Object[] local = _getFieldAnnotation(this.getClass(), c);
        Object[] remote = _getFieldAnnotation(c, this.getClass());

        Field localField, remoteField = null;
        Object refIdLocal, refIdRemote = null;

        if (local != null) {
            localField = (Field) local[0];
            if (HasMany.class.isInstance(local[1])) {
                HasMany hsm = (HasMany) local[1];
                refIdRemote = this._getKeyFromAnnotation(c, obj, hsm.key());
                this._addToList(this, localField, refIdRemote);
            } else if (HasOne.class.isInstance(local[1])) {
                HasOne hso = (HasOne) local[1];
                refIdRemote = this._getKeyFromAnnotation(c, obj, hso.key());
                localField.set(this, refIdRemote);
            }
            this._save(localField.getName(), localField.get(this));
        } else {
            Log.error("Set relation failed " + this.getClass().getSimpleName()
                    + " => " + c.getSimpleName(), this.getClass());
        }
        if (remote != null) {
            if (this.getClass() != c) {
                remoteField = (Field) remote[0];
                if (HasMany.class.isInstance(remote[1])) {
                    HasMany hsm = (HasMany) remote[1];
                    refIdLocal = this._getKeyFromAnnotation(this.getClass(),
                            this, hsm.key());
                    s._addToList(obj, remoteField, refIdLocal);
                } else if (HasOne.class.isInstance(local[1])) {
                    HasOne hso = (HasOne) local[1];
                    refIdLocal = this._getKeyFromAnnotation(this.getClass(),
                            this, hso.key());
                    remoteField.set(obj, refIdLocal);
                }
                s._save(remoteField.getName(), remoteField.get(obj));
            }
        } else {
            Log.error("Set relation failed " + c.getSimpleName() + " => "
                    + this.getClass().getSimpleName(), this.getClass());
        }
    }

    public void _merge(Map<String, String> items) {
        for (Entry<String, String> item : items.entrySet()) {
            try {
                Field f = this.getClass().getField(item.getKey());
                if (_annotationPresent(f)){
                    if (f != null) {
                        ColumnData cd = new ColumnData(f);
                        if (!cd.isAutoIncrement()) {
                            try {
                                if (f.get(this) != _convert(f.getType(),
                                        item.getValue())) {
                                    f.set(this,
                                            _convert(f.getType(), item.getValue()));
                                    this._save(f.getName(),
                                            _convert(f.getType(), item.getValue()));
                                }
                            } catch (IllegalArgumentException
                                    | IllegalAccessException | SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static Object[] _getFieldAnnotation(Class from, Class look) {
        for (Field f : from.getFields()) {
            if (f.isAnnotationPresent(HasOne.class)) {
                HasOne mtm = f.getAnnotation(HasOne.class);
                if (mtm.of() == look) {
                    return new Object[] { f, mtm };
                }
            } else if (f.isAnnotationPresent(HasMany.class)) {
                HasMany mtm = f.getAnnotation(HasMany.class);
                if (mtm.of() == look) {
                    return new Object[] { f, mtm };
                }
            }
        }
        return null;
    }

    public static Object _convert(Class<?> target, String s) {
        if (target == Object.class || target == String.class || s == null) {
            return s;
        }
        if (target == Character.class || target == char.class) {
            return s.charAt(0);
        }
        if (target == Byte.class || target == byte.class) {
            return Byte.parseByte(s);
        }
        if (target == Short.class || target == short.class) {
            return Short.parseShort(s);
        }
        if (target == Integer.class || target == int.class) {
            return Integer.parseInt(s);
        }
        if (target == Long.class || target == long.class) {
            return Long.parseLong(s);
        }
        if (target == Float.class || target == float.class) {
            return Float.parseFloat(s);
        }
        if (target == Double.class || target == double.class) {
            return Double.parseDouble(s);
        }
        if (target == Boolean.class || target == boolean.class) {
            return ((s.equals("1")) ? true : false);
        }
        throw new IllegalArgumentException("Don't know how to convert to "
                + target);
    }
}
