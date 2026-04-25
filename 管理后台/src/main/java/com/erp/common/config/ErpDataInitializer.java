package com.erp.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            
            // 6. 检查并创建销售订单表
            checkAndCreateSalesOrderTable();
            
            // 7. 检查并创建销售订单明细表
            checkAndCreateSalesOrderItemTable();
            
            // 8. 检查并创建预测单表
            checkAndCreateForecastOrderTable();
            
            // 9. 检查并创建预测单明细表
            checkAndCreateForecastOrderItemTable();
            
            // 10. 检查并创建库存表
            checkAndCreateInventoryTable();
            
            // 11. 检查并创建需求来源表
            checkAndCreateDemandSourceTable();
            
            // 12. 检查并创建净需求表
            checkAndCreateNetRequirementTable();
            
            // 13. 检查并创建设备表
            checkAndCreateEquipmentTable();
            
            // 14. 检查并创建班组表
            checkAndCreateWorkGroupTable();
            
            // 15. 检查并创建人员排班表
            checkAndCreateEmployeeScheduleTable();
            
            // 16. 检查并创建主生产计划表
            checkAndCreateMpsTable();
            
            // 17. 检查并创建齐套预警表
            checkAndCreateKittingAlertTable();
            
            // 18. 检查并插入菜单数据
            checkAndInsertMenuData();
            
            // 19. 检查并插入产能平衡和齐套预警菜单数据
            checkAndInsertCapacityAndKittingMenuData();
            
            // 20. 检查并插入可视化排程和甘特图菜单数据
            checkAndInsertGanttMenuData();
            
            // 21. 检查并修复is_deleted字段（如果存在数据但is_deleted为NULL或1）
            fixIsDeletedField();
            
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
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_product'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("产品表 erp_product 已存在");
                
                Long dataCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_product",
                    Long.class
                );
                
                if (dataCount != null && dataCount < 30) {
                    log.info("产品表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleProductData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("产品表为空，插入示例数据...");
                    insertSampleProductData();
                }
                return;
            }

            log.info("产品表 erp_product 不存在，创建表...");
            
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
            
            insertSampleProductData();
            
        } catch (Exception e) {
            log.error("创建产品表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例产品数据（35条）
     */
    private void insertSampleProductData() {
        try {
            String insertSql = """
                INSERT IGNORE INTO `erp_product` (`product_code`, `product_name`, `specification`, `unit`, `category`, `brand`, `technical_params`, `description`, `status`) VALUES
                ('PRD001', '台式计算机', 'ThinkCentre M90a', '台', '计算机设备', '联想', '{"cpu":"Intel i5-10400","memory":"16GB","storage":"512GB SSD","monitor":"23.8英寸"}', '联想台式计算机，商务办公用', 1),
                ('PRD002', '笔记本电脑', 'ThinkPad X1 Carbon', '台', '计算机设备', '联想', '{"cpu":"Intel i7-1165G7","memory":"16GB","storage":"1TB SSD","screen":"14英寸"}', '联想轻薄商务笔记本', 1),
                ('PRD003', '激光打印机', 'HP LaserJet Pro M404dn', '台', '办公设备', '惠普', '{"print_speed":"40ppm","resolution":"1200x1200dpi","connectivity":"USB, Ethernet"}', '惠普黑白激光打印机', 1),
                ('PRD004', '投影仪', 'Epson CB-X50', '台', '办公设备', '爱普生', '{"brightness":"3600流明","resolution":"1024x768","contrast":"16000:1"}', '爱普生商务投影仪', 1),
                ('PRD005', '网络交换机', 'Cisco Catalyst 2960', '台', '网络设备', '思科', '{"ports":"24口","speed":"10/100/1000Mbps","type":"千兆交换机"}', '思科千兆网络交换机', 1),
                ('PRD006', '服务器', 'Dell PowerEdge R750', '台', '服务器设备', '戴尔', '{"cpu":"Intel Xeon Silver 4310","memory":"32GB","storage":"2TB SAS RAID","network":"4x1Gbps"}', '戴尔机架式服务器', 1),
                ('PRD007', '显示器', 'Dell U2723QE', '台', '计算机设备', '戴尔', '{"size":"27英寸","resolution":"4K","type":"IPS","refresh":"60Hz"}', '戴尔4K高清显示器', 1),
                ('PRD008', '键盘鼠标套装', 'Logitech MK270', '套', '计算机配件', '罗技', '{"type":"无线","interface":"2.4G","battery":"AAA"}', '罗技无线键鼠套装', 1),
                ('PRD009', '路由器', 'Huawei AR6120', '台', '网络设备', '华为', '{"ports":"8GE+2SFP","type":"企业级","bandwidth":"1Gbps"}', '华为企业级路由器', 1),
                ('PRD010', 'UPS电源', 'APC SUA1000ICH', '台', '电源设备', 'APC', '{"capacity":"1000VA","runtime":"5-10min","type":"在线互动式"}', 'APC不间断电源', 1),
                ('PRD011', '多功能一体机', 'HP OfficeJet Pro 9010', '台', '办公设备', '惠普', '{"functions":"打印/复印/扫描/传真","type":"彩色喷墨","connectivity":"WiFi/USB"}', '惠普彩色喷墨一体机', 1),
                ('PRD012', '复印机', 'Canon iR-ADV C3720', '台', '办公设备', '佳能', '{"type":"彩色数码","speed":"20ppm","functions":"复印/打印/扫描"}', '佳能彩色数码复合机', 1),
                ('PRD013', '碎纸机', 'Fellowes 99Ci', '台', '办公设备', '范罗士', '{"capacity":"17升","shred_type":"段状","security":"P4级"}', '范罗士商用碎纸机', 1),
                ('PRD014', '装订机', '得力 3880', '台', '办公设备', '得力', '{"type":"财务装订机","capacity":"50mm","binding":"热熔铆管"}', '得力财务装订机', 1),
                ('PRD015', '扫描仪', 'Epson DS-570W', '台', '办公设备', '爱普生', '{"type":"馈纸式","speed":"35ppm","resolution":"600dpi"}', '爱普生高速文档扫描仪', 1),
                ('PRD016', '工业平板电脑', '研华 PPC-3150', '台', '工控设备', '研华', '{"size":"15英寸","cpu":"Intel Celeron J1900","memory":"4GB","os":"Windows 7"}', '研华工业触摸平板电脑', 1),
                ('PRD017', 'PLC控制器', 'Siemens S7-1200', '台', '工控设备', '西门子', '{"model":"6ES7 214-1AG40-0XB0","io":"14DI/10DO","communication":"PROFINET"}', '西门子S7-1200 PLC控制器', 1),
                ('PRD018', '触摸屏', '威纶通 MT8102iP', '台', '工控设备', '威纶通', '{"size":"10.1英寸","resolution":"1024x600","communication":"Ethernet/RS485"}', '威纶通工业触摸屏', 1),
                ('PRD019', '伺服驱动器', '松下 MINAS A6', '台', '工控设备', '松下', '{"power":"1kW","voltage":"200V","communication":"MODBUS-RTU"}', '松下A6系列伺服驱动器', 1),
                ('PRD020', '变频器', '三菱 FR-E740', '台', '工控设备', '三菱', '{"power":"3.7kW","voltage":"380V","type":"矢量控制"}', '三菱E700系列变频器', 1),
                ('PRD021', '稳压电源', '鸿宝 SVC-5KVA', '台', '电源设备', '鸿宝', '{"capacity":"5KVA","voltage":"220V","type":"全自动交流稳压"}', '鸿宝全自动交流稳压器', 1),
                ('PRD022', '直流电源', '固纬 GPS-3303C', '台', '电源设备', '固纬', '{"voltage":"0-30V","current":"0-3A","channels":"3通道"}', '固纬线性直流电源供应器', 1),
                ('PRD023', '网络机柜', '图腾 G26642', '台', '网络设备', '图腾', '{"height":"42U","width":"600mm","depth":"600mm","material":"冷轧钢"}', '图腾标准网络机柜', 1),
                ('PRD024', '配线架', 'AMP 24口配线架', '个', '网络设备', '安普', '{"ports":"24口","type":"超五类","mounting":"19英寸机架式"}', '安普24口网络配线架', 1),
                ('PRD025', '光纤模块', 'Cisco SFP-GE-L', '个', '网络设备', '思科', '{"type":"SFP","speed":"1Gbps","distance":"10km","interface":"LC"}', '思科千兆单模光纤模块', 1),
                ('PRD026', '无线AP', 'Cisco Catalyst 9120AX', '台', '网络设备', '思科', '{"standard":"WiFi 6","band":"2.4G/5G双频","speed":"5.4Gbps"}', '思科WiFi 6无线接入点', 1),
                ('PRD027', '防火墙', 'Fortinet FortiGate 60E', '台', '网络设备', '飞塔', '{"ports":"10GE","throughput":"3Gbps","type":"下一代防火墙"}', '飞塔企业级防火墙', 1),
                ('PRD028', 'NAS存储', 'Synology DS1621+', '台', '存储设备', '群晖', '{"bay":"6盘位","cpu":"AMD Ryzen V1500B","memory":"4GB"}', '群晖6盘位网络存储服务器', 1),
                ('PRD029', '固态硬盘', 'Samsung 980 Pro', '个', '存储设备', '三星', '{"capacity":"1TB","interface":"NVMe M.2","speed":"7000MB/s"}', '三星NVMe固态硬盘', 1),
                ('PRD030', '机械硬盘', 'WD Red Pro', '个', '存储设备', '西部数据', '{"capacity":"8TB","interface":"SATA 3","rpm":"7200rpm"}', '西数红盘企业级硬盘', 1),
                ('PRD031', 'U盘', 'Kingston DTKN', '个', '存储设备', '金士顿', '{"capacity":"64GB","interface":"USB 3.2","type":"金属外壳"}', '金士顿USB3.2高速U盘', 1),
                ('PRD032', '移动硬盘', 'Seagate One Touch', '个', '存储设备', '希捷', '{"capacity":"2TB","interface":"USB 3.0","type":"2.5英寸"}', '希捷睿品移动硬盘', 1),
                ('PRD033', '显示器支架', '乐歌 D7H', '个', '计算机配件', '乐歌', '{"type":"双屏支架","load":"2-9kg","adjustment":"升降旋转"}', '乐歌双屏显示器支架', 1),
                ('PRD034', '网线钳', '三堡 HT-568R', '把', '网络工具', '三堡', '{"type":"多功能","functions":"压线/剥线/剪线","connectors":"RJ45/RJ11"}', '三堡多功能网线钳', 1),
                ('PRD035', '测线仪', '优利德 UT681', '个', '网络工具', '优利德', '{"type":"智能线缆测试仪","test":"网线/电话线/同轴","display":"LED指示"}', '优利德智能线缆测试仪', 1)
                """;
            
            jdbcTemplate.execute(insertSql);
            log.info("示例产品数据插入成功（35条）");
            
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
            
            // 查找并删除已有的相关菜单（按菜单名称）
            List<Map<String, Object>> existingMenus = jdbcTemplate.queryForList(
                "SELECT id, menu_name, parent_id FROM sys_menu WHERE menu_name IN ('BOM版本与替代料', 'BOM版本管理', '替代料管理')"
            );
            
            // 查找并删除已有的相关菜单（按permission_key）
            List<Map<String, Object>> existingMenusByPermission = jdbcTemplate.queryForList(
                "SELECT id, menu_name, parent_id FROM sys_menu WHERE permission_key IN ('erp:bom-version:list', 'erp:bom-version:add', 'erp:bom-version:edit', 'erp:bom-version:delete', 'erp:alternative-material:list', 'erp:alternative-material:add', 'erp:alternative-material:edit', 'erp:alternative-material:delete')"
            );
            
            // 合并两个列表，去重
            Set<Long> menuIdsToDelete = new HashSet<>();
            for (Map<String, Object> menu : existingMenus) {
                menuIdsToDelete.add(((Number) menu.get("id")).longValue());
            }
            for (Map<String, Object> menu : existingMenusByPermission) {
                menuIdsToDelete.add(((Number) menu.get("id")).longValue());
            }
            
            if (!menuIdsToDelete.isEmpty()) {
                log.info("发现已存在的BOM版本与替代料相关菜单（共{}个），准备重新初始化...", menuIdsToDelete.size());
                
                // 先删除按钮权限（子菜单）
                for (Long menuId : menuIdsToDelete) {
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
                for (Long menuId : menuIdsToDelete) {
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
                productBomMenuId, "BOM版本与替代料", 1, null, "bom-version", "", "DataLine", 2, 1, 1, 1
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

    /**
     * 检查并创建销售订单表
     */
    private void checkAndCreateSalesOrderTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_sales_order'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("销售订单表 erp_sales_order 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_sales_order", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("销售订单表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleSalesOrderData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("销售订单表为空，插入示例数据...");
                    insertSampleSalesOrderData();
                }
                return;
            }

            log.info("销售订单表 erp_sales_order 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_sales_order` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
                    `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户名称',
                    `customer_code` VARCHAR(50) DEFAULT NULL COMMENT '客户编码',
                    `order_type` TINYINT NOT NULL DEFAULT 1 COMMENT '订单类型：1普通订单 2紧急订单',
                    `order_date` DATE DEFAULT NULL COMMENT '订单日期',
                    `delivery_date` DATE DEFAULT NULL COMMENT '交货日期',
                    `total_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '订单总金额',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1待确认 2已确认 3已发货 4已完成 5已取消',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_order_no` (`order_no`),
                    KEY `idx_customer_name` (`customer_name`),
                    KEY `idx_order_date` (`order_date`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单主表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("销售订单表创建成功");
            
            insertSampleSalesOrderData();
            
        } catch (Exception e) {
            log.error("创建销售订单表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例销售订单数据（35条）
     */
    private void insertSampleSalesOrderData() {
        try {
            String[][] customers = {
                {"ABC科技有限公司", "CUST001"}, {"XYZ电子设备公司", "CUST002"}, {"环球贸易集团", "CUST003"},
                {"创新科技发展公司", "CUST004"}, {"东方电子有限公司", "CUST005"}, {"南方通信设备公司", "CUST006"},
                {"北方工业集团", "CUST007"}, {"西部数据科技", "CUST008"}, {"中原计算机系统公司", "CUST009"},
                {"华东网络技术公司", "CUST010"}, {"华南信息科技", "CUST011"}, {"华北自动化设备", "CUST012"},
                {"华西软件服务", "CUST013"}, {"华中电子工程", "CUST014"}, {"蓝天智能制造", "CUST015"},
                {"绿地数字化科技", "CUST016"}, {"白云信息系统", "CUST017"}, {"红日自动化", "CUST018"},
                {"蓝海科技发展", "CUST019"}, {"金茂电子技术", "CUST020"}, {"银星通信设备", "CUST021"},
                {"铜鼎工业自动化", "CUST022"}, {"铁流智能制造", "CUST023"}, {"玉峰信息科技", "CUST024"},
                {"石城网络技术", "CUST025"}, {"山城电子设备", "CUST026"}, {"水城通信系统", "CUST027"},
                {"火城自动化工程", "CUST028"}, {"风城信息技术", "CUST029"}, {"雷城电子科技", "CUST030"},
                {"闪电智能制造", "CUST031"}, {"星云数据服务", "CUST032"}, {"银河系统集成", "CUST033"},
                {"宇宙科技发展", "CUST034"}, {"地球电子工程", "CUST035"}
            };
            
            String[] orderDates = {
                "2026-03-01", "2026-03-05", "2026-03-10", "2026-03-15", "2026-03-20",
                "2026-03-25", "2026-03-30", "2026-04-01", "2026-04-03", "2026-04-05",
                "2026-04-07", "2026-04-08", "2026-04-09", "2026-04-10", "2026-04-11",
                "2026-04-12", "2026-04-13", "2026-04-14", "2026-04-15", "2026-04-16",
                "2026-04-17", "2026-04-18", "2026-04-19", "2026-04-20", "2026-04-21",
                "2026-04-22", "2026-04-23", "2026-04-24", "2026-04-25", "2026-04-26",
                "2026-04-27", "2026-04-28", "2026-04-29", "2026-04-30"
            };
            
            String[] deliveryDates = {
                "2026-03-15", "2026-03-20", "2026-03-25", "2026-03-30", "2026-04-05",
                "2026-04-10", "2026-04-15", "2026-04-15", "2026-04-18", "2026-04-20",
                "2026-04-22", "2026-04-23", "2026-04-24", "2026-04-25", "2026-04-26",
                "2026-04-27", "2026-04-28", "2026-04-29", "2026-04-30", "2026-05-01",
                "2026-05-02", "2026-05-03", "2026-05-04", "2026-05-05", "2026-05-06",
                "2026-05-07", "2026-05-08", "2026-05-09", "2026-05-10", "2026-05-11",
                "2026-05-12", "2026-05-13", "2026-05-14", "2026-05-15"
            };
            
            int[] statuses = {1, 2, 2, 3, 4, 1, 2, 2, 3, 4, 1, 2, 2, 3, 4, 1, 2, 2, 3, 4,
                               1, 2, 2, 3, 4, 1, 2, 2, 3, 4, 1, 2, 2, 3, 4};
            
            int[] orderTypes = {1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2,
                                 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2};
            
            for (int i = 0; i < 35; i++) {
                String orderNo = String.format("SO2026%06d", (i + 1));
                String customerName = customers[i % customers.length][0];
                String customerCode = customers[i % customers.length][1];
                int orderType = orderTypes[i % orderTypes.length];
                String orderDate = orderDates[i % orderDates.length];
                String deliveryDate = deliveryDates[i % deliveryDates.length];
                java.math.BigDecimal totalAmount = new java.math.BigDecimal(50000 + (i * 15000));
                String remark = String.format("%s采购订单，产品编号PRD%03d，数量%d台", 
                    customerName, (i % 35) + 1, 10 + (i % 10) * 5);
                int status = statuses[i % statuses.length];
                
                jdbcTemplate.update(
                    "INSERT IGNORE INTO `erp_sales_order` (`order_no`, `customer_name`, `customer_code`, `order_type`, `order_date`, `delivery_date`, `total_amount`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    orderNo, customerName, customerCode, orderType, orderDate, deliveryDate, totalAmount, remark, status
                );
            }
            
            log.info("示例销售订单数据插入成功（35条）");
            
        } catch (Exception e) {
            log.error("插入示例销售订单数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建销售订单明细表
     */
    private void checkAndCreateSalesOrderItemTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_sales_order_item'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("销售订单明细表 erp_sales_order_item 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_sales_order_item", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("销售订单明细表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleSalesOrderItemData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("销售订单明细表为空，插入示例数据...");
                    insertSampleSalesOrderItemData();
                }
                return;
            }

            log.info("销售订单明细表 erp_sales_order_item 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_sales_order_item` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `order_id` BIGINT NOT NULL COMMENT '订单ID',
                    `order_no` VARCHAR(50) DEFAULT NULL COMMENT '订单编号',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `quantity` DECIMAL(10,3) NOT NULL COMMENT '数量',
                    `unit_price` DECIMAL(12,2) DEFAULT 0.00 COMMENT '单价',
                    `amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '金额',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    KEY `idx_order_id` (`order_id`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单明细表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("销售订单明细表创建成功");
            
            insertSampleSalesOrderItemData();
            
        } catch (Exception e) {
            log.error("创建销售订单明细表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例销售订单明细数据（每订单2-4个产品，共约90条）
     */
    private void insertSampleSalesOrderItemData() {
        try {
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(
                "SELECT id, order_no FROM erp_sales_order ORDER BY id"
            );
            List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT id, product_code, product_name FROM erp_product ORDER BY id"
            );
            
            if (!orders.isEmpty() && !products.isEmpty()) {
                int itemCount = 0;
                for (int i = 0; i < orders.size(); i++) {
                    Map<String, Object> order = orders.get(i);
                    Long orderId = ((Number) order.get("id")).longValue();
                    String orderNo = (String) order.get("order_no");
                    
                    int itemsPerOrder = 2 + (i % 3);
                    for (int j = 0; j < itemsPerOrder; j++) {
                        int productIndex = (i * 3 + j) % products.size();
                        Map<String, Object> product = products.get(productIndex);
                        
                        Long productId = ((Number) product.get("id")).longValue();
                        String productCode = (String) product.get("product_code");
                        String productName = (String) product.get("product_name");
                        
                        int quantity = 10 + (i % 5) * 10 + j * 5;
                        int unitPrice = 2000 + (productIndex % 10) * 500;
                        int amount = quantity * unitPrice;
                        
                        jdbcTemplate.update(
                            "INSERT INTO `erp_sales_order_item` (`order_id`, `order_no`, `product_id`, `product_code`, `product_name`, `specification`, `unit`, `quantity`, `unit_price`, `amount`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            orderId, orderNo, productId, productCode, productName, "规格型号" + (productIndex + 1), "台", 
                            new java.math.BigDecimal(quantity), 
                            new java.math.BigDecimal(unitPrice), 
                            new java.math.BigDecimal(amount), 
                            1
                        );
                        itemCount++;
                    }
                }
                log.info("示例销售订单明细数据插入成功（共{}条）", itemCount);
            }
            
        } catch (Exception e) {
            log.error("插入示例销售订单明细数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建预测单表
     */
    private void checkAndCreateForecastOrderTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_forecast_order'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("预测单表 erp_forecast_order 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_forecast_order", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("预测单表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleForecastOrderData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("预测单表为空，插入示例数据...");
                    insertSampleForecastOrderData();
                }
                return;
            }

            log.info("预测单表 erp_forecast_order 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_forecast_order` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `forecast_no` VARCHAR(50) NOT NULL COMMENT '预测单编号',
                    `forecast_name` VARCHAR(100) DEFAULT NULL COMMENT '预测单名称',
                    `forecast_type` TINYINT NOT NULL DEFAULT 1 COMMENT '预测类型：1月度预测 2季度预测 3年度预测',
                    `start_date` DATE DEFAULT NULL COMMENT '开始日期',
                    `end_date` DATE DEFAULT NULL COMMENT '结束日期',
                    `total_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总预测数量',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1草稿 2已确认 3已完成 4已取消',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_forecast_no` (`forecast_no`),
                    KEY `idx_start_date` (`start_date`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预测单主表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("预测单表创建成功");
            
            insertSampleForecastOrderData();
            
        } catch (Exception e) {
            log.error("创建预测单表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例预测单数据（35条）
     */
    private void insertSampleForecastOrderData() {
        try {
            String[][] forecastNames = {
                {"Q1季度产品需求预测", "2026-01-01", "2026-03-31"},
                {"Q2季度产品需求预测", "2026-04-01", "2026-06-30"},
                {"Q3季度产品需求预测", "2026-07-01", "2026-09-30"},
                {"Q4季度产品需求预测", "2026-10-01", "2026-12-31"},
                {"1月月度预测", "2026-01-01", "2026-01-31"},
                {"2月月度预测", "2026-02-01", "2026-02-28"},
                {"3月月度预测", "2026-03-01", "2026-03-31"},
                {"4月月度预测", "2026-04-01", "2026-04-30"},
                {"5月月度预测", "2026-05-01", "2026-05-31"},
                {"6月月度预测", "2026-06-01", "2026-06-30"},
                {"7月月度预测", "2026-07-01", "2026-07-31"},
                {"8月月度预测", "2026-08-01", "2026-08-31"},
                {"9月月度预测", "2026-09-01", "2026-09-30"},
                {"10月月度预测", "2026-10-01", "2026-10-31"},
                {"11月月度预测", "2026-11-01", "2026-11-30"},
                {"12月月度预测", "2026-12-01", "2026-12-31"},
                {"2026年度产品预测", "2026-01-01", "2026-12-31"},
                {"上半年预测", "2026-01-01", "2026-06-30"},
                {"下半年预测", "2026-07-01", "2026-12-31"},
                {"春节旺季预测", "2026-01-20", "2026-02-20"},
                {"五一黄金周预测", "2026-04-25", "2026-05-10"},
                {"618电商大促预测", "2026-06-01", "2026-06-30"},
                {"暑假促销预测", "2026-07-01", "2026-08-31"},
                {"十一黄金周预测", "2026-09-25", "2026-10-10"},
                {"双11大促预测", "2026-10-20", "2026-11-20"},
                {"双12促销预测", "2026-12-01", "2026-12-15"},
                {"年终备货预测", "2026-11-15", "2026-12-31"},
                {"计算机设备专项预测", "2026-01-01", "2026-12-31"},
                {"办公设备专项预测", "2026-01-01", "2026-12-31"},
                {"网络设备专项预测", "2026-01-01", "2026-12-31"},
                {"工控设备专项预测", "2026-01-01", "2026-12-31"},
                {"存储设备专项预测", "2026-01-01", "2026-12-31"},
                {"服务器设备专项预测", "2026-01-01", "2026-12-31"},
                {"电源设备专项预测", "2026-01-01", "2026-12-31"},
                {"配件及工具专项预测", "2026-01-01", "2026-12-31"}
            };
            
            int[] forecastTypes = {2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                   3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3};
            
            int[] statuses = {2, 2, 1, 1, 4, 4, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1,
                               1, 2, 1, 4, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
            
            for (int i = 0; i < 35; i++) {
                String forecastNo = String.format("FC2026%05d", (i + 1));
                String forecastName = forecastNames[i][0];
                int forecastType = forecastTypes[i];
                String startDate = forecastNames[i][1];
                String endDate = forecastNames[i][2];
                java.math.BigDecimal totalQuantity = new java.math.BigDecimal(500 + (i * 200));
                String remark = String.format("%s，基于历史数据分析及市场趋势预测，预计需求数量约%d台", 
                    forecastName, 500 + (i * 200));
                int status = statuses[i];
                
                jdbcTemplate.update(
                    "INSERT IGNORE INTO `erp_forecast_order` (`forecast_no`, `forecast_name`, `forecast_type`, `start_date`, `end_date`, `total_quantity`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    forecastNo, forecastName, forecastType, startDate, endDate, totalQuantity, remark, status
                );
            }
            
            log.info("示例预测单数据插入成功（35条）");
            
        } catch (Exception e) {
            log.error("插入示例预测单数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建预测单明细表
     */
    private void checkAndCreateForecastOrderItemTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_forecast_order_item'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("预测单明细表 erp_forecast_order_item 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_forecast_order_item", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("预测单明细表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleForecastOrderItemData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("预测单明细表为空，插入示例数据...");
                    insertSampleForecastOrderItemData();
                }
                return;
            }

            log.info("预测单明细表 erp_forecast_order_item 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_forecast_order_item` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `forecast_id` BIGINT NOT NULL COMMENT '预测单ID',
                    `forecast_no` VARCHAR(50) DEFAULT NULL COMMENT '预测单编号',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `forecast_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '预测数量',
                    `actual_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '实际数量',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    KEY `idx_forecast_id` (`forecast_id`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预测单明细表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("预测单明细表创建成功");
            
            insertSampleForecastOrderItemData();
            
        } catch (Exception e) {
            log.error("创建预测单明细表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例预测单明细数据（每预测单2-4个产品，共约90条）
     */
    private void insertSampleForecastOrderItemData() {
        try {
            List<Map<String, Object>> forecasts = jdbcTemplate.queryForList(
                "SELECT id, forecast_no FROM erp_forecast_order ORDER BY id"
            );
            List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT id, product_code, product_name FROM erp_product ORDER BY id"
            );
            
            if (!forecasts.isEmpty() && !products.isEmpty()) {
                int itemCount = 0;
                for (int i = 0; i < forecasts.size(); i++) {
                    Map<String, Object> forecast = forecasts.get(i);
                    Long forecastId = ((Number) forecast.get("id")).longValue();
                    String forecastNo = (String) forecast.get("forecast_no");
                    
                    int itemsPerForecast = 2 + (i % 3);
                    for (int j = 0; j < itemsPerForecast; j++) {
                        int productIndex = (i * 3 + j) % products.size();
                        Map<String, Object> product = products.get(productIndex);
                        
                        Long productId = ((Number) product.get("id")).longValue();
                        String productCode = (String) product.get("product_code");
                        String productName = (String) product.get("product_name");
                        
                        int forecastQty = 100 + (i % 10) * 50 + j * 20;
                        
                        jdbcTemplate.update(
                            "INSERT INTO `erp_forecast_order_item` (`forecast_id`, `forecast_no`, `product_id`, `product_code`, `product_name`, `specification`, `unit`, `forecast_quantity`, `actual_quantity`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            forecastId, forecastNo, productId, productCode, productName, "规格型号" + (productIndex + 1), "台", 
                            new java.math.BigDecimal(forecastQty), 
                            new java.math.BigDecimal(0), 
                            "预测需求，来源于市场数据分析", 
                            1
                        );
                        itemCount++;
                    }
                }
                log.info("示例预测单明细数据插入成功（共{}条）", itemCount);
            }
            
        } catch (Exception e) {
            log.error("插入示例预测单明细数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建库存表
     */
    private void checkAndCreateInventoryTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_inventory'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("库存表 erp_inventory 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_inventory", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("库存表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleInventoryData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("库存表为空，插入示例数据...");
                    insertSampleInventoryData();
                }
                return;
            }

            log.info("库存表 erp_inventory 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_inventory` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `warehouse_code` VARCHAR(50) DEFAULT NULL COMMENT '仓库编码',
                    `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
                    `location_code` VARCHAR(50) DEFAULT NULL COMMENT '库位编码',
                    `quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '库存数量',
                    `locked_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '锁定数量',
                    `available_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '可用数量',
                    `safety_stock` DECIMAL(12,2) DEFAULT 0.00 COMMENT '安全库存',
                    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
                    `production_date` DATETIME DEFAULT NULL COMMENT '生产日期',
                    `expiry_date` DATETIME DEFAULT NULL COMMENT '有效期至',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 2冻结',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_product_code` (`product_code`),
                    KEY `idx_warehouse_code` (`warehouse_code`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("库存表创建成功");
            
            insertSampleInventoryData();
            
        } catch (Exception e) {
            log.error("创建库存表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例库存数据（每产品2个仓库，共约70条）
     */
    private void insertSampleInventoryData() {
        try {
            List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT id, product_code, product_name FROM erp_product ORDER BY id"
            );
            
            if (!products.isEmpty()) {
                String[][] warehouses = {
                    {"WH001", "主仓库", "A-01-01"},
                    {"WH002", "备用仓库", "B-02-03"},
                    {"WH001", "主仓库", "A-01-02"},
                    {"WH003", "华东分仓", "C-03-01"},
                    {"WH004", "华南分仓", "D-01-05"}
                };
                
                int inventoryCount = 0;
                for (int i = 0; i < products.size(); i++) {
                    Map<String, Object> product = products.get(i);
                    Long productId = ((Number) product.get("id")).longValue();
                    String productCode = (String) product.get("product_code");
                    String productName = (String) product.get("product_name");
                    
                    int warehousesPerProduct = 2 + (i % 2);
                    for (int w = 0; w < warehousesPerProduct; w++) {
                        int whIndex = (i + w) % warehouses.length;
                        
                        int baseQty = 50 + (i % 10) * 20 + w * 10;
                        int lockedQty = 5 + (i % 5) * 3 + w * 2;
                        
                        java.math.BigDecimal quantity = new java.math.BigDecimal(baseQty);
                        java.math.BigDecimal lockedQuantity = new java.math.BigDecimal(lockedQty);
                        java.math.BigDecimal availableQuantity = quantity.subtract(lockedQuantity);
                        java.math.BigDecimal safetyStock = new java.math.BigDecimal(10 + (i % 8) * 5);
                        
                        jdbcTemplate.update(
                            "INSERT INTO `erp_inventory` (`product_id`, `product_code`, `product_name`, `specification`, `unit`, `warehouse_code`, `warehouse_name`, `location_code`, `quantity`, `locked_quantity`, `available_quantity`, `safety_stock`, `batch_no`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            productId, productCode, productName, "规格型号" + (i + 1), "台", 
                            warehouses[whIndex][0], warehouses[whIndex][1], warehouses[whIndex][2], 
                            quantity, lockedQuantity, availableQuantity, safetyStock, 
                            "BATCH" + (20260400 + i * 10 + w), 
                            "示例库存数据", 1
                        );
                        inventoryCount++;
                    }
                }
                log.info("示例库存数据插入成功（共{}条）", inventoryCount);
            }
            
        } catch (Exception e) {
            log.error("插入示例库存数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建需求来源表
     */
    private void checkAndCreateDemandSourceTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_demand_source'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("需求来源表 erp_demand_source 已存在");
                return;
            }

            log.info("需求来源表 erp_demand_source 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_demand_source` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `source_no` VARCHAR(50) NOT NULL COMMENT '来源编号',
                    `source_type` TINYINT NOT NULL COMMENT '来源类型：1销售订单 2预测单',
                    `source_id` BIGINT DEFAULT NULL COMMENT '来源单据ID',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `demand_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '需求数量',
                    `demand_date` DATE DEFAULT NULL COMMENT '需求日期',
                    `allocated_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已分配数量',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1待处理 2已处理 3已取消',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_source_no` (`source_no`),
                    KEY `idx_source_type` (`source_type`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_demand_date` (`demand_date`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='需求来源表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("需求来源表创建成功");
            
        } catch (Exception e) {
            log.error("创建需求来源表失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建净需求表
     */
    private void checkAndCreateNetRequirementTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_net_requirement'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("净需求表 erp_net_requirement 已存在");
                return;
            }

            log.info("净需求表 erp_net_requirement 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_net_requirement` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `plan_no` VARCHAR(50) NOT NULL COMMENT '计划编号',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `requirement_date` DATE DEFAULT NULL COMMENT '需求日期',
                    `gross_demand` DECIMAL(12,2) DEFAULT 0.00 COMMENT '毛需求',
                    `stock_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '库存数量',
                    `locked_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '锁定数量',
                    `safety_stock` DECIMAL(12,2) DEFAULT 0.00 COMMENT '安全库存',
                    `available_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '可用数量',
                    `net_requirement` DECIMAL(12,2) DEFAULT 0.00 COMMENT '净需求',
                    `planned_receipt` DECIMAL(12,2) DEFAULT 0.00 COMMENT '计划接收',
                    `planned_order` DECIMAL(12,2) DEFAULT 0.00 COMMENT '计划订单',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1待确认 2已确认 3已执行',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_plan_no` (`plan_no`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_requirement_date` (`requirement_date`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='净需求表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("净需求表创建成功");
            
        } catch (Exception e) {
            log.error("创建净需求表失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建设备表
     */
    private void checkAndCreateEquipmentTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_equipment'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("设备表 erp_equipment 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_equipment", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("设备表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleEquipmentData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("设备表为空，插入示例数据...");
                    insertSampleEquipmentData();
                }
                return;
            }

            log.info("设备表 erp_equipment 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_equipment` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `equipment_code` VARCHAR(50) NOT NULL COMMENT '设备编码',
                    `equipment_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
                    `equipment_type` VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `brand` VARCHAR(50) DEFAULT NULL COMMENT '品牌',
                    `model` VARCHAR(50) DEFAULT NULL COMMENT '型号',
                    `capacity_per_hour` DECIMAL(12,2) DEFAULT 0.00 COMMENT '每小时产能',
                    `capacity_unit` VARCHAR(20) DEFAULT NULL COMMENT '产能单位',
                    `purchase_date` DATE DEFAULT NULL COMMENT '购买日期',
                    `warranty_expiry_date` DATE DEFAULT NULL COMMENT '保修到期日期',
                    `workshop` VARCHAR(50) DEFAULT NULL COMMENT '车间',
                    `workcenter` VARCHAR(50) DEFAULT NULL COMMENT '工作中心',
                    `maintenance_date` DATE DEFAULT NULL COMMENT '上次维护日期',
                    `maintenance_interval_days` INT DEFAULT 90 COMMENT '维护间隔天数',
                    `responsible_person` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 2维护中 3停用 4报废',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_equipment_code` (`equipment_code`),
                    KEY `idx_equipment_name` (`equipment_name`),
                    KEY `idx_equipment_type` (`equipment_type`),
                    KEY `idx_workshop` (`workshop`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备管理表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("设备表创建成功");
            
            insertSampleEquipmentData();
            
        } catch (Exception e) {
            log.error("创建设备表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例设备数据（35条）
     */
    private void insertSampleEquipmentData() {
        try {
            String[][] equipments = {
                {"EQ001", "CNC数控车床", "车床", "FANUC 0i-MF", "发那科", "α-D21MiA", "50.00", "件", "2023-01-15", "2026-01-14", "一车间", "数控中心", "2026-01-10", "90", "张工", "高精度数控加工设备"},
                {"EQ002", "立式加工中心", "加工中心", "VMC-850", "沈阳机床", "VMC850B", "30.00", "件", "2023-03-20", "2026-03-19", "一车间", "数控中心", "2026-02-15", "90", "李工", "立式数控加工中心"},
                {"EQ003", "卧式铣床", "铣床", "X6132", "北京一机", "X6132A", "20.00", "件", "2022-06-10", "2025-06-09", "一车间", "机加工区", "2025-12-20", "60", "王工", "普通卧式铣床"},
                {"EQ004", "平面磨床", "磨床", "M7130", "杭州机床", "M7130H", "15.00", "件", "2022-08-25", "2025-08-24", "一车间", "机加工区", "2026-01-05", "90", "刘工", "精密平面磨床"},
                {"EQ005", "摇臂钻床", "钻床", "Z3050", "中捷", "Z3050×16", "25.00", "件", "2023-02-14", "2026-02-13", "一车间", "机加工区", "2026-02-01", "90", "陈工", "液压摇臂钻床"},
                {"EQ006", "线切割机", "线切割", "DK7740", "苏州三光", "DK7740", "10.00", "件", "2023-04-18", "2026-04-17", "一车间", "电加工区", "2026-01-25", "60", "赵工", "快走丝线切割"},
                {"EQ007", "电火花机", "电火花", "EDM450", "台一", "EDM-450", "8.00", "件", "2023-05-22", "2026-05-21", "一车间", "电加工区", "2026-02-10", "90", "孙工", "精密电火花成型机"},
                {"EQ008", "数控折弯机", "折弯机", "WC67Y-100", "扬力", "WC67Y-100/3200", "40.00", "件", "2022-11-30", "2025-11-29", "二车间", "钣金区", "2025-11-15", "60", "周工", "液压板料折弯机"},
                {"EQ009", "数控剪板机", "剪板机", "QC12Y-6", "扬力", "QC12Y-6×3200", "60.00", "件", "2022-12-15", "2025-12-14", "二车间", "钣金区", "2025-12-01", "60", "吴工", "液压摆式剪板机"},
                {"EQ010", "激光切割机", "切割机", "F3015", "大族激光", "F3015", "35.00", "件", "2024-01-08", "2027-01-07", "二车间", "钣金区", "2026-01-18", "90", "郑工", "光纤激光切割机"},
                {"EQ011", "冲床", "冲床", "JH21-80", "沃得精机", "JH21-80", "45.00", "件", "2023-06-30", "2026-06-29", "二车间", "冲压区", "2026-01-22", "90", "冯工", "开式固定台压力机"},
                {"EQ012", "数控车床", "车床", "CK6140", "大连机床", "CK6140×1000", "28.00", "件", "2022-09-12", "2025-09-11", "一车间", "数控中心", "2025-12-10", "60", "褚工", "经济型数控车床"},
                {"EQ013", "龙门铣床", "铣床", "X2010", "北京一机", "X2010×3000", "12.00", "件", "2021-10-20", "2024-10-19", "一车间", "机加工区", "2024-10-01", "30", "卫工", "龙门铣床（待检修）"},
                {"EQ014", "外圆磨床", "磨床", "M1432", "上海机床", "M1432B×1000", "18.00", "件", "2023-07-15", "2026-07-14", "一车间", "机加工区", "2026-01-28", "90", "蒋工", "万能外圆磨床"},
                {"EQ015", "坐标镗床", "镗床", "TX611", "昆明机床", "TX6111C", "8.00", "件", "2022-04-05", "2025-04-04", "一车间", "精密区", "2025-03-20", "60", "沈工", "数显卧式镗床"},
                {"EQ016", "滚齿机", "齿轮加工", "Y3180", "重庆机床", "Y3180H", "6.00", "件", "2023-08-22", "2026-08-21", "一车间", "齿轮区", "2026-02-05", "90", "韩工", "滚齿机"},
                {"EQ017", "插齿机", "齿轮加工", "Y5132", "天津一机", "Y5132A", "5.00", "件", "2023-09-10", "2026-09-09", "一车间", "齿轮区", "2026-01-30", "90", "杨工", "插齿机"},
                {"EQ018", "磨齿机", "齿轮加工", "Y7131", "秦川机床", "Y7131A", "4.00", "件", "2024-02-14", "2027-02-13", "一车间", "齿轮区", "2026-02-12", "90", "朱工", "磨齿机"},
                {"EQ019", "热处理炉", "热处理", "RT-90-9", "南京摄炉", "RT-90-9", "2000.00", "千克", "2022-07-08", "2025-07-07", "三车间", "热处理区", "2025-06-25", "60", "秦工", "箱式电阻炉"},
                {"EQ020", "淬火机床", "热处理", "GCK-500", "恒进", "GCK-500", "30.00", "件", "2023-10-18", "2026-10-17", "三车间", "热处理区", "2026-02-08", "90", "尤工", "数控淬火机床"},
                {"EQ021", "喷砂机", "表面处理", "PS-1010", "吉川", "PS-1010A", "50.00", "件", "2023-11-25", "2026-11-24", "三车间", "表面处理区", "2026-01-15", "90", "许工", "加压式喷砂机"},
                {"EQ022", "电泳线", "表面处理", "DY-2020", "安捷特", "DY-2020", "80.00", "件", "2024-03-20", "2027-03-19", "三车间", "表面处理区", "2026-02-18", "90", "何工", "全自动电泳生产线"},
                {"EQ023", "喷涂线", "表面处理", "PT-3030", "裕东", "PT-3030", "60.00", "件", "2024-04-15", "2027-04-14", "三车间", "表面处理区", "2026-02-20", "90", "吕工", "粉末喷涂线"},
                {"EQ024", "装配线", "装配", "ZX-100", "自研", "ZX-A100", "100.00", "件", "2023-12-10", "2026-12-09", "四车间", "装配区", "2026-01-20", "90", "施工", "产品装配流水线"},
                {"EQ025", "测试台", "测试", "CS-200", "自研", "CS-T200", "150.00", "件", "2024-01-25", "2027-01-24", "四车间", "测试区", "2026-02-02", "90", "张工", "性能测试平台"},
                {"EQ026", "老化房", "测试", "LH-300", "宏展", "LH-300", "200.00", "件", "2024-05-08", "2027-05-07", "四车间", "测试区", "2026-02-15", "90", "孔工", "高温老化试验室"},
                {"EQ027", "包装线", "包装", "BZ-150", "永创", "BZ-150", "120.00", "件", "2024-06-12", "2027-06-11", "四车间", "包装区", "2026-02-22", "90", "曹工", "自动包装线"},
                {"EQ028", "AGV小车", "物流", "AGV-500", "新松", "AGV-S500", "50.00", "次", "2024-07-20", "2027-07-19", "全厂", "物流区", "2026-01-25", "90", "严工", "自动导引运输车"},
                {"EQ029", "立体货架", "仓储", "ST-1000", "宝钢", "ST-H1000", "1000.00", "托", "2022-05-10", "2025-05-09", "仓库", "仓储区", "2025-04-20", "30", "华工", "高层立体货架"},
                {"EQ030", "叉车", "物流", "CPCD30", "合力", "CPCD30", "200.00", "托", "2023-03-05", "2026-03-04", "全厂", "物流区", "2026-01-10", "90", "金工", "内燃平衡重式叉车"},
                {"EQ031", "堆垛机", "仓储", "SRM100", "昆船", "SRM100", "100.00", "托", "2024-02-28", "2027-02-27", "仓库", "仓储区", "2026-02-01", "90", "魏工", "巷道式堆垛机"},
                {"EQ032", "空压机", "动力", "GA-37", "阿特拉斯", "GA37", "10000.00", "升/分", "2022-10-15", "2025-10-14", "动力房", "动力区", "2025-09-30", "60", "陶工", "螺杆式空气压缩机"},
                {"EQ033", "变压器", "动力", "S11-1000", "正泰", "S11-1000KVA", "0.00", "kVA", "2021-11-20", "2024-11-19", "动力房", "动力区", "2024-10-30", "30", "姜工", "1000KVA电力变压器"},
                {"EQ034", "中央空调", "动力", "MDV-560", "美的", "MDV-560W", "0.00", "kW", "2023-08-08", "2026-08-07", "全厂", "空调区", "2026-01-05", "90", "范工", "多联式中央空调"},
                {"EQ035", "消防系统", "安全", "XFS-100", "海湾", "XFS-GST", "0.00", "套", "2022-03-12", "2025-03-11", "全厂", "安全区", "2025-02-28", "60", "方工", "火灾自动报警系统"}
            };
            
            int[] statuses = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
                              1, 1, 2, 1, 1, 1, 1, 1, 1, 1,
                              1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                              1, 1, 2, 1, 1};
            
            for (int i = 0; i < 35; i++) {
                String[] eq = equipments[i];
                jdbcTemplate.update(
                    "INSERT IGNORE INTO `erp_equipment` (`equipment_code`, `equipment_name`, `equipment_type`, `specification`, `brand`, `model`, `capacity_per_hour`, `capacity_unit`, `purchase_date`, `warranty_expiry_date`, `workshop`, `workcenter`, `maintenance_date`, `maintenance_interval_days`, `responsible_person`, `remark`, `status`, `is_deleted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)",
                    eq[0], eq[1], eq[2], eq[3], eq[4], eq[5], 
                    new java.math.BigDecimal(eq[6]), eq[7], eq[8], eq[9], eq[10], eq[11], eq[12], 
                    Integer.parseInt(eq[13]), eq[14], eq[15], statuses[i]
                );
            }
            
            log.info("示例设备数据插入成功（35条）");
            
        } catch (Exception e) {
            log.error("插入示例设备数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建班组表
     */
    private void checkAndCreateWorkGroupTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_work_group'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("班组表 erp_work_group 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_work_group", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("班组表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleWorkGroupData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("班组表为空，插入示例数据...");
                    insertSampleWorkGroupData();
                }
                return;
            }

            log.info("班组表 erp_work_group 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_work_group` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `group_code` VARCHAR(50) NOT NULL COMMENT '班组编码',
                    `group_name` VARCHAR(100) NOT NULL COMMENT '班组名称',
                    `group_type` VARCHAR(50) DEFAULT NULL COMMENT '班组类型',
                    `supervisor` VARCHAR(50) DEFAULT NULL COMMENT '班组长',
                    `supervisor_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
                    `workshop` VARCHAR(50) DEFAULT NULL COMMENT '车间',
                    `workcenter` VARCHAR(50) DEFAULT NULL COMMENT '工作中心',
                    `member_count` INT DEFAULT 0 COMMENT '成员数量',
                    `skill_level` VARCHAR(20) DEFAULT NULL COMMENT '技能等级',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_group_code` (`group_code`),
                    KEY `idx_group_name` (`group_name`),
                    KEY `idx_group_type` (`group_type`),
                    KEY `idx_workshop` (`workshop`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班组管理表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("班组表创建成功");
            
            insertSampleWorkGroupData();
            
        } catch (Exception e) {
            log.error("创建班组表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例班组数据（35条）
     */
    private void insertSampleWorkGroupData() {
        try {
            String[][] groups = {
                {"GRP001", "数控一班", "生产班", "张师傅", "13812345678", "一车间", "数控中心", "8", "高级"},
                {"GRP002", "数控二班", "生产班", "李师傅", "13823456789", "一车间", "数控中心", "7", "高级"},
                {"GRP003", "铣床班", "生产班", "王师傅", "13834567890", "一车间", "机加工区", "6", "中级"},
                {"GRP004", "磨床班", "生产班", "刘师傅", "13845678901", "一车间", "机加工区", "5", "中级"},
                {"GRP005", "钻床班", "生产班", "陈师傅", "13856789012", "一车间", "机加工区", "4", "中级"},
                {"GRP006", "电加工班", "生产班", "赵师傅", "13867890123", "一车间", "电加工区", "4", "中级"},
                {"GRP007", "钣金一班", "生产班", "孙师傅", "13878901234", "二车间", "钣金区", "9", "高级"},
                {"GRP008", "钣金二班", "生产班", "周师傅", "13889012345", "二车间", "钣金区", "8", "高级"},
                {"GRP009", "冲压班", "生产班", "吴师傅", "13890123456", "二车间", "冲压区", "7", "中级"},
                {"GRP010", "激光切割班", "生产班", "郑师傅", "13801234567", "二车间", "钣金区", "6", "高级"},
                {"GRP011", "热处理班", "生产班", "冯师傅", "13812345678", "三车间", "热处理区", "5", "高级"},
                {"GRP012", "表面处理班", "生产班", "褚师傅", "13823456789", "三车间", "表面处理区", "6", "中级"},
                {"GRP013", "装配一班", "生产班", "卫师傅", "13834567890", "四车间", "装配区", "10", "高级"},
                {"GRP014", "装配二班", "生产班", "蒋师傅", "13845678901", "四车间", "装配区", "9", "高级"},
                {"GRP015", "测试班", "生产班", "沈师傅", "13856789012", "四车间", "测试区", "7", "中级"},
                {"GRP016", "包装班", "生产班", "韩师傅", "13867890123", "四车间", "包装区", "8", "中级"},
                {"GRP017", "维修一班", "维修班", "杨师傅", "13878901234", "一车间", "设备区", "4", "高级"},
                {"GRP018", "维修二班", "维修班", "朱师傅", "13889012345", "二车间", "设备区", "3", "高级"},
                {"GRP019", "维修三班", "维修班", "秦师傅", "13890123456", "三车间", "设备区", "3", "中级"},
                {"GRP020", "维修四班", "维修班", "尤师傅", "13801234567", "四车间", "设备区", "3", "中级"},
                {"GRP021", "质检一班", "质检班", "许师傅", "13812345678", "一车间", "质检区", "5", "高级"},
                {"GRP022", "质检二班", "质检班", "何师傅", "13823456789", "二车间", "质检区", "4", "高级"},
                {"GRP023", "质检三班", "质检班", "吕师傅", "13834567890", "三车间", "质检区", "4", "中级"},
                {"GRP024", "质检四班", "质检班", "施师傅", "13845678901", "四车间", "质检区", "5", "中级"},
                {"GRP025", "物流一班", "物流班", "张师傅", "13856789012", "全厂", "物流区", "6", "中级"},
                {"GRP026", "物流二班", "物流班", "孔师傅", "13867890123", "仓库", "仓储区", "5", "中级"},
                {"GRP027", "动力班", "辅助班", "曹师傅", "13878901234", "动力房", "动力区", "3", "高级"},
                {"GRP028", "安全班", "辅助班", "严师傅", "13889012345", "全厂", "安全区", "4", "中级"},
                {"GRP029", "保洁班", "辅助班", "华师傅", "13890123456", "全厂", "保洁区", "8", "初级"},
                {"GRP030", "绿化班", "辅助班", "金师傅", "13801234567", "全厂", "绿化区", "3", "初级"},
                {"GRP031", "厨房班", "辅助班", "魏师傅", "13812345678", "食堂", "厨房区", "6", "初级"},
                {"GRP032", "保安一班", "辅助班", "陶师傅", "13823456789", "门卫", "安保区", "4", "初级"},
                {"GRP033", "保安二班", "辅助班", "姜师傅", "13834567890", "门卫", "安保区", "4", "初级"},
                {"GRP034", "研发一班", "技术班", "范师傅", "13845678901", "研发部", "研发区", "5", "专家级"},
                {"GRP035", "研发二班", "技术班", "方师傅", "13856789012", "研发部", "研发区", "4", "专家级"}
            };
            
            int[] statuses = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
                              1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                              1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                              1, 1, 1, 1, 1};
            
            for (int i = 0; i < 35; i++) {
                String[] grp = groups[i];
                jdbcTemplate.update(
                    "INSERT IGNORE INTO `erp_work_group` (`group_code`, `group_name`, `group_type`, `supervisor`, `supervisor_phone`, `workshop`, `workcenter`, `member_count`, `skill_level`, `remark`, `status`, `is_deleted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)",
                    grp[0], grp[1], grp[2], grp[3], grp[4], grp[5], grp[6], 
                    Integer.parseInt(grp[7]), grp[8], grp[1] + "，负责" + grp[5] + grp[6] + "的生产任务", statuses[i]
                );
            }
            
            log.info("示例班组数据插入成功（35条）");
            
        } catch (Exception e) {
            log.error("插入示例班组数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建人员排班表
     */
    private void checkAndCreateEmployeeScheduleTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_employee_schedule'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("人员排班表 erp_employee_schedule 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_employee_schedule", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("人员排班表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleEmployeeScheduleData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("人员排班表为空，插入示例数据...");
                    insertSampleEmployeeScheduleData();
                }
                return;
            }

            log.info("人员排班表 erp_employee_schedule 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_employee_schedule` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `schedule_no` VARCHAR(50) NOT NULL COMMENT '排班编号',
                    `group_id` BIGINT DEFAULT NULL COMMENT '班组ID',
                    `group_code` VARCHAR(50) DEFAULT NULL COMMENT '班组编码',
                    `group_name` VARCHAR(100) DEFAULT NULL COMMENT '班组名称',
                    `schedule_date` DATE DEFAULT NULL COMMENT '排班日期',
                    `shift_type` TINYINT DEFAULT 1 COMMENT '班次类型：1白班 2夜班 3中班',
                    `start_time` TIME DEFAULT NULL COMMENT '开始时间',
                    `end_time` TIME DEFAULT NULL COMMENT '结束时间',
                    `work_hours` DECIMAL(5,1) DEFAULT 8.0 COMMENT '工时',
                    `employee_names` VARCHAR(500) DEFAULT NULL COMMENT '排班人员',
                    `responsible_person` VARCHAR(50) DEFAULT NULL COMMENT '负责人',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1待确认 2已确认 3已执行 4已取消',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_schedule_no` (`schedule_no`),
                    KEY `idx_group_id` (`group_id`),
                    KEY `idx_schedule_date` (`schedule_date`),
                    KEY `idx_shift_type` (`shift_type`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员排班表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("人员排班表创建成功");
            
            insertSampleEmployeeScheduleData();
            
        } catch (Exception e) {
            log.error("创建人员排班表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例人员排班数据
     */
    private void insertSampleEmployeeScheduleData() {
        try {
            // 先查询班组数据
            List<Map<String, Object>> groups = jdbcTemplate.queryForList(
                "SELECT id, group_code, group_name, supervisor FROM erp_work_group LIMIT 20"
            );
            
            if (!groups.isEmpty()) {
                String[] shiftTypes = {"1", "2", "3"};
                String[] startTimes = {"08:00:00", "20:00:00", "14:00:00"};
                String[] endTimes = {"17:00:00", "06:00:00", "23:00:00"};
                double[] workHours = {8.0, 10.0, 9.0};
                
                int[] statuses = {2, 2, 3, 3, 2, 2, 3, 3, 2, 2,
                                  3, 3, 2, 2, 3, 3, 2, 2, 3, 3,
                                  2, 2, 3, 3, 2, 2, 3, 3, 2, 2};
                
                // 生成近15天的排班
                java.time.LocalDate baseDate = java.time.LocalDate.now();
                int scheduleCount = 0;
                
                for (int i = 0; i < 15; i++) {
                    java.time.LocalDate scheduleDate = baseDate.plusDays(i);
                    for (int j = 0; j < Math.min(groups.size(), 2); j++) {
                        Map<String, Object> group = groups.get(j);
                        Long groupId = ((Number) group.get("id")).longValue();
                        String groupCode = (String) group.get("group_code");
                        String groupName = (String) group.get("group_name");
                        String supervisor = (String) group.get("supervisor");
                        
                        for (int k = 0; k < 3; k++) {
                            String scheduleNo = String.format("SCH%s%03d", scheduleDate.toString().replace("-", ""), scheduleCount + 1);
                            
                            String employees = supervisor + ", 员工" + (j + 1) + "-1, 员工" + (j + 1) + "-2, 员工" + (j + 1) + "-3";
                            
                            jdbcTemplate.update(
                                "INSERT IGNORE INTO `erp_employee_schedule` (`schedule_no`, `group_id`, `group_code`, `group_name`, `schedule_date`, `shift_type`, `start_time`, `end_time`, `work_hours`, `employee_names`, `responsible_person`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                scheduleNo, groupId, groupCode, groupName, scheduleDate.toString(), 
                                Integer.parseInt(shiftTypes[k]), startTimes[k], endTimes[k], 
                                workHours[k], employees, supervisor, 
                                groupName + "的" + (k == 0 ? "白班" : k == 1 ? "夜班" : "中班") + "排班", 
                                statuses[scheduleCount % statuses.length]
                            );
                            
                            scheduleCount++;
                            if (scheduleCount >= 35) break;
                        }
                        if (scheduleCount >= 35) break;
                    }
                    if (scheduleCount >= 35) break;
                }
                
                log.info("示例人员排班数据插入成功（共{}条）", scheduleCount);
            }
            
        } catch (Exception e) {
            log.error("插入示例人员排班数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建主生产计划表
     */
    private void checkAndCreateMpsTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_mps'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("主生产计划表 erp_mps 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_mps", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("主生产计划表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleMpsData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("主生产计划表为空，插入示例数据...");
                    insertSampleMpsData();
                }
                return;
            }

            log.info("主生产计划表 erp_mps 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_mps` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `mps_no` VARCHAR(50) NOT NULL COMMENT 'MPS编号',
                    `plan_name` VARCHAR(100) DEFAULT NULL COMMENT '计划名称',
                    `plan_type` TINYINT DEFAULT 1 COMMENT '计划类型：1月度 2季度 3年度',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `net_requirement` DECIMAL(12,2) DEFAULT 0.00 COMMENT '净需求',
                    `planned_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '计划数量',
                    `plan_start_date` DATE DEFAULT NULL COMMENT '计划开始日期',
                    `plan_end_date` DATE DEFAULT NULL COMMENT '计划结束日期',
                    `actual_start_date` DATE DEFAULT NULL COMMENT '实际开始日期',
                    `actual_end_date` DATE DEFAULT NULL COMMENT '实际结束日期',
                    `equipment_id` BIGINT DEFAULT NULL COMMENT '设备ID',
                    `equipment_code` VARCHAR(50) DEFAULT NULL COMMENT '设备编码',
                    `equipment_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
                    `group_id` BIGINT DEFAULT NULL COMMENT '班组ID',
                    `group_code` VARCHAR(50) DEFAULT NULL COMMENT '班组编码',
                    `group_name` VARCHAR(100) DEFAULT NULL COMMENT '班组名称',
                    `priority` TINYINT DEFAULT 3 COMMENT '优先级：1-5（1最高）',
                    `capacity_required` DECIMAL(12,2) DEFAULT 0.00 COMMENT '所需产能',
                    `capacity_available` DECIMAL(12,2) DEFAULT 0.00 COMMENT '可用产能',
                    `capacity_utilization` DECIMAL(5,2) DEFAULT 0.00 COMMENT '产能利用率',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1计划 2执行 3完成 4取消',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_mps_no` (`mps_no`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_plan_start_date` (`plan_start_date`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主生产计划表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("主生产计划表创建成功");
            
            insertSampleMpsData();
            
        } catch (Exception e) {
            log.error("创建主生产计划表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例MPS数据
     */
    private void insertSampleMpsData() {
        try {
            // 先查询产品、设备和班组数据
            List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT id, product_code, product_name, specification FROM erp_product LIMIT 10"
            );
            List<Map<String, Object>> equipments = jdbcTemplate.queryForList(
                "SELECT id, equipment_code, equipment_name, capacity_per_hour FROM erp_equipment WHERE status = 1 LIMIT 10"
            );
            List<Map<String, Object>> groups = jdbcTemplate.queryForList(
                "SELECT id, group_code, group_name FROM erp_work_group WHERE status = 1 LIMIT 10"
            );
            
            if (!products.isEmpty() && !equipments.isEmpty() && !groups.isEmpty()) {
                java.time.LocalDate baseDate = java.time.LocalDate.now();
                int mpsCount = 0;
                
                for (int i = 0; i < 35; i++) {
                    Map<String, Object> product = products.get(i % products.size());
                    Map<String, Object> equipment = equipments.get(i % equipments.size());
                    Map<String, Object> group = groups.get(i % groups.size());
                    
                    Long productId = ((Number) product.get("id")).longValue();
                    String productCode = (String) product.get("product_code");
                    String productName = (String) product.get("product_name");
                    String specification = (String) product.get("specification");
                    
                    Long equipmentId = ((Number) equipment.get("id")).longValue();
                    String equipmentCode = (String) equipment.get("equipment_code");
                    String equipmentName = (String) equipment.get("equipment_name");
                    double capacityPerHour = ((java.math.BigDecimal) equipment.get("capacity_per_hour")).doubleValue();
                    
                    Long groupId = ((Number) group.get("id")).longValue();
                    String groupCode = (String) group.get("group_code");
                    String groupName = (String) group.get("group_name");
                    
                    String mpsNo = String.format("MPS2026%05d", i + 1);
                    String planName = productName + "生产计划";
                    int planType = (i % 3) + 1;
                    
                    java.math.BigDecimal netRequirement = new java.math.BigDecimal(100 + (i * 50));
                    java.math.BigDecimal plannedQuantity = netRequirement;
                    
                    java.time.LocalDate planStartDate = baseDate.plusDays(i * 2);
                    java.time.LocalDate planEndDate = planStartDate.plusDays(7 + (i % 3));
                    
                    int priority = 3 + (i % 3) - 1;
                    if (priority < 1) priority = 1;
                    if (priority > 5) priority = 5;
                    
                    double capacityRequired = netRequirement.doubleValue() / 8.0; // 假设8小时工作制
                    double capacityAvailable = capacityPerHour * 8 * 5; // 5天工作
                    double capacityUtilization = Math.min(100.0, (capacityRequired / capacityAvailable) * 100);
                    
                    int status = (i % 4) + 1;
                    if (status > 4) status = 4;
                    
                    jdbcTemplate.update(
                        "INSERT IGNORE INTO `erp_mps` (`mps_no`, `plan_name`, `plan_type`, `product_id`, `product_code`, `product_name`, `specification`, `unit`, `net_requirement`, `planned_quantity`, `plan_start_date`, `plan_end_date`, `equipment_id`, `equipment_code`, `equipment_name`, `group_id`, `group_code`, `group_name`, `priority`, `capacity_required`, `capacity_available`, `capacity_utilization`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        mpsNo, planName, planType, productId, productCode, productName, specification, "台", 
                        netRequirement, plannedQuantity, planStartDate.toString(), planEndDate.toString(), 
                        equipmentId, equipmentCode, equipmentName, groupId, groupCode, groupName, 
                        priority, new java.math.BigDecimal(capacityRequired), 
                        new java.math.BigDecimal(capacityAvailable), 
                        new java.math.BigDecimal(capacityUtilization), 
                        planName + " - " + groupName + "使用" + equipmentName + "生产", 
                        status
                    );
                    
                    mpsCount++;
                }
                
                log.info("示例MPS数据插入成功（共{}条）", mpsCount);
            }
            
        } catch (Exception e) {
            log.error("插入示例MPS数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并创建齐套预警表
     */
    private void checkAndCreateKittingAlertTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_kitting_alert'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("齐套预警表 erp_kitting_alert 已存在");
                Long dataCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM erp_kitting_alert", Long.class);
                if (dataCount != null && dataCount < 30) {
                    log.info("齐套预警表数据不足（{}条），补充示例数据...", dataCount);
                    insertSampleKittingAlertData();
                } else if (dataCount != null && dataCount == 0) {
                    log.info("齐套预警表为空，插入示例数据...");
                    insertSampleKittingAlertData();
                }
                return;
            }

            log.info("齐套预警表 erp_kitting_alert 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_kitting_alert` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `alert_no` VARCHAR(50) NOT NULL COMMENT '预警编号',
                    `mps_id` BIGINT DEFAULT NULL COMMENT 'MPS ID',
                    `mps_no` VARCHAR(50) DEFAULT NULL COMMENT 'MPS编号',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `material_id` BIGINT DEFAULT NULL COMMENT '物料ID',
                    `material_code` VARCHAR(50) DEFAULT NULL COMMENT '物料编码',
                    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物料名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `required_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '需求数量',
                    `stock_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '库存数量',
                    `allocated_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已分配数量',
                    `shortage_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '短缺数量',
                    `kitting_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '齐套率',
                    `alert_level` TINYINT DEFAULT 3 COMMENT '预警等级：1紧急 2高 3中 4低',
                    `required_date` DATE DEFAULT NULL COMMENT '需求日期',
                    `expected_arrival_date` DATE DEFAULT NULL COMMENT '预计到货日期',
                    `supplier_code` VARCHAR(50) DEFAULT NULL COMMENT '供应商编码',
                    `supplier_name` VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
                    `purchase_order_no` VARCHAR(50) DEFAULT NULL COMMENT '采购订单号',
                    `synced_to_scrm` TINYINT DEFAULT 0 COMMENT '是否同步到SCM：0未同步 1已同步',
                    `synced_time` DATETIME DEFAULT NULL COMMENT '同步时间',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1待处理 2处理中 3已解决 4已忽略',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_alert_no` (`alert_no`),
                    KEY `idx_mps_id` (`mps_id`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_material_id` (`material_id`),
                    KEY `idx_alert_level` (`alert_level`),
                    KEY `idx_status` (`status`, `is_deleted`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='齐套预警表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("齐套预警表创建成功");
            
            insertSampleKittingAlertData();
            
        } catch (Exception e) {
            log.error("创建齐套预警表失败: {}", e.getMessage());
        }
    }

    /**
     * 插入示例齐套预警数据
     */
    private void insertSampleKittingAlertData() {
        try {
            // 先查询MPS、产品和物料数据
            List<Map<String, Object>> mpsList = jdbcTemplate.queryForList(
                "SELECT id, mps_no, product_id, product_code, product_name FROM erp_mps LIMIT 15"
            );
            List<Map<String, Object>> materials = jdbcTemplate.queryForList(
                "SELECT id, material_code, material_name, specification FROM erp_material LIMIT 10"
            );
            
            if (!mpsList.isEmpty() && !materials.isEmpty()) {
                String[][] suppliers = {
                    {"SUP001", "上海供应商"}, {"SUP002", "北京供应商"}, {"SUP003", "广州供应商"},
                    {"SUP004", "深圳供应商"}, {"SUP005", "杭州供应商"}
                };
                
                int alertCount = 0;
                
                for (Map<String, Object> mps : mpsList) {
                    Long mpsId = ((Number) mps.get("id")).longValue();
                    String mpsNo = (String) mps.get("mps_no");
                    Long productId = ((Number) mps.get("product_id")).longValue();
                    String productCode = (String) mps.get("product_code");
                    String productName = (String) mps.get("product_name");
                    
                    // 为每个MPS生成3-4个物料预警
                    for (int i = 0; i < 3; i++) {
                        Map<String, Object> material = materials.get((alertCount + i) % materials.size());
                        Long materialId = ((Number) material.get("id")).longValue();
                        String materialCode = (String) material.get("material_code");
                        String materialName = (String) material.get("material_name");
                        String specification = (String) material.get("specification");
                        
                        String alertNo = String.format("ALT2026%05d", alertCount + 1);
                        
                        java.math.BigDecimal requiredQuantity = new java.math.BigDecimal(100 + (alertCount * 20));
                        java.math.BigDecimal stockQuantity = new java.math.BigDecimal(50 + (alertCount * 10));
                        java.math.BigDecimal allocatedQuantity = new java.math.BigDecimal(20 + (alertCount * 5));
                        java.math.BigDecimal availableStock = stockQuantity.subtract(allocatedQuantity);
                        java.math.BigDecimal shortageQuantity = requiredQuantity.subtract(availableStock);
                        if (shortageQuantity.compareTo(java.math.BigDecimal.ZERO) < 0) {
                            shortageQuantity = java.math.BigDecimal.ZERO;
                        }
                        
                        double kittingRate = availableStock.divide(requiredQuantity, 4, java.math.RoundingMode.HALF_UP).doubleValue() * 100;
                        if (kittingRate > 100) kittingRate = 100;
                        
                        int alertLevel = 4;
                        if (kittingRate < 30) alertLevel = 1;
                        else if (kittingRate < 60) alertLevel = 2;
                        else if (kittingRate < 80) alertLevel = 3;
                        
                        java.time.LocalDate requiredDate = java.time.LocalDate.now().plusDays(7 + (alertCount % 10));
                        java.time.LocalDate expectedArrivalDate = requiredDate.minusDays(2 + (alertCount % 5));
                        
                        String[] supplier = suppliers[alertCount % suppliers.length];
                        String purchaseOrderNo = String.format("PO2026%05d", alertCount + 1);
                        
                        int syncedToScrm = (alertCount % 2);
                        java.time.LocalDateTime syncedTime = null;
                        if (syncedToScrm == 1) {
                            syncedTime = java.time.LocalDateTime.now().minusDays(alertCount % 3);
                        }
                        
                        int status = (alertCount % 4) + 1;
                        if (status > 4) status = 4;
                        
                        jdbcTemplate.update(
                            "INSERT IGNORE INTO `erp_kitting_alert` (`alert_no`, `mps_id`, `mps_no`, `product_id`, `product_code`, `product_name`, `material_id`, `material_code`, `material_name`, `specification`, `unit`, `required_quantity`, `stock_quantity`, `allocated_quantity`, `shortage_quantity`, `kitting_rate`, `alert_level`, `required_date`, `expected_arrival_date`, `supplier_code`, `supplier_name`, `purchase_order_no`, `synced_to_scrm`, `synced_time`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            alertNo, mpsId, mpsNo, productId, productCode, productName, materialId, materialCode, materialName, specification, "个", 
                            requiredQuantity, stockQuantity, allocatedQuantity, shortageQuantity, 
                            new java.math.BigDecimal(kittingRate), alertLevel, requiredDate.toString(), expectedArrivalDate.toString(), 
                            supplier[0], supplier[1], purchaseOrderNo, syncedToScrm, syncedTime, 
                            productName + "生产所需" + materialName + "短缺", status
                        );
                        
                        alertCount++;
                        if (alertCount >= 35) break;
                    }
                    if (alertCount >= 35) break;
                }
                
                log.info("示例齐套预警数据插入成功（共{}条）", alertCount);
            }
            
        } catch (Exception e) {
            log.error("插入示例齐套预警数据失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并插入产能平衡和齐套预警的菜单数据
     */
    private void checkAndInsertCapacityAndKittingMenuData() {
        try {
            // 查找生产计划与排程管理菜单
            List<Map<String, Object>> prodPlanMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '生产计划与排程管理' AND parent_id = 0 LIMIT 1"
            );
            
            Long prodPlanMenuId = 0L;
            if (prodPlanMenus.isEmpty()) {
                // 插入生产计划与排程管理顶级菜单
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    0, "生产计划与排程管理", 1, null, "/erp/production", "Layout", "Calendar", 80, 1, 1, 1
                );
                prodPlanMenuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入生产计划与排程管理菜单，ID: {}", prodPlanMenuId);
            } else {
                prodPlanMenuId = ((Number) prodPlanMenus.get(0).get("id")).longValue();
                log.info("生产计划与排程管理菜单已存在，ID: {}", prodPlanMenuId);
            }
            
            // 插入产能平衡菜单
            List<Map<String, Object>> capacityMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '产能平衡' AND parent_id = ? LIMIT 1",
                prodPlanMenuId
            );
            
            Long capacityMenuId = 0L;
            if (capacityMenus.isEmpty()) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    prodPlanMenuId, "产能平衡", 1, null, "capacity", "Layout", "TrendCharts", 2, 1, 1, 1
                );
                capacityMenuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入产能平衡菜单，ID: {}", capacityMenuId);
            } else {
                capacityMenuId = ((Number) capacityMenus.get(0).get("id")).longValue();
                log.info("产能平衡菜单已存在，ID: {}", capacityMenuId);
            }
            
            // 插入设备管理菜单
            checkAndInsertSubMenu(capacityMenuId, "设备管理", "erp:equipment:list", "equipments", "erp/Equipment", "Monitor");
            
            // 插入班组管理菜单
            checkAndInsertSubMenu(capacityMenuId, "班组管理", "erp:work-group:list", "work-groups", "erp/WorkGroup", "UserFilled");
            
            // 插入人员排班菜单
            checkAndInsertSubMenu(capacityMenuId, "人员排班", "erp:employee-schedule:list", "employee-schedules", "erp/EmployeeSchedule", "Timer");
            
            // 插入主生产计划菜单
            checkAndInsertSubMenu(capacityMenuId, "主生产计划(MPS)", "erp:mps:list", "mps", "erp/Mps", "Notebook");
            
            // 插入齐套预警菜单
            List<Map<String, Object>> kittingMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '齐套预警' AND parent_id = ? LIMIT 1",
                prodPlanMenuId
            );
            
            Long kittingMenuId = 0L;
            if (kittingMenus.isEmpty()) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    prodPlanMenuId, "齐套预警", 1, null, "kitting", "Layout", "Warning", 3, 1, 1, 1
                );
                kittingMenuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入齐套预警菜单，ID: {}", kittingMenuId);
            } else {
                kittingMenuId = ((Number) kittingMenus.get(0).get("id")).longValue();
                log.info("齐套预警菜单已存在，ID: {}", kittingMenuId);
            }
            
            // 插入齐套预警子菜单
            checkAndInsertSubMenu(kittingMenuId, "齐套预警", "erp:kitting-alert:list", "kitting-alerts", "erp/KittingAlert", "WarningFilled");
            
            // 给超级管理员分配新菜单权限
            assignMenuPermissionsToAdmin();
            
        } catch (Exception e) {
            log.error("插入产能平衡和齐套预警菜单失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并插入子菜单
     */
    private void checkAndInsertSubMenu(Long parentId, String menuName, String permissionKey, String path, String component, String icon) {
        try {
            List<Map<String, Object>> existingMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = ? AND parent_id = ? LIMIT 1",
                menuName, parentId
            );
            
            if (existingMenus.isEmpty()) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    parentId, menuName, 2, permissionKey, path, component, icon, 1, 1, 1, 1
                );
                Long menuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入{}菜单，ID: {}", menuName, menuId);
                
                // 插入按钮权限
                String[][] buttons = {
                    {"新增", permissionKey.replace(":list", ":add"), "1"},
                    {"编辑", permissionKey.replace(":list", ":edit"), "2"},
                    {"删除", permissionKey.replace(":list", ":delete"), "3"}
                };
                
                for (String[] button : buttons) {
                    jdbcTemplate.update(
                        "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        menuId, button[0], 3, button[1], Integer.parseInt(button[2]), 1, 1
                    );
                }
            } else {
                Long menuId = ((Number) existingMenus.get(0).get("id")).longValue();
                log.info("{}菜单已存在，ID: {}", menuName, menuId);
            }
        } catch (Exception e) {
            log.error("插入{}菜单失败: {}", menuName, e.getMessage());
        }
    }

    /**
     * 给超级管理员分配新菜单权限
     */
    private void assignMenuPermissionsToAdmin() {
        try {
            // 查找所有新插入的菜单ID
            List<Map<String, Object>> menuIds = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE path LIKE '/erp/production%' OR path LIKE '/erp/capacity%' OR path LIKE '/erp/kitting%' OR path LIKE '/erp/scheduling%' OR menu_name IN ('生产计划与排程管理', '产能平衡', '齐套预警', '可视化排程', '甘特图展示', '设备管理', '班组管理', '人员排班', '主生产计划(MPS)', '齐套预警')"
            );
            
            for (Map<String, Object> menu : menuIds) {
                Long menuId = ((Number) menu.get("id")).longValue();
                jdbcTemplate.update(
                    "INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (?, ?)",
                    1, menuId
                );
            }
            
            log.info("已给超级管理员分配新菜单权限");
        } catch (Exception e) {
            log.error("分配菜单权限失败: {}", e.getMessage());
        }
    }

    /**
     * 检查并插入可视化排程和甘特图菜单数据
     */
    private void checkAndInsertGanttMenuData() {
        try {
            // 查找生产计划与排程管理菜单
            List<Map<String, Object>> prodPlanMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '生产计划与排程管理' AND parent_id = 0 LIMIT 1"
            );
            
            if (prodPlanMenus.isEmpty()) {
                log.warn("生产计划与排程管理菜单不存在，跳过可视化排程菜单插入");
                return;
            }
            
            Long prodPlanMenuId = ((Number) prodPlanMenus.get(0).get("id")).longValue();
            log.info("生产计划与排程管理菜单ID: {}", prodPlanMenuId);
            
            // 插入可视化排程菜单
            List<Map<String, Object>> schedulingMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '可视化排程' AND parent_id = ? LIMIT 1",
                prodPlanMenuId
            );
            
            Long schedulingMenuId = 0L;
            if (schedulingMenus.isEmpty()) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    prodPlanMenuId, "可视化排程", 1, null, "scheduling", "Layout", "Timer", 4, 1, 1, 1
                );
                schedulingMenuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入可视化排程菜单，ID: {}", schedulingMenuId);
            } else {
                schedulingMenuId = ((Number) schedulingMenus.get(0).get("id")).longValue();
                log.info("可视化排程菜单已存在，ID: {}", schedulingMenuId);
            }
            
            // 插入甘特图展示子菜单
            checkAndInsertSubMenu(schedulingMenuId, "甘特图展示", "erp:gantt:list", "gantt-chart", "erp/GanttChart", "Histogram");
            
            // 给超级管理员分配新菜单权限
            assignMenuPermissionsToAdmin();
            
        } catch (Exception e) {
            log.error("插入可视化排程和甘特图菜单失败: {}", e.getMessage());
        }
    }

    /**
     * 修复is_deleted字段
     * 将所有is_deleted为NULL或1的记录更新为0
     * 这是为了解决数据生成器未设置is_deleted字段导致的查询不到数据的问题
     */
    private void fixIsDeletedField() {
        try {
            log.info("========================================");
            log.info("   开始检查并修复is_deleted字段...");
            log.info("========================================");
            
            // 检查设备表当前数据量
            Long equipmentTotal = 0L;
            Long equipmentNeedFix = 0L;
            try {
                equipmentTotal = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_equipment",
                    Long.class
                );
                equipmentNeedFix = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_equipment WHERE is_deleted IS NULL OR is_deleted != 0",
                    Long.class
                );
                log.info("设备表：总数据量={}条，需要修复={}条", equipmentTotal, equipmentNeedFix);
            } catch (Exception e) {
                log.warn("检查设备表数据量失败: {}", e.getMessage());
            }
            
            // 检查班组表当前数据量
            Long workGroupTotal = 0L;
            Long workGroupNeedFix = 0L;
            try {
                workGroupTotal = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_work_group",
                    Long.class
                );
                workGroupNeedFix = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM erp_work_group WHERE is_deleted IS NULL OR is_deleted != 0",
                    Long.class
                );
                log.info("班组表：总数据量={}条，需要修复={}条", workGroupTotal, workGroupNeedFix);
            } catch (Exception e) {
                log.warn("检查组表数据量失败: {}", e.getMessage());
            }
            
            // 修复设备表
            int equipmentFixed = jdbcTemplate.update(
                "UPDATE erp_equipment SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0"
            );
            
            // 修复班组表
            int workGroupFixed = jdbcTemplate.update(
                "UPDATE erp_work_group SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0"
            );
            
            // 修复人员排班表
            int employeeScheduleFixed = jdbcTemplate.update(
                "UPDATE erp_employee_schedule SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0"
            );
            
            // 修复主生产计划表
            int mpsFixed = jdbcTemplate.update(
                "UPDATE erp_mps SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0"
            );
            
            // 修复齐套预警表
            int kittingAlertFixed = jdbcTemplate.update(
                "UPDATE erp_kitting_alert SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0"
            );
            
            if (equipmentFixed > 0 || workGroupFixed > 0 || employeeScheduleFixed > 0 || mpsFixed > 0 || kittingAlertFixed > 0) {
                log.info("========================================");
                log.info("   is_deleted字段修复完成！");
                log.info("========================================");
                log.info("  设备表：修复 {} 条", equipmentFixed);
                log.info("  班组表：修复 {} 条", workGroupFixed);
                log.info("  人员排班表：修复 {} 条", employeeScheduleFixed);
                log.info("  MPS表：修复 {} 条", mpsFixed);
                log.info("  齐套预警表：修复 {} 条", kittingAlertFixed);
                log.info("========================================");
            } else {
                log.info("========================================");
                log.info("   is_deleted字段检查完成");
                log.info("========================================");
                log.info("  设备表：总数据量={}条，is_deleted=0的={}条", equipmentTotal, equipmentTotal - equipmentNeedFix);
                log.info("  班组表：总数据量={}条，is_deleted=0的={}条", workGroupTotal, workGroupTotal - workGroupNeedFix);
                log.info("========================================");
                
                // 如果表中没有数据，输出警告
                if (equipmentTotal == null || equipmentTotal == 0) {
                    log.warn("⚠️ 设备表中没有数据！请先生成测试数据");
                }
                if (workGroupTotal == null || workGroupTotal == 0) {
                    log.warn("⚠️ 班组表中没有数据！请先生成测试数据");
                }
            }
            
        } catch (Exception e) {
            log.error("修复is_deleted字段失败: {}", e.getMessage(), e);
        }
    }
}
