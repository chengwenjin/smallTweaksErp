package com.erp.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * ERP模块数据初始化组件
 * 在应用启动时检查并初始化ERP相关的数据库表和菜单数据
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class ErpDataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========================================");
        log.info("   检查ERP模块数据初始化...");
        log.info("========================================");

        try {
            // 1. 检查并创建产品表
            checkAndCreateProductTable();
            
            // 2. 检查并插入菜单数据
            checkAndInsertMenuData();
            
            log.info("========================================");
            log.info("   ERP模块数据初始化完成！");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("ERP模块数据初始化失败: {}", e.getMessage());
            log.warn("应用将继续启动，但ERP功能可能无法正常使用");
        }
    }

    /**
     * 检查并创建产品表
     */
    private void checkAndCreateProductTable() {
        try {
            // 检查表是否存在
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_product'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("产品表 erp_product 已存在");
                
                // 检查表中是否有数据
                Long dataCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_product",
                    Long.class
                );
                
                if (dataCount != null && dataCount == 0) {
                    log.info("产品表为空，插入示例数据...");
                    insertSampleProductData();
                }
                return;
            }

            log.info("产品表 erp_product 不存在，创建表...");
            
            // 创建表
            String createTableSql = """
                CREATE TABLE `erp_product` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `product_code` VARCHAR(50) NOT NULL COMMENT '产品编码（全局唯一）',
                    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `category` VARCHAR(50) DEFAULT NULL COMMENT '产品分类',
                    `brand` VARCHAR(50) DEFAULT NULL COMMENT '品牌',
                    `technical_params` TEXT DEFAULT NULL COMMENT '技术参数（JSON格式）',
                    `description` TEXT DEFAULT NULL COMMENT '产品描述',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_product_code` (`product_code`),
                    KEY `idx_product_name` (`product_name`),
                    KEY `idx_category` (`category`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品档案表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("产品表创建成功");
            
            // 插入示例数据
            insertSampleProductData();
            
        } catch (Exception e) {
            log.error("创建产品表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例产品数据
     */
    private void insertSampleProductData() {
        try {
            String insertSql = """
                INSERT INTO `erp_product` (`product_code`, `product_name`, `specification`, `unit`, `category`, `brand`, `technical_params`, `description`, `status`) VALUES
                ('PRD001', '台式计算机', 'ThinkCentre M90a', '台', '计算机设备', '联想', '{"cpu":"Intel i5-10400","memory":"16GB","storage":"512GB SSD","monitor":"23.8英寸"}', '联想台式计算机，商务办公用', 1),
                ('PRD002', '笔记本电脑', 'ThinkPad X1 Carbon', '台', '计算机设备', '联想', '{"cpu":"Intel i7-1165G7","memory":"16GB","storage":"1TB SSD","screen":"14英寸"}', '联想轻薄商务笔记本', 1),
                ('PRD003', '激光打印机', 'HP LaserJet Pro M404dn', '台', '办公设备', '惠普', '{"print_speed":"40ppm","resolution":"1200x1200dpi","connectivity":"USB, Ethernet"}', '惠普黑白激光打印机', 1),
                ('PRD004', '投影仪', 'Epson CB-X50', '台', '办公设备', '爱普生', '{"brightness":"3600流明","resolution":"1024x768","contrast":"16000:1"}', '爱普生商务投影仪', 1),
                ('PRD005', '网络交换机', 'Cisco Catalyst 2960', '台', '网络设备', '思科', '{"ports":"24口","speed":"10/100/1000Mbps","type":"千兆交换机"}', '思科千兆网络交换机', 1)
                """;
            
            jdbcTemplate.execute(insertSql);
            log.info("示例产品数据插入成功");
            
        } catch (Exception e) {
            log.error("插入示例产品数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并插入菜单数据
     */
    private void checkAndInsertMenuData() {
        try {
            // 检查是否已存在产品与BOM管理菜单
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_menu WHERE menu_name = '产品与BOM管理' AND parent_id = 0",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("菜单数据已存在，跳过插入");
                return;
            }

            log.info("菜单数据不存在，插入菜单数据...");

            // 1. 插入顶级菜单：产品与BOM管理
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                0, "产品与BOM管理", 1, null, "/erp", "Layout", "Goods", 70, 1, 1, 1
            );

            // 获取刚插入的菜单ID
            Long erpMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            // 2. 插入二级菜单：基础数据管理
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                erpMenuId, "基础数据管理", 1, null, "base-data", "Layout", "Setting", 1, 1, 1, 1
            );

            Long baseDataMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            // 3. 插入三级菜单：产品档案
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                baseDataMenuId, "产品档案", 2, "erp:product:list", "product", "erp/Product", "Goods", 1, 1, 1, 1
            );

            Long productMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            // 4. 插入按钮权限
            String[][] buttons = {
                {"新增产品", "erp:product:add", "1"},
                {"编辑产品", "erp:product:edit", "2"},
                {"删除产品", "erp:product:delete", "3"}
            };

            for (String[] button : buttons) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    productMenuId, button[0], 3, button[1], Integer.parseInt(button[2]), 1, 1
                );
            }

            // 5. 给超级管理员分配菜单权限
            // 先查询所有新插入的菜单ID
            List<Map<String, Object>> menuIds = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE path LIKE '/erp%' OR menu_name LIKE '产品%' OR menu_name LIKE '基础数据%' OR menu_name = '产品与BOM管理'"
            );

            for (Map<String, Object> menu : menuIds) {
                Long menuId = ((Number) menu.get("id")).longValue();
                jdbcTemplate.update(
                    "INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (?, ?)",
                    1, menuId
                );
            }

            log.info("菜单数据插入成功");
            
        } catch (Exception e) {
            log.error("插入菜单数据失败: {}", e.getMessage());
        }
    }
}
