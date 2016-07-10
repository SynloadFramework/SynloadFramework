package com.synload.framework.sql;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.sql.annotations.NonSQL;
import com.synload.framework.sql.annotations.SQLTable;

import dnl.utils.text.table.TextTable;

//
public class SQLRegistry {

    @SuppressWarnings("rawtypes")
    public static List<Class> sqltables = new ArrayList<Class>();

    public static <T> void register(Class<T> c) {
        sqltables.add(c);
    }

    @SuppressWarnings("rawtypes")
    public static void updateTable(Class table, Field f) throws SQLException {
        if (Model._annotationPresent(f) && !f.isAnnotationPresent(NonSQL.class)) {
            String sql = "";
            ColumnData cd = new ColumnData(f);
            sql += "ALTER TABLE  `" + Model._tableName(table.getSimpleName())
                    + "` CHANGE `" + f.getName() + "` " + cd.getType() + "";
            if (!cd.getCollation().equalsIgnoreCase("")) {
                sql += " COLLATE " + cd.getCollation();
            }
            if (cd.isNullV()) {
                sql += " NULL";
            } else {
                sql += " NOT NULL";
            }
            if (!cd.getDefaultV().equalsIgnoreCase("")) {
                sql += "DEFAULT '" + cd.getDefaultV() + "'";
            }
            if (cd.isAutoIncrement()) {
                sql += " AUTO_INCREMENT";
            }
            PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
            ps.execute();
            ps.close();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void addIndex(Class table, Field f) throws SQLException {
        if (Model._annotationPresent(f)) {
            String sql = "ALTER TABLE  `"
                    + Model._tableName(table.getSimpleName())
                    + "` ADD INDEX (  `" + f.getName() + "` )";
            PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
            ps.execute();
            ps.close();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void updateComment(Class c) {
        try {
            SQLTable sqt = ((SQLTable) c.getAnnotation(SQLTable.class));
            PreparedStatement ps = SynloadFramework.sql
                    .prepareStatement("ALTER TABLE `"
                            + Model._tableName(c.getSimpleName())
                            + "` COMMENT = '" + String.valueOf(sqt.version())
                            + "'");
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void dropIndex(Class table, Field f) throws SQLException {
        if (Model._annotationPresent(f)) {
            String sql = "ALTER TABLE tags DROP INDEX " + f.getName();
            PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
            ps.execute();
            ps.close();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void addKey(Class table, Field f) throws SQLException {
        if (Model._annotationPresent(f)) {
            String sql = "ALTER TABLE `"
                    + Model._tableName(table.getSimpleName())
                    + "` ADD PRIMARY KEY(`" + f.getName() + "`)";
            PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
            ps.execute();
            ps.close();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void dropKey(Class table) throws SQLException {
        String sql = "ALTER TABLE `" + Model._tableName(table.getSimpleName())
                + "` DROP PRIMARY KEY";
        PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
        ps.execute();
        ps.close();
    }

    @SuppressWarnings("rawtypes")
    public static void dropColumn(Class table, TableInfo col)
            throws SQLException {
        String sql = "ALTER TABLE `" + Model._tableName(table.getSimpleName())
                + "` DROP `" + col.getField() + "`";
        PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
        ps.execute();
        ps.close();
    }

    @SuppressWarnings("rawtypes")
    public static void addColumn(Class table, Field f) throws SQLException {
        if (Model._annotationPresent(f)) {
            ColumnData cd = new ColumnData(f);
            String sql = "ALTER TABLE  `"
                    + Model._tableName(table.getSimpleName()) + "` ADD `"
                    + f.getName() + "` " + cd.getType() + "";
            if (!cd.getCollation().equalsIgnoreCase("")) {
                sql += " COLLATE " + cd.getCollation();
            }
            if (cd.isNullV()) {
                sql += " NULL";
            } else {
                sql += " NOT NULL";
            }
            if (!cd.getDefaultV().equalsIgnoreCase("")) {
                sql += "DEFAULT '" + cd.getDefaultV() + "'";
            }
            if (cd.isAutoIncrement()) {
                sql += " AUTO_INCREMENT";
            }
            PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
            ps.execute();
            ps.close();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void checkVersions() {
        if(!SynloadFramework.sqlManager){
            Log.info("SQL Manager Disabled", SQLRegistry.class);
            return;
        }
        List<Object[]> sql = new ArrayList<Object[]>();
        for (Class table : sqltables) {
            try{
                Object[] obj = new Object[4];
                Field[] fs = Model._getFields(table);
                SQLTable sqltable = (SQLTable) table.getAnnotation(SQLTable.class);
                TableStatus ts = TableStatus.get(table);
                if (ts == null) {
                    try {
                        createTable(table, fs);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else{
                    obj[0] = Model._tableName(table.getSimpleName());
                    obj[1] = String.valueOf(sqltable.version());
                    obj[2] = ts.getComment();
                    obj[3] = "";
                    if (ts.getComment().equals(String.valueOf(sqltable.version()))) {
                        obj[3] = "up to date!";
                    } else {
                        obj[3] = obj[3] + "not up to date!";
                        List<TableInfo> tis = TableInfo.getTableInfo(table);
                        List<TableInfo> foundTables = new ArrayList<TableInfo>(tis);
                        if (tis.size() == 0 || foundTables.size() == 0) {
                            System.out.println("[SQL][ERROR]\t\t [" + sqltable.name()
                                    + "] table info not found");
                            return;
                        }
                        for (Field f : fs) {
                            if (Model._annotationPresent(f) && !f.isAnnotationPresent(NonSQL.class)) {
                                ColumnData cd = new ColumnData(f);
                                boolean notFound = true;
                                for (TableInfo ti : tis) {
                                    if (f.getName().equalsIgnoreCase(ti.getField())) {
                                        notFound = false;
                                        foundTables.remove(ti);
                                        if (cd.isNullV() != ti.getNull()
                                                .equalsIgnoreCase("YES")) {
                                            obj[3] = obj[3] + ", \"" + ti.getField()
                                                    + "\" null changed";
                                            try {
                                                updateTable(table, f);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (cd.getCollation().equalsIgnoreCase(
                                                ti.getCollation())) {
                                            obj[3] = obj[3] + ", \"" + ti.getField()
                                                    + "\" collation changed";
                                            try {
                                                updateTable(table, f);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                                if (notFound) {
                                    obj[3] = obj[3] + ", added column \"" + f.getName() + "\"";
                                    try {
                                        addColumn(table, f);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        for (TableInfo col : foundTables) {
                            if (!col.getField().equalsIgnoreCase("")) {
                                obj[3] = obj[3] + ", removed column \"" + col.getField() + "\"";
                                try {
                                    dropColumn(table, col);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    
                    sql.add(obj);
                    updateComment(table);
                }
            }catch(Exception e1){
                e1.printStackTrace();
            }
        }
        Log.info("SQL Version Checks", SQLRegistry.class);
        TextTable tt = new TextTable(new String[] { "Table", "Model Version",
                "SQL Version", "Actions" },
                sql.toArray(new Object[sql.size()][]));
        tt.printTable();
        Log.info("\n", SQLRegistry.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean createTable(Class table, Field[] fs)
            throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `"
                + Model._tableName(table.getSimpleName()) + "` ( ";
        boolean t = true;
        for (Field f : fs) {
            if (Model._annotationPresent(f) && !f.isAnnotationPresent(NonSQL.class)) {
                if (!t) {
                    sql += ", ";
                } else {
                    t = false;
                }
                sql += columnCreate(f);
            }
        }
        SQLTable sqltable = (SQLTable) table.getAnnotation(SQLTable.class);
        sql += ") ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '"
                + sqltable.version() + "';";
        PreparedStatement ps = SynloadFramework.sql.prepareStatement(sql);
        boolean worked = ps.execute();
        ps.close();
        for (Field f : fs) {
            if (Model._annotationPresent(f) && !f.isAnnotationPresent(NonSQL.class)) {
                ColumnData cd = new ColumnData(f);
                if (cd.isIndex()) {
                    addIndex(table, f);
                }
            }
        }
        return worked;
    }

    public static String columnCreate(Field f) {
        String sql = "";
        if (Model._annotationPresent(f) && !f.isAnnotationPresent(NonSQL.class)) {
            ColumnData cd = new ColumnData(f);
            sql += "`" + f.getName() + "` " + cd.getType();
            if (cd.isNullV()) {
                sql += " NULL";
            } else {
                sql += " NOT NULL";
            }
            if (cd.isAutoIncrement()) {
                sql += " AUTO_INCREMENT, PRIMARY KEY (`" + f.getName() + "`) ";
            }
        }
        return sql;
    }
}
