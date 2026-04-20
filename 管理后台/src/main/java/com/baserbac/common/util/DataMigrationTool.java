package com.baserbac.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据迁移工具类
 * 用于将 base_rbac 数据库中的数据迁移到 small_tweaks_erp 数据库
 */
public class DataMigrationTool {

    private static final String SOURCE_DB = "base_rbac";
    private static final String TARGET_DB = "small_tweaks_erp";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/%s?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String[] TABLES = {
        "sys_role",
        "sys_user",
        "sys_menu",
        "sys_api_permission",
        "sys_user_role",
        "sys_role_menu",
        "sys_role_api",
        "sys_operation_log",
        "sys_login_log"
    };

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   开始数据迁移...");
        System.out.println("   源数据库: " + SOURCE_DB);
        System.out.println("   目标数据库: " + TARGET_DB);
        System.out.println("========================================");

        try {
            migrateData();
            System.out.println("\n========================================");
            System.out.println("   数据迁移完成！");
            System.out.println("========================================");
        } catch (Exception e) {
            System.err.println("数据迁移失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void migrateData() throws SQLException {
        try (Connection sourceConn = getConnection(SOURCE_DB);
             Connection targetConn = getConnection(TARGET_DB)) {

            targetConn.setAutoCommit(false);

            try {
                for (String tableName : TABLES) {
                    System.out.print("\n迁移表 " + tableName + " ... ");
                    
                    int count = migrateTable(sourceConn, targetConn, tableName);
                    
                    System.out.println("完成，迁移 " + count + " 条记录");
                }
                
                targetConn.commit();
                System.out.println("\n事务已提交");
                
            } catch (SQLException e) {
                targetConn.rollback();
                System.err.println("事务已回滚");
                throw e;
            }
        }
    }

    private static Connection getConnection(String dbName) throws SQLException {
        String url = String.format(DB_URL, dbName);
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    private static int migrateTable(Connection sourceConn, Connection targetConn, String tableName) throws SQLException {
        int count = 0;
        
        String countSql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement countStmt = sourceConn.createStatement();
             ResultSet countRs = countStmt.executeQuery(countSql)) {
            if (countRs.next()) {
                int totalCount = countRs.getInt(1);
                if (totalCount == 0) {
                    System.out.print("(源表为空) ");
                    return 0;
                }
            }
        }

        String checkSql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement checkStmt = targetConn.createStatement();
             ResultSet checkRs = checkStmt.executeQuery(checkSql)) {
            if (checkRs.next()) {
                int existingCount = checkRs.getInt(1);
                if (existingCount > 0) {
                    System.out.print("(目标表已有 " + existingCount + " 条记录，跳过) ");
                    return existingCount;
                }
            }
        } catch (SQLException e) {
            System.out.print("(目标表不存在或错误: " + e.getMessage() + ") ");
            return 0;
        }

        String selectSql = "SELECT * FROM " + tableName;
        try (Statement sourceStmt = sourceConn.createStatement();
             ResultSet rs = sourceStmt.executeQuery(selectSql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            while (rs.next()) {
                StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
                StringBuilder values = new StringBuilder("VALUES (");
                
                for (int i = 0; i < columnNames.size(); i++) {
                    if (i > 0) {
                        insertSql.append(", ");
                        values.append(", ");
                    }
                    insertSql.append(columnNames.get(i));
                    
                    Object value = rs.getObject(i + 1);
                    if (value == null) {
                        values.append("NULL");
                    } else if (value instanceof Number) {
                        values.append(value);
                    } else {
                        values.append("'").append(escapeSql(value.toString())).append("'");
                    }
                }
                
                insertSql.append(") ");
                values.append(")");
                
                String fullSql = insertSql.toString() + values.toString();
                
                try (Statement targetStmt = targetConn.createStatement()) {
                    targetStmt.executeUpdate(fullSql);
                }
                
                count++;
            }
        }
        
        return count;
    }

    private static String escapeSql(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }
}
