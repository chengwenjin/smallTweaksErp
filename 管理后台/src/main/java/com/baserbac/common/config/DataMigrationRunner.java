package com.baserbac.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据迁移组件
 * 在应用启动时检查并执行数据迁移（从 base_rbac 到 small_tweaks_erp）
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataMigrationRunner implements ApplicationRunner {

    private final JdbcTemplate targetJdbcTemplate;
    
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========================================");
        log.info("   检查数据迁移状态...");
        log.info("========================================");

        try {
            Long userCount = targetJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_user", Long.class);
            
            if (userCount != null && userCount > 0) {
                log.info("目标数据库 {} 已有数据（用户数: {}），跳过迁移", TARGET_DB, userCount);
                return;
            }

            log.info("目标数据库 {} 为空，开始从 {} 迁移数据...", TARGET_DB, SOURCE_DB);
            
            migrateData();
            
            log.info("========================================");
            log.info("   数据迁移完成！");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("数据迁移检查失败: {}", e.getMessage());
            log.warn("如果 {} 数据库不存在或无法连接，请忽略此错误", SOURCE_DB);
            log.warn("应用将继续启动，但可能需要手动初始化数据");
        }
    }

    private void migrateData() {
        DataSource sourceDataSource = createSourceDataSource();
        JdbcTemplate sourceJdbcTemplate = new JdbcTemplate(sourceDataSource);

        try {
            for (String tableName : TABLES) {
                migrateTable(sourceJdbcTemplate, tableName);
            }
            
            log.info("所有表数据迁移成功完成！");
            
        } catch (Exception e) {
            log.error("数据迁移过程中发生错误: {}", e.getMessage());
            throw e;
        }
    }

    private DataSource createSourceDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(String.format(DB_URL, SOURCE_DB));
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }

    private void migrateTable(JdbcTemplate sourceJdbcTemplate, String tableName) {
        log.info("迁移表 {}...", tableName);
        
        try {
            Long sourceCount = sourceJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + tableName, Long.class);
            
            if (sourceCount == null || sourceCount == 0) {
                log.info("表 {} 源数据库为空，跳过", tableName);
                return;
            }

            log.info("表 {} 源数据库有 {} 条记录，开始迁移...", tableName, sourceCount);

            List<Map<String, Object>> rows = sourceJdbcTemplate.queryForList("SELECT * FROM " + tableName);
            
            if (rows.isEmpty()) {
                log.info("表 {} 没有数据需要迁移", tableName);
                return;
            }

            Map<String, Object> firstRow = rows.get(0);
            List<String> columnNames = new ArrayList<>(firstRow.keySet());

            StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
            for (int i = 0; i < columnNames.size(); i++) {
                if (i > 0) insertSql.append(", ");
                insertSql.append(columnNames.get(i));
            }
            insertSql.append(") VALUES (");
            for (int i = 0; i < columnNames.size(); i++) {
                if (i > 0) insertSql.append(", ");
                insertSql.append("?");
            }
            insertSql.append(")");

            int migratedCount = 0;
            for (Map<String, Object> row : rows) {
                List<Object> values = new ArrayList<>();
                for (String columnName : columnNames) {
                    values.add(row.get(columnName));
                }
                
                targetJdbcTemplate.update(insertSql.toString(), values.toArray());
                migratedCount++;
            }

            log.info("表 {} 迁移完成，共迁移 {} 条记录", tableName, migratedCount);
            
        } catch (Exception e) {
            log.warn("表 {} 迁移时出现问题: {}", tableName, e.getMessage());
            log.warn("如果该表不存在于源数据库或目标数据库，请忽略此警告");
        }
    }
}
