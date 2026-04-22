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
            
            // 2. 检查并创建物料表
            checkAndCreateMaterialTable();
            
            // 3. 检查并创建BOM表
            checkAndCreateBomTable();
            
            // 4. 检查并创建BOM版本表
            checkAndCreateBomVersionTable();
            
            // 5. 检查并创建替代料表
            checkAndCreateAlternativeMaterialTable();
            
            // 6. 检查并插入菜单数据
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
                log.info("产品与BOM管理菜单已存在，跳过插入");
            } else {
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
            }
            
        } catch (Exception e) {
            log.error("插入菜单数据失败: {}", e.getMessage());
        }
        
        // 检查并插入物料主数据菜单（单独处理，因为菜单可能已存在）
        try {
            checkAndInsertMaterialMenu();
        } catch (Exception e) {
            log.error("插入物料主数据菜单失败: {}", e.getMessage());
        }
        
        // 检查并插入BOM管理菜单（单独处理，因为菜单可能已存在）
        try {
            checkAndInsertBomMenu();
        } catch (Exception e) {
            log.error("插入BOM管理菜单失败: {}", e.getMessage());
        }
        
        // 检查并插入BOM版本与替代料菜单（单独处理，因为菜单可能已存在）
        try {
            checkAndInsertBomVersionMenu();
        } catch (Exception e) {
            log.error("插入BOM版本与替代料菜单失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建物料表
     */
    private void checkAndCreateMaterialTable() {
        try {
            // 检查表是否存在
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_material'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("物料表 erp_material 已存在");
                
                // 检查表中是否有数据
                Long dataCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_material",
                    Long.class
                );
                
                if (dataCount != null && dataCount == 0) {
                    log.info("物料表为空，插入示例数据...");
                    insertSampleMaterialData();
                }
                return;
            }

            log.info("物料表 erp_material 不存在，创建表...");
            
            // 创建表
            String createTableSql = """
                CREATE TABLE `erp_material` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `material_code` VARCHAR(50) NOT NULL COMMENT '物料编码（全局唯一）',
                    `material_name` VARCHAR(100) NOT NULL COMMENT '物料名称',
                    `material_type` TINYINT NOT NULL COMMENT '物料类型：1原材料 2半成品 3成品',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `category` VARCHAR(50) DEFAULT NULL COMMENT '物料分类',
                    `brand` VARCHAR(50) DEFAULT NULL COMMENT '品牌',
                    `custom_attributes` TEXT DEFAULT NULL COMMENT '自定义属性（JSON格式）',
                    `description` TEXT DEFAULT NULL COMMENT '描述',
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
                    UNIQUE KEY `uk_material_code` (`material_code`),
                    KEY `idx_material_name` (`material_name`),
                    KEY `idx_material_type` (`material_type`),
                    KEY `idx_category` (`category`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料主数据表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("物料表创建成功");
            
            // 插入示例数据
            insertSampleMaterialData();
            
        } catch (Exception e) {
            log.error("创建物料表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例物料数据
     */
    private void insertSampleMaterialData() {
        try {
            String insertSql = """
                INSERT INTO `erp_material` (`material_code`, `material_name`, `material_type`, `specification`, `unit`, `category`, `brand`, `custom_attributes`, `description`, `status`) VALUES
                ('MAT001', '钢材', 1, 'Q235 10mm', '吨', '金属材料', '宝钢', '{"颜色":"黑色","材质":"碳钢","规格":"10mm"}', '优质碳素结构钢', 1),
                ('MAT002', '铝合金板材', 1, '6061-T6 2mm', '千克', '金属材料', '中铝', '{"颜色":"银色","材质":"铝合金","厚度":"2mm"}', '6061系列铝合金板材', 1),
                ('MAT003', '塑料颗粒', 1, 'ABS 黑色', '千克', '塑料材料', '奇美', '{"颜色":"黑色","材质":"ABS","型号":"PA-757"}', 'ABS工程塑料颗粒', 1),
                ('MAT004', '电路板组件', 2, 'PCB-V1.0', '块', '电子组件', '自研', '{"层数":"4层","尺寸":"100x80mm","材质":"FR-4"}', '主控制电路板半成品', 1),
                ('MAT005', '电机模组', 2, 'MOTOR-50W', '个', '电子组件', '自研', '{"功率":"50W","电压":"24V","转速":"3000rpm"}', '直流减速电机模组', 1),
                ('MAT006', '智能控制器', 3, 'CTRL-V2.0', '台', '成品', '自研', '{"型号":"CTRL-V2.0","功率":"100W","通信":"RS485/WiFi"}', '工业智能控制器成品', 1),
                ('MAT007', '传感器模块', 3, 'SENS-TEMP', '个', '成品', '自研', '{"类型":"温度","量程":"-40~125℃","精度":"±0.5℃"}', '高精度温度传感器成品', 1)
                """;
            
            jdbcTemplate.execute(insertSql);
            log.info("示例物料数据插入成功");
            
        } catch (Exception e) {
            log.error("插入示例物料数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并插入物料主数据菜单
     */
    private void checkAndInsertMaterialMenu() {
        try {
            // 检查是否已存在物料主数据菜单
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_menu WHERE menu_name = '物料主数据'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("物料主数据菜单已存在，跳过插入");
                return;
            }

            log.info("物料主数据菜单不存在，插入菜单数据...");

            // 查找基础数据管理菜单ID
            List<Map<String, Object>> baseDataMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '基础数据管理' LIMIT 1"
            );
            
            Long baseDataMenuId = 0L;
            if (!baseDataMenus.isEmpty()) {
                baseDataMenuId = ((Number) baseDataMenus.get(0).get("id")).longValue();
            }

            // 插入物料主数据菜单
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                baseDataMenuId, "物料主数据", 2, "erp:material:list", "materials", "erp/Material", "Goods", 2, 1, 1, 1
            );

            Long materialMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            // 插入按钮权限
            String[][] buttons = {
                {"新增物料", "erp:material:add", "1"},
                {"编辑物料", "erp:material:edit", "2"},
                {"删除物料", "erp:material:delete", "3"}
            };

            for (String[] button : buttons) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    materialMenuId, button[0], 3, button[1], Integer.parseInt(button[2]), 1, 1
                );
            }

            // 给超级管理员分配菜单权限
            jdbcTemplate.update(
                "INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (?, ?)",
                1, materialMenuId
            );

            log.info("物料主数据菜单插入成功");
            
        } catch (Exception e) {
            log.error("插入物料主数据菜单失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建BOM表
     */
    private void checkAndCreateBomTable() {
        try {
            // 检查表是否存在
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_bom'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("BOM表 erp_bom 已存在");
                
                // 检查表中是否有数据
                Long dataCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_bom",
                    Long.class
                );
                
                if (dataCount != null && dataCount == 0) {
                    log.info("BOM表为空，插入示例数据...");
                    insertSampleBomData();
                }
                return;
            }

            log.info("BOM表 erp_bom 不存在，创建表...");
            
            // 创建表
            String createTableSql = """
                CREATE TABLE `erp_bom` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `parent_id` BIGINT NOT NULL COMMENT '父件ID（对应erp_product或erp_material的ID）',
                    `parent_type` TINYINT NOT NULL COMMENT '父件类型：1产品 2物料',
                    `child_id` BIGINT NOT NULL COMMENT '子件ID（对应erp_product或erp_material的ID）',
                    `child_type` TINYINT NOT NULL COMMENT '子件类型：1产品 2物料',
                    `quantity` DECIMAL(10,3) NOT NULL COMMENT '用量',
                    `unit` VARCHAR(20) NOT NULL COMMENT '单位',
                    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
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
                    UNIQUE KEY `uk_bom_relation` (`parent_id`, `parent_type`, `child_id`, `child_type`),
                    KEY `idx_parent` (`parent_id`, `parent_type`),
                    KEY `idx_child` (`child_id`, `child_type`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("BOM表创建成功");
            
            // 插入示例数据
            insertSampleBomData();
            
        } catch (Exception e) {
            log.error("创建BOM表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例BOM数据
     */
    private void insertSampleBomData() {
        try {
            // 先查询产品和物料的ID
            List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT id, product_code FROM erp_product LIMIT 3"
            );
            
            List<Map<String, Object>> materials = jdbcTemplate.queryForList(
                "SELECT id, material_code FROM erp_material LIMIT 5"
            );
            
            if (products.size() >= 2 && materials.size() >= 3) {
                Long product1Id = ((Number) products.get(0).get("id")).longValue();
                Long product2Id = ((Number) products.get(1).get("id")).longValue();
                Long material1Id = ((Number) materials.get(0).get("id")).longValue();
                Long material2Id = ((Number) materials.get(1).get("id")).longValue();
                Long material3Id = ((Number) materials.get(2).get("id")).longValue();
                
                String insertSql = """
                    INSERT INTO `erp_bom` (`parent_id`, `parent_type`, `child_id`, `child_type`, `quantity`, `unit`, `remark`, `status`) VALUES
                    (?, 1, ?, 2, 2.5, '个', '产品1需要2.5个物料1', 1),
                    (?, 1, ?, 2, 1.0, '个', '产品1需要1个物料2', 1),
                    (?, 1, ?, 2, 0.5, '个', '产品1需要0.5个物料3', 1),
                    (?, 1, ?, 2, 1.5, '个', '产品2需要1.5个物料1', 1),
                    (?, 1, ?, 1, 1.0, '个', '产品2需要1个产品1', 1)
                    """;
                
                jdbcTemplate.update(insertSql, 
                    product1Id, material1Id,
                    product1Id, material2Id,
                    product1Id, material3Id,
                    product2Id, material1Id,
                    product2Id, product1Id
                );
                
                log.info("示例BOM数据插入成功");
            }
            
        } catch (Exception e) {
            log.error("插入示例BOM数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并插入BOM管理菜单
     */
    private void checkAndInsertBomMenu() {
        try {
            // 检查是否已存在BOM管理菜单
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_menu WHERE menu_name = 'BOM管理'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("BOM管理菜单已存在，跳过插入");
                return;
            }

            log.info("BOM管理菜单不存在，插入菜单数据...");

            // 查找基础数据管理菜单ID
            List<Map<String, Object>> baseDataMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '基础数据管理' LIMIT 1"
            );
            
            Long baseDataMenuId = 0L;
            if (!baseDataMenus.isEmpty()) {
                baseDataMenuId = ((Number) baseDataMenus.get(0).get("id")).longValue();
            }

            // 插入BOM管理菜单
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                baseDataMenuId, "BOM管理", 2, "erp:bom:list", "boms", "erp/Bom", "Tree", 3, 1, 1, 1
            );

            Long bomMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            // 插入按钮权限
            String[][] buttons = {
                {"新增BOM", "erp:bom:add", "1"},
                {"编辑BOM", "erp:bom:edit", "2"},
                {"删除BOM", "erp:bom:delete", "3"}
            };

            for (String[] button : buttons) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    bomMenuId, button[0], 3, button[1], Integer.parseInt(button[2]), 1, 1
                );
            }

            // 给超级管理员分配菜单权限
            jdbcTemplate.update(
                "INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (?, ?)",
                1, bomMenuId
            );

            log.info("BOM管理菜单插入成功");
            
        } catch (Exception e) {
            log.error("插入BOM管理菜单失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建BOM版本表
     */
    private void checkAndCreateBomVersionTable() {
        try {
            // 检查表是否存在
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_bom_version'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("BOM版本表 erp_bom_version 已存在");
                
                // 检查表中是否有数据
                Long dataCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_bom_version",
                    Long.class
                );
                
                if (dataCount != null && dataCount == 0) {
                    log.info("BOM版本表为空，插入示例数据...");
                    insertSampleBomVersionData();
                }
                return;
            }

            log.info("BOM版本表 erp_bom_version 不存在，创建表...");
            
            // 创建表
            String createTableSql = """
                CREATE TABLE `erp_bom_version` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `bom_id` BIGINT NOT NULL COMMENT 'BOM ID（对应erp_bom的ID）',
                    `version` VARCHAR(20) NOT NULL COMMENT '版本号',
                    `ecn_number` VARCHAR(50) NOT NULL COMMENT 'ECN编号',
                    `change_reason` VARCHAR(255) DEFAULT NULL COMMENT '变更原因',
                    `change_content` TEXT DEFAULT NULL COMMENT '变更内容',
                    `effective_time` DATETIME DEFAULT NULL COMMENT '生效时间',
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
                    UNIQUE KEY `uk_bom_version` (`bom_id`, `version`),
                    KEY `idx_ecn_number` (`ecn_number`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='BOM版本表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("BOM版本表创建成功");
            
            // 插入示例数据
            insertSampleBomVersionData();
            
        } catch (Exception e) {
            log.error("创建BOM版本表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例BOM版本数据
     */
    private void insertSampleBomVersionData() {
        try {
            // 先查询BOM的ID
            List<Map<String, Object>> boms = jdbcTemplate.queryForList(
                "SELECT id FROM erp_bom LIMIT 2"
            );
            
            if (boms.size() >= 2) {
                Long bom1Id = ((Number) boms.get(0).get("id")).longValue();
                Long bom2Id = ((Number) boms.get(1).get("id")).longValue();
                
                String insertSql = """
                    INSERT INTO `erp_bom_version` (`bom_id`, `version`, `ecn_number`, `change_reason`, `change_content`, `effective_time`, `status`) VALUES
                    (?, 'V1.0', 'ECN2024001', '初始版本', '创建BOM初始版本', NOW(), 1),
                    (?, 'V1.1', 'ECN2024002', '调整用量', '调整子件用量比例', NOW(), 1),
                    (?, 'V1.0', 'ECN2024003', '初始版本', '创建BOM初始版本', NOW(), 1)
                    """;
                
                jdbcTemplate.update(insertSql, 
                    bom1Id, 
                    bom1Id, 
                    bom2Id
                );
                
                log.info("示例BOM版本数据插入成功");
            }
            
        } catch (Exception e) {
            log.error("插入示例BOM版本数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建替代料表
     */
    private void checkAndCreateAlternativeMaterialTable() {
        try {
            // 检查表是否存在
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_alternative_material'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("替代料表 erp_alternative_material 已存在");
                
                // 检查表中是否有数据
                Long dataCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_alternative_material",
                    Long.class
                );
                
                if (dataCount != null && dataCount == 0) {
                    log.info("替代料表为空，插入示例数据...");
                    insertSampleAlternativeMaterialData();
                }
                return;
            }

            log.info("替代料表 erp_alternative_material 不存在，创建表...");
            
            // 创建表
            String createTableSql = """
                CREATE TABLE `erp_alternative_material` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `main_material_id` BIGINT NOT NULL COMMENT '主料ID（对应erp_product或erp_material的ID）',
                    `main_material_type` TINYINT NOT NULL COMMENT '主料类型：1产品 2物料',
                    `alternative_material_id` BIGINT NOT NULL COMMENT '替代料ID（对应erp_product或erp_material的ID）',
                    `alternative_material_type` TINYINT NOT NULL COMMENT '替代料类型：1产品 2物料',
                    `alternative_ratio` DECIMAL(10,3) NOT NULL COMMENT '替代比例',
                    `priority` TINYINT NOT NULL COMMENT '优先级：1-5（1最高）',
                    `applicable_scene` VARCHAR(255) DEFAULT NULL COMMENT '适用场景',
                    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
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
                    UNIQUE KEY `uk_alternative_relation` (`main_material_id`, `main_material_type`, `alternative_material_id`, `alternative_material_type`),
                    KEY `idx_main_material` (`main_material_id`, `main_material_type`),
                    KEY `idx_alternative_material` (`alternative_material_id`, `alternative_material_type`),
                    KEY `idx_priority` (`priority`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='替代料表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("替代料表创建成功");
            
            // 插入示例数据
            insertSampleAlternativeMaterialData();
            
        } catch (Exception e) {
            log.error("创建替代料表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例替代料数据
     */
    private void insertSampleAlternativeMaterialData() {
        try {
            // 先查询物料的ID
            List<Map<String, Object>> materials = jdbcTemplate.queryForList(
                "SELECT id, material_code FROM erp_material LIMIT 4"
            );
            
            if (materials.size() >= 4) {
                Long material1Id = ((Number) materials.get(0).get("id")).longValue();
                Long material2Id = ((Number) materials.get(1).get("id")).longValue();
                Long material3Id = ((Number) materials.get(2).get("id")).longValue();
                Long material4Id = ((Number) materials.get(3).get("id")).longValue();
                
                String insertSql = """
                    INSERT INTO `erp_alternative_material` (`main_material_id`, `main_material_type`, `alternative_material_id`, `alternative_material_type`, `alternative_ratio`, `priority`, `applicable_scene`, `remark`, `status`) VALUES
                    (?, 2, ?, 2, 1.0, 1, '常规生产', '首选替代料', 1),
                    (?, 2, ?, 2, 1.2, 2, '紧急生产', '次选替代料', 1),
                    (?, 2, ?, 2, 1.0, 1, '常规生产', '首选替代料', 1)
                    """;
                
                jdbcTemplate.update(insertSql, 
                    material1Id, material2Id,
                    material1Id, material3Id,
                    material2Id, material4Id
                );
                
                log.info("示例替代料数据插入成功");
            }
            
        } catch (Exception e) {
            log.error("插入示例替代料数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并插入BOM版本与替代料菜单
     * 注意：这个方法会强制重新插入菜单，确保配置正确
     */
    private void checkAndInsertBomVersionMenu() {
        try {
            // 查找产品与BOM管理菜单ID
            List<Map<String, Object>> productBomMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '产品与BOM管理' LIMIT 1"
            );
            
            Long productBomMenuId = 0L;
            if (!productBomMenus.isEmpty()) {
                productBomMenuId = ((Number) productBomMenus.get(0).get("id")).longValue();
            }

            // 先删除已有的BOM版本与替代料相关菜单（强制重新初始化）
            log.info("准备初始化BOM版本与替代料菜单...");
            
            // 查找并删除已有的相关菜单
            List<Map<String, Object>> existingMenus = jdbcTemplate.queryForList(
                "SELECT id, menu_name, parent_id FROM sys_menu WHERE menu_name IN ('BOM版本与替代料', 'BOM版本管理', '替代料管理')"
            );
            
            if (!existingMenus.isEmpty()) {
                log.info("发现已存在的BOM版本与替代料菜单，准备重新初始化...");
                
                // 先删除按钮权限
                for (Map<String, Object> menu : existingMenus) {
                    Long menuId = ((Number) menu.get("id")).longValue();
                    jdbcTemplate.update(
                        "DELETE FROM sys_menu WHERE parent_id = ?",
                        menuId
                    );
                    // 从角色菜单关联表中删除
                    jdbcTemplate.update(
                        "DELETE FROM sys_role_menu WHERE menu_id = ?",
                        menuId
                    );
                }
                
                // 删除菜单本身
                for (Map<String, Object> menu : existingMenus) {
                    Long menuId = ((Number) menu.get("id")).longValue();
                    jdbcTemplate.update(
                        "DELETE FROM sys_menu WHERE id = ?",
                        menuId
                    );
                }
                
                log.info("已清除旧的BOM版本与替代料菜单配置");
            }

            log.info("开始插入BOM版本与替代料菜单数据...");

            // 插入BOM版本与替代料菜单（父菜单）
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                productBomMenuId, "BOM版本与替代料", 1, "erp:bom-version:list", "bom-version", "", "DataLine", 2, 1, 1, 1
            );

            Long bomVersionMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            log.info("已插入BOM版本与替代料父菜单，ID: {}", bomVersionMenuId);

            // 插入BOM版本管理菜单
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                bomVersionMenuId, "BOM版本管理", 2, "erp:bom-version:list", "bom-versions", "erp/BomVersion", "Version", 1, 1, 1, 1
            );

            Long bomVersionSubMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            log.info("已插入BOM版本管理菜单，ID: {}", bomVersionSubMenuId);

            // 插入BOM版本管理按钮权限
            String[][] bomVersionButtons = {
                {"新增版本", "erp:bom-version:add", "1"},
                {"编辑版本", "erp:bom-version:edit", "2"},
                {"删除版本", "erp:bom-version:delete", "3"}
            };

            for (String[] button : bomVersionButtons) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    bomVersionSubMenuId, button[0], 3, button[1], Integer.parseInt(button[2]), 1, 1
                );
            }

            // 插入替代料管理菜单
            jdbcTemplate.update(
                "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                bomVersionMenuId, "替代料管理", 2, "erp:alternative-material:list", "alternative-materials", "erp/AlternativeMaterial", "Switch", 2, 1, 1, 1
            );

            Long alternativeMaterialSubMenuId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

            log.info("已插入替代料管理菜单，ID: {}", alternativeMaterialSubMenuId);

            // 插入替代料管理按钮权限
            String[][] alternativeMaterialButtons = {
                {"新增替代料", "erp:alternative-material:add", "1"},
                {"编辑替代料", "erp:alternative-material:edit", "2"},
                {"删除替代料", "erp:alternative-material:delete", "3"}
            };

            for (String[] button : alternativeMaterialButtons) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    alternativeMaterialSubMenuId, button[0], 3, button[1], Integer.parseInt(button[2]), 1, 1
                );
            }

            // 给超级管理员分配菜单权限
            jdbcTemplate.update(
                "INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (?, ?), (?, ?), (?, ?)",
                1, bomVersionMenuId,
                1, bomVersionSubMenuId,
                1, alternativeMaterialSubMenuId
            );

            log.info("========================================");
            log.info("   BOM版本与替代料菜单初始化完成！");
            log.info("   - BOM版本与替代料菜单ID: {}", bomVersionMenuId);
            log.info("   - BOM版本管理菜单ID: {}", bomVersionSubMenuId);
            log.info("   - 替代料管理菜单ID: {}", alternativeMaterialSubMenuId);
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("初始化BOM版本与替代料菜单失败: {}", e.getMessage(), e);
        }
    }
}
