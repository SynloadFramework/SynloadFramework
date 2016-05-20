package com.synload.framework.sql;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.synload.framework.SynloadFramework;

public class QuerySet {
    public String limit = null;
    public String[] order = {};
    public String[] columns = null;
    public String where;
    public Object[] data;
    public String[] ret = { "*" };
    public String name = "";

    public QuerySet(String where, Object[] data, String[] cols, String name) {
        this.where = where;
        this.data = data;
        this.columns = cols;
        this.name = name;
    }

    public QuerySet limit(String lm) {
        this.limit = lm;
        return this;
    }

    public QuerySet orderBy(String... ob) {
        this.order = ob;
        return this;
    }

    public QuerySet returnColumns(String[] cols) {
        this.ret = cols;
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> List<T> exec(Class<T> c) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException, SQLException,
            NoSuchMethodException, SecurityException, ClassNotFoundException,
            InvocationTargetException {

        Constructor con = c.getConstructor(ResultSet.class);
        if (con == null) {
            System.out.println("Constructor Not Found For " + c.getName());
        }

        List<T> ms = new ArrayList<T>();
        String sql = "SELECT ";
        if (this.ret.length == 1) {
            if (this.ret[0].equals("*")) {
                sql += this.columnToString(columns);
            } else {
                sql += "`" + this.ret[0] + "`";
            }
        } else {
            sql += this.columnToString(this.ret);
        }
        sql += " FROM `" + name + "`";
        if (where != "") {
            sql += " WHERE " + where;
        }
        if (order.length > 0) {
            sql += " ORDER BY " + StringUtils.join(order, ", ");
        }
        if (limit != null) {
            sql += " LIMIT " + limit;
        }
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

    public int count() throws InstantiationException,
            IllegalAccessException, IllegalArgumentException, SQLException,
            NoSuchMethodException, SecurityException, ClassNotFoundException,
            InvocationTargetException {
        int c = -1;
        String sql = "SELECT ";
        sql += "COUNT(*) as c";
        sql += " FROM `" + name + "`";
        if (where != "") {
            sql += " WHERE " + where;
        }
        if (limit != null) {
            sql += " LIMIT " + limit;
        }
        PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
        for (int x = 0; x < data.length; x++) {
            ps.setObject(x + 1, data[x]);
        }
        ResultSet rs = ps.executeQuery();
        rs.next();
        c = rs.getInt("c");
        rs.close();
        ps.close();
        return c;
    }

    public String columnToString(String[] items) {
        String out = "";
        for (String item : items) {
            if (item.contains("`")) {
                out += ((out != "") ? ", " : "") + item;
            } else {
                out += ((out != "") ? ", " : "") + "`" + item + "`";
            }
        }
        return out;
    }
}