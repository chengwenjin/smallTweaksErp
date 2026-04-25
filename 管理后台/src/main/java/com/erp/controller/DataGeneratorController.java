package com.erp.controller;

import com.baserbac.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "数据生成器")
@RestController
@RequestMapping("/api/data-generator")
@RequiredArgsConstructor
public class DataGeneratorController {

    private final JdbcTemplate jdbcTemplate;

    private static final String[] GROUP_TYPES = {"1", "2", "3"};
    private static final String[] GROUP_TYPE_NAMES = {"生产班", "维修班", "质检班"};
    private static final String[] WORKSHOPS = {"一车间", "二车间", "三车间", "四车间"};
    private static final String[] WORKCENTERS = {"数控中心", "机加工区", "电加工区", "钣金区", "冲压区", "热处理区", "表面处理区", "装配区", "测试区"};
    private static final String[] SKILL_LEVELS = {"初级", "中级", "高级", "技师"};
    private static final String[] SURNAMES = {"张", "李", "王", "刘", "陈", "赵", "孙", "周", "吴", "郑", "冯", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤"};

    private static final String[] EQUIPMENT_TYPES = {"车床", "铣床", "磨床", "钻床", "加工中心", "线切割", "电火花", "折弯机", "剪板机", "激光切割机", "冲床", "镗床", "热处理炉", "喷涂线", "装配线", "测试台", "叉车", "空压机"};
    private static final String[] BRANDS = {"发那科", "沈阳机床", "北京一机", "杭州机床", "中捷", "苏州三光", "台一", "扬力", "大族激光", "沃得精机", "大连机床", "昆明机床", "重庆机床", "南京摄炉", "安捷特", "永创", "合力", "阿特拉斯"};
    private static final String[] MODELS = {"FANUC 0i-MF", "VMC-850", "X6132", "M7130", "Z3050", "DK7740", "EDM450", "WC67Y-100", "QC12Y-6", "F3015", "JH21-80", "CK6140", "TX611", "Y3180", "RT-90-9", "DY-2020", "ZX-100", "GA-37"};

    @Operation(summary = "生成所有测试数据（班组+设备）")
    @GetMapping("/generate-all")
    public R<Map<String, Object>> generateAllData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int workGroupCount = insertWorkGroupData();
            result.put("workGroupCount", workGroupCount);
            
            int equipmentCount = insertEquipmentData();
            result.put("equipmentCount", equipmentCount);
            
            result.put("status", "success");
            result.put("message", "数据生成成功");
            
            log.info("数据生成完成：班组{}条，设备{}条", workGroupCount, equipmentCount);
            
        } catch (Exception e) {
            log.error("数据生成失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "数据生成失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    @Operation(summary = "生成班组数据（每种类型5个，共15个）")
    @GetMapping("/generate-workgroups")
    public R<Map<String, Object>> generateWorkGroups() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = insertWorkGroupData();
            result.put("count", count);
            result.put("status", "success");
            result.put("message", "班组数据生成成功，共" + count + "条");
            
            log.info("班组数据生成完成：{}条", count);
            
        } catch (Exception e) {
            log.error("班组数据生成失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "班组数据生成失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    @Operation(summary = "生成设备数据（50台）")
    @GetMapping("/generate-equipments")
    public R<Map<String, Object>> generateEquipments() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int count = insertEquipmentData();
            result.put("count", count);
            result.put("status", "success");
            result.put("message", "设备数据生成成功，共" + count + "条");
            
            log.info("设备数据生成完成：{}条", count);
            
        } catch (Exception e) {
            log.error("设备数据生成失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "设备数据生成失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    @Operation(summary = "检查中文乱码问题")
    @GetMapping("/check-charset")
    public R<Map<String, Object>> checkCharset() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> issues = new ArrayList<>();
            
            List<Map<String, Object>> workGroups = jdbcTemplate.queryForList(
                "SELECT id, group_code, group_name, group_type, supervisor, workshop, workcenter, skill_level, remark FROM erp_work_group WHERE is_deleted = 0 LIMIT 20"
            );
            
            for (Map<String, Object> group : workGroups) {
                checkChineseField(issues, "erp_work_group", group, "group_name", "班组名称");
                checkChineseField(issues, "erp_work_group", group, "supervisor", "班组长");
                checkChineseField(issues, "erp_work_group", group, "workshop", "车间");
                checkChineseField(issues, "erp_work_group", group, "workcenter", "工作中心");
                checkChineseField(issues, "erp_work_group", group, "skill_level", "技能等级");
                checkChineseField(issues, "erp_work_group", group, "remark", "备注");
            }
            
            List<Map<String, Object>> equipments = jdbcTemplate.queryForList(
                "SELECT id, equipment_code, equipment_name, equipment_type, specification, brand, model, workshop, workcenter, responsible_person, remark FROM erp_equipment WHERE is_deleted = 0 LIMIT 20"
            );
            
            for (Map<String, Object> eq : equipments) {
                checkChineseField(issues, "erp_equipment", eq, "equipment_name", "设备名称");
                checkChineseField(issues, "erp_equipment", eq, "equipment_type", "设备类型");
                checkChineseField(issues, "erp_equipment", eq, "specification", "规格型号");
                checkChineseField(issues, "erp_equipment", eq, "brand", "品牌");
                checkChineseField(issues, "erp_equipment", eq, "model", "型号");
                checkChineseField(issues, "erp_equipment", eq, "workshop", "车间");
                checkChineseField(issues, "erp_equipment", eq, "workcenter", "工作中心");
                checkChineseField(issues, "erp_equipment", eq, "responsible_person", "负责人");
                checkChineseField(issues, "erp_equipment", eq, "remark", "备注");
            }
            
            Map<String, Object> dbCharset = jdbcTemplate.queryForMap(
                "SELECT @@character_set_database as db_charset, @@collation_database as db_collation"
            );
            
            result.put("database_charset", dbCharset);
            result.put("issues_found", issues.size());
            result.put("issues", issues);
            result.put("status", issues.isEmpty() ? "success" : "warning");
            result.put("message", issues.isEmpty() ? "未检测到中文乱码问题" : "检测到" + issues.size() + "处潜在乱码问题");
            
            log.info("中文乱码检查完成：数据库字符集={}, 发现问题={}", dbCharset, issues.size());
            
        } catch (Exception e) {
            log.error("中文乱码检查失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "中文乱码检查失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    @Operation(summary = "修复is_deleted字段（将NULL或1改为0）")
    @GetMapping("/fix-is-deleted")
    public R<Map<String, Object>> fixIsDeleted() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int workGroupFixed = jdbcTemplate.update(
                "UPDATE erp_work_group SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0"
            );
            
            int equipmentFixed = jdbcTemplate.update(
                "UPDATE erp_equipment SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0"
            );
            
            Map<String, Object> workGroupCount = jdbcTemplate.queryForMap(
                "SELECT COUNT(*) as count FROM erp_work_group WHERE is_deleted = 0"
            );
            
            Map<String, Object> equipmentCount = jdbcTemplate.queryForMap(
                "SELECT COUNT(*) as count FROM erp_equipment WHERE is_deleted = 0"
            );
            
            result.put("workGroupFixed", workGroupFixed);
            result.put("equipmentFixed", equipmentFixed);
            result.put("workGroupTotal", workGroupCount.get("count"));
            result.put("equipmentTotal", equipmentCount.get("count"));
            result.put("status", "success");
            result.put("message", "is_deleted字段修复完成");
            
            log.info("is_deleted字段修复完成：班组修复{}条，设备修复{}条，当前班组总数{}，设备总数{}", 
                workGroupFixed, equipmentFixed, workGroupCount.get("count"), equipmentCount.get("count"));
            
        } catch (Exception e) {
            log.error("修复is_deleted字段失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "修复失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    private int insertWorkGroupData() {
        int count = 0;
        
        for (int typeIndex = 0; typeIndex < GROUP_TYPES.length; typeIndex++) {
            String groupType = GROUP_TYPES[typeIndex];
            String typeName = GROUP_TYPE_NAMES[typeIndex];
            
            for (int i = 1; i <= 5; i++) {
                String groupCode = String.format("GRP-%s-%02d", groupType, i);
                String groupName = String.format("%s第%d班", typeName, i);
                String supervisor = SURNAMES[(typeIndex * 5 + i - 1) % SURNAMES.length] + "师傅";
                String supervisorPhone = generatePhoneNumber();
                String workshop = WORKSHOPS[(typeIndex * 5 + i - 1) % WORKSHOPS.length];
                String workcenter = WORKCENTERS[(typeIndex * 5 + i - 1) % WORKCENTERS.length];
                int memberCount = 5 + (int)(Math.random() * 10);
                String skillLevel = SKILL_LEVELS[(typeIndex * 5 + i - 1) % SKILL_LEVELS.length];
                String remark = String.format("这是%s，负责%s的%s工作", groupName, workshop, workcenter);
                
                int inserted = jdbcTemplate.update(
                    "INSERT IGNORE INTO `erp_work_group` (`group_code`, `group_name`, `group_type`, `supervisor`, `supervisor_phone`, `workshop`, `workcenter`, `member_count`, `skill_level`, `remark`, `status`, `is_deleted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)",
                    groupCode, groupName, groupType, supervisor, supervisorPhone, workshop, workcenter, memberCount, skillLevel, remark, 1
                );
                
                if (inserted > 0) {
                    count++;
                    log.debug("插入班组数据: {}", groupName);
                }
            }
        }
        
        return count;
    }

    private int insertEquipmentData() {
        int count = 0;
        LocalDate today = LocalDate.now();
        
        for (int i = 1; i <= 50; i++) {
            int typeIndex = (i - 1) % EQUIPMENT_TYPES.length;
            String equipmentType = EQUIPMENT_TYPES[typeIndex];
            String brand = BRANDS[typeIndex];
            String model = MODELS[typeIndex];
            
            String equipmentCode = String.format("EQ-NEW-%03d", i);
            String equipmentName = String.format("%s设备-%02d", equipmentType, i);
            String specification = model + " " + (100 + i) + "型";
            BigDecimal capacityPerHour = BigDecimal.valueOf(10 + (int)(Math.random() * 90));
            String capacityUnit = "件";
            LocalDate purchaseDate = today.minusYears(1 + (int)(Math.random() * 3));
            LocalDate warrantyExpiryDate = purchaseDate.plusYears(3);
            String workshop = WORKSHOPS[(i - 1) % WORKSHOPS.length];
            String workcenter = WORKCENTERS[(i - 1) % WORKCENTERS.length];
            LocalDate maintenanceDate = today.minusDays(30 + (int)(Math.random() * 60));
            int maintenanceIntervalDays = 30 + (int)(Math.random() * 60);
            String responsiblePerson = SURNAMES[(i - 1) % SURNAMES.length] + "工";
            String remark = String.format("%s，品牌%s，型号%s，用于%s的生产加工", equipmentName, brand, model, workshop);
            
            int status;
            if (i % 10 == 0) {
                status = 2;
            } else if (i % 7 == 0) {
                status = 3;
            } else if (i % 13 == 0) {
                status = 0;
            } else {
                status = 1;
            }
            
            int inserted = jdbcTemplate.update(
                "INSERT IGNORE INTO `erp_equipment` (`equipment_code`, `equipment_name`, `equipment_type`, `specification`, `brand`, `model`, `capacity_per_hour`, `capacity_unit`, `purchase_date`, `warranty_expiry_date`, `workshop`, `workcenter`, `maintenance_date`, `maintenance_interval_days`, `responsible_person`, `remark`, `status`, `is_deleted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)",
                equipmentCode, equipmentName, equipmentType, specification, brand, model, capacityPerHour, capacityUnit, 
                java.sql.Date.valueOf(purchaseDate), java.sql.Date.valueOf(warrantyExpiryDate), 
                workshop, workcenter, java.sql.Date.valueOf(maintenanceDate), maintenanceIntervalDays, 
                responsiblePerson, remark, status
            );
            
            if (inserted > 0) {
                count++;
                log.debug("插入设备数据: {}", equipmentName);
            }
        }
        
        return count;
    }

    private void checkChineseField(List<Map<String, Object>> issues, String tableName, Map<String, Object> row, String fieldName, String fieldDesc) {
        Object value = row.get(fieldName);
        if (value == null) return;
        
        String strValue = value.toString();
        
        if (containsGarbledCharacters(strValue)) {
            Map<String, Object> issue = new HashMap<>();
            issue.put("table", tableName);
            issue.put("id", row.get("id"));
            issue.put("field", fieldName);
            issue.put("field_desc", fieldDesc);
            issue.put("value", strValue);
            issue.put("problem", "疑似包含乱码字符");
            issues.add(issue);
        }
    }

    private boolean containsGarbledCharacters(String str) {
        if (str == null || str.isEmpty()) return false;
        
        for (char c : str.toCharArray()) {
            if (c >= '\uFFFD' || c == '?' || (c >= '\u0080' && c <= '\u00FF')) {
                return true;
            }
        }
        
        return false;
    }

    private String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder("138");
        for (int i = 0; i < 8; i++) {
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }

    @Operation(summary = "生成工单测试数据（35条）")
    @GetMapping("/generate-work-orders")
    public R<Map<String, Object>> generateWorkOrders() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            checkAndCreateWorkOrderTable();
            checkAndCreateWorkOrderLogTable();
            int count = insertWorkOrderData();
            result.put("count", count);
            result.put("status", "success");
            result.put("message", "工单数据生成成功，共" + count + "条");
            
            log.info("工单数据生成完成：{}条", count);
            
        } catch (Exception e) {
            log.error("工单数据生成失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "工单数据生成失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    @Operation(summary = "初始化工单管理菜单（创建菜单并分配权限）")
    @GetMapping("/init-work-order-menus")
    public R<Map<String, Object>> initWorkOrderMenus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int menuCount = checkAndInsertWorkOrderMenuData();
            result.put("menuCount", menuCount);
            result.put("status", "success");
            result.put("message", "工单管理菜单初始化成功");
            
            log.info("工单管理菜单初始化完成：新增{}个菜单", menuCount);
            
        } catch (Exception e) {
            log.error("工单管理菜单初始化失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "工单管理菜单初始化失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    @Operation(summary = "检查工单中文乱码问题")
    @GetMapping("/check-work-order-charset")
    public R<Map<String, Object>> checkWorkOrderCharset() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> issues = new ArrayList<>();
            
            List<Map<String, Object>> workOrders = jdbcTemplate.queryForList(
                "SELECT id, work_order_no, work_order_name, work_order_type, product_name, equipment_name, group_name, remark FROM erp_work_order WHERE is_deleted = 0 LIMIT 20"
            );
            
            for (Map<String, Object> wo : workOrders) {
                checkChineseField(issues, "erp_work_order", wo, "work_order_name", "工单名称");
                checkChineseField(issues, "erp_work_order", wo, "work_order_type", "工单类型");
                checkChineseField(issues, "erp_work_order", wo, "product_name", "产品名称");
                checkChineseField(issues, "erp_work_order", wo, "equipment_name", "设备名称");
                checkChineseField(issues, "erp_work_order", wo, "group_name", "班组名称");
                checkChineseField(issues, "erp_work_order", wo, "remark", "备注");
            }
            
            Map<String, Object> dbCharset = jdbcTemplate.queryForMap(
                "SELECT @@character_set_database as db_charset, @@collation_database as db_collation"
            );
            
            result.put("database_charset", dbCharset);
            result.put("issues_found", issues.size());
            result.put("issues", issues);
            result.put("status", issues.isEmpty() ? "success" : "warning");
            result.put("message", issues.isEmpty() ? "工单表未检测到中文乱码问题" : "工单表检测到" + issues.size() + "处潜在乱码问题");
            
            log.info("工单表中文乱码检查完成：数据库字符集={}, 发现问题={}", dbCharset, issues.size());
            
        } catch (Exception e) {
            log.error("工单表中文乱码检查失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "工单表中文乱码检查失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    @Operation(summary = "生成所有工单相关数据（菜单+测试数据）")
    @GetMapping("/generate-all-work-order")
    public R<Map<String, Object>> generateAllWorkOrderData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            int menuCount = checkAndInsertWorkOrderMenuData();
            result.put("menuCount", menuCount);
            
            checkAndCreateWorkOrderTable();
            checkAndCreateWorkOrderLogTable();
            int dataCount = insertWorkOrderData();
            result.put("dataCount", dataCount);
            
            result.put("status", "success");
            result.put("message", "工单模块数据初始化成功，菜单" + menuCount + "个，数据" + dataCount + "条");
            
            log.info("工单模块数据初始化完成：菜单{}个，数据{}条", menuCount, dataCount);
            
        } catch (Exception e) {
            log.error("工单模块数据初始化失败: {}", e.getMessage(), e);
            result.put("status", "error");
            result.put("message", "工单模块数据初始化失败: " + e.getMessage());
        }
        
        return R.success(result);
    }

    private void checkAndCreateWorkOrderTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_work_order'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("工单表 erp_work_order 已存在");
                return;
            }

            log.info("工单表 erp_work_order 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_work_order` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `work_order_no` VARCHAR(50) NOT NULL COMMENT '工单编号',
                    `work_order_name` VARCHAR(100) DEFAULT NULL COMMENT '工单名称',
                    `work_order_type` VARCHAR(50) DEFAULT '正常工单' COMMENT '工单类型',
                    `source_mps_id` BIGINT DEFAULT NULL COMMENT '来源MPS ID',
                    `source_mps_no` VARCHAR(50) DEFAULT NULL COMMENT '来源MPS编号',
                    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
                    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
                    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
                    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
                    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
                    `plan_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '计划数量',
                    `actual_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '实际数量',
                    `completed_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已完成数量',
                    `scrapped_quantity` DECIMAL(12,2) DEFAULT 0.00 COMMENT '报废数量',
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
                    `priority` TINYINT DEFAULT 2 COMMENT '优先级：1高 2中 3低',
                    `order_source` VARCHAR(100) DEFAULT NULL COMMENT '订单来源',
                    `order_source_id` BIGINT DEFAULT NULL COMMENT '订单来源ID',
                    `order_source_no` VARCHAR(50) DEFAULT NULL COMMENT '订单来源编号',
                    `delivery_date` DATE DEFAULT NULL COMMENT '交期',
                    `completion_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成率(%)',
                    `approval_user_id` BIGINT DEFAULT NULL COMMENT '审批人ID',
                    `approval_user_name` VARCHAR(50) DEFAULT NULL COMMENT '审批人名称',
                    `approval_time` DATETIME DEFAULT NULL COMMENT '审批时间',
                    `approval_opinion` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
                    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1草稿 2待审批 3已审批 4已下发 5领料中 6生产中 7报工中 8待入库 9已完工 10已取消',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    UNIQUE KEY `uk_work_order_no` (`work_order_no`),
                    KEY `idx_product_id` (`product_id`),
                    KEY `idx_equipment_id` (`equipment_id`),
                    KEY `idx_group_id` (`group_id`),
                    KEY `idx_status` (`status`, `is_deleted`),
                    KEY `idx_delivery_date` (`delivery_date`),
                    KEY `idx_plan_start_date` (`plan_start_date`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单主表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("工单表创建成功");
            
        } catch (Exception e) {
            log.error("创建工单表失败: {}", e.getMessage());
        }
    }

    private void checkAndCreateWorkOrderLogTable() {
        try {
            Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'erp_work_order_log'",
                Long.class
            );
            
            if (count != null && count > 0) {
                log.info("工单流程日志表 erp_work_order_log 已存在");
                return;
            }

            log.info("工单流程日志表 erp_work_order_log 不存在，创建表...");
            
            String createTableSql = """
                CREATE TABLE `erp_work_order_log` (
                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                    `work_order_id` BIGINT NOT NULL COMMENT '工单ID',
                    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '工单编号',
                    `from_status` TINYINT DEFAULT NULL COMMENT '原状态',
                    `from_status_name` VARCHAR(50) DEFAULT NULL COMMENT '原状态名称',
                    `to_status` TINYINT NOT NULL COMMENT '目标状态',
                    `to_status_name` VARCHAR(50) NOT NULL COMMENT '目标状态名称',
                    `operation_type` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
                    `operation_name` VARCHAR(100) DEFAULT NULL COMMENT '操作名称',
                    `operation_quantity` DECIMAL(12,2) DEFAULT NULL COMMENT '操作数量',
                    `operator` VARCHAR(50) DEFAULT NULL COMMENT '操作人',
                    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人名称',
                    `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                    `operation_remark` VARCHAR(500) DEFAULT NULL COMMENT '操作备注',
                    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除',
                    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
                    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
                    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段1',
                    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段2',
                    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '扩展字段3',
                    PRIMARY KEY (`id`),
                    KEY `idx_work_order_id` (`work_order_id`),
                    KEY `idx_operation_time` (`operation_time`),
                    KEY `idx_operator` (`operator`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流程日志表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            log.info("工单流程日志表创建成功");
            
        } catch (Exception e) {
            log.error("创建工单流程日志表失败: {}", e.getMessage());
        }
    }

    private int insertWorkOrderData() {
        try {
            List<Map<String, Object>> products = jdbcTemplate.queryForList(
                "SELECT id, product_code, product_name, specification FROM erp_product ORDER BY id LIMIT 15"
            );
            List<Map<String, Object>> equipments = jdbcTemplate.queryForList(
                "SELECT id, equipment_code, equipment_name FROM erp_equipment WHERE status = 1 ORDER BY id LIMIT 10"
            );
            List<Map<String, Object>> groups = jdbcTemplate.queryForList(
                "SELECT id, group_code, group_name FROM erp_work_group WHERE status = 1 ORDER BY id LIMIT 10"
            );
            
            if (products.isEmpty() || equipments.isEmpty() || groups.isEmpty()) {
                log.warn("缺少基础数据：产品{}条，设备{}条，班组{}条", products.size(), equipments.size(), groups.size());
                return 0;
            }
            
            String[] workOrderTypes = {"正常工单", "正常工单", "正常工单", "返工工单", "试制工单", "维修工单"};
            int[] statuses = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10,
                                 1, 2, 3, 4, 5, 6, 7, 8, 9,
                                 1, 2, 3, 4, 5, 6, 7};
            
            LocalDate baseDate = LocalDate.now();
            int workOrderCount = 0;
            
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
                
                Long groupId = ((Number) group.get("id")).longValue();
                String groupCode = (String) group.get("group_code");
                String groupName = (String) group.get("group_name");
                
                String workOrderNo = String.format("WO%s%04d", baseDate.plusDays(i).toString().replace("-", ""), i + 1);
                String workOrderName = productName + "生产工单-" + (i + 1);
                String workOrderType = workOrderTypes[i % workOrderTypes.length];
                
                int planQuantity = 50 + (i * 10);
                int completedQuantity = 0;
                double completionRate = 0.0;
                
                int status = statuses[i % statuses.length];
                
                if (status >= 6 && status <= 8) {
                    completedQuantity = (int) (planQuantity * (0.3 + (i % 5) * 0.1));
                    completionRate = (completedQuantity * 100.0) / planQuantity;
                } else if (status == 9) {
                    completedQuantity = planQuantity;
                    completionRate = 100.0;
                }
                
                LocalDate planStartDate = baseDate.plusDays(i * 2);
                LocalDate planEndDate = planStartDate.plusDays(5 + (i % 3));
                LocalDate deliveryDate = planEndDate.plusDays(3);
                
                int priority = 2 + (i % 3) - 1;
                if (priority < 1) priority = 1;
                if (priority > 3) priority = 3;
                
                String remark = workOrderName + "，由" + groupName + "使用" + equipmentName + "生产";
                
                int inserted = jdbcTemplate.update(
                    "INSERT IGNORE INTO `erp_work_order` (`work_order_no`, `work_order_name`, `work_order_type`, `product_id`, `product_code`, `product_name`, `specification`, `unit`, `plan_quantity`, `completed_quantity`, `scrapped_quantity`, `plan_start_date`, `plan_end_date`, `equipment_id`, `equipment_code`, `equipment_name`, `group_id`, `group_code`, `group_name`, `priority`, `delivery_date`, `completion_rate`, `remark`, `status`, `is_deleted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)",
                    workOrderNo, workOrderName, workOrderType, productId, productCode, productName, specification, "台",
                    new BigDecimal(planQuantity), new BigDecimal(completedQuantity),
                    new BigDecimal(0), planStartDate.toString(), planEndDate.toString(),
                    equipmentId, equipmentCode, equipmentName, groupId, groupCode, groupName,
                    priority, deliveryDate.toString(), new BigDecimal(completionRate),
                    remark, status
                );
                
                if (inserted > 0) {
                    workOrderCount++;
                }
            }
            
            log.info("示例工单数据插入成功（共{}条）", workOrderCount);
            return workOrderCount;
            
        } catch (Exception e) {
            log.error("插入示例工单数据失败: {}", e.getMessage());
            return 0;
        }
    }

    private int checkAndInsertWorkOrderMenuData() {
        int menuCount = 0;
        
        try {
            List<Map<String, Object>> workOrderExecMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '生产工单与执行管理' AND parent_id = 0 LIMIT 1"
            );
            
            Long workOrderExecMenuId = 0L;
            if (workOrderExecMenus.isEmpty()) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    0, "生产工单与执行管理", 1, null, "/erp/work-order", "Layout", "List", 90, 1, 1, 1
                );
                workOrderExecMenuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入生产工单与执行管理菜单，ID: {}", workOrderExecMenuId);
                menuCount++;
            } else {
                workOrderExecMenuId = ((Number) workOrderExecMenus.get(0).get("id")).longValue();
                log.info("生产工单与执行管理菜单已存在，ID: {}", workOrderExecMenuId);
            }
            
            List<Map<String, Object>> processMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '工单全流程' AND parent_id = ? LIMIT 1",
                workOrderExecMenuId
            );
            
            Long processMenuId = 0L;
            if (processMenus.isEmpty()) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    workOrderExecMenuId, "工单全流程", 1, null, "process", "Layout", "SetUp", 1, 1, 1, 1
                );
                processMenuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入工单全流程菜单，ID: {}", processMenuId);
                menuCount++;
            } else {
                processMenuId = ((Number) processMenus.get(0).get("id")).longValue();
                log.info("工单全流程菜单已存在，ID: {}", processMenuId);
            }
            
            int subMenuCount = checkAndInsertSubMenu(processMenuId, "工单管理", "erp:work-order:list", "work-orders", "erp/WorkOrder", "Document");
            menuCount += subMenuCount;
            
            List<Map<String, Object>> trackingMenus = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE menu_name = '进度跟踪' AND parent_id = ? LIMIT 1",
                workOrderExecMenuId
            );
            
            Long trackingMenuId = 0L;
            if (trackingMenus.isEmpty()) {
                jdbcTemplate.update(
                    "INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    workOrderExecMenuId, "进度跟踪", 1, null, "tracking", "Layout", "TrendCharts", 2, 1, 1, 1
                );
                trackingMenuId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                log.info("插入进度跟踪菜单，ID: {}", trackingMenuId);
                menuCount++;
            } else {
                trackingMenuId = ((Number) trackingMenus.get(0).get("id")).longValue();
                log.info("进度跟踪菜单已存在，ID: {}", trackingMenuId);
            }
            
            int dashboardCount = checkAndInsertSubMenu(trackingMenuId, "工单看板", "erp:work-order:dashboard", "work-order-dashboard", "erp/WorkOrderDashboard", "DataLine");
            menuCount += dashboardCount;
            
            assignWorkOrderMenuPermissionsToAdmin();
            
        } catch (Exception e) {
            log.error("插入工单管理菜单失败: {}", e.getMessage());
        }
        
        return menuCount;
    }

    private int checkAndInsertSubMenu(Long parentId, String menuName, String permissionKey, String path, String component, String icon) {
        int count = 0;
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
                count++;
                
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
                    count++;
                }
            } else {
                Long menuId = ((Number) existingMenus.get(0).get("id")).longValue();
                log.info("{}菜单已存在，ID: {}", menuName, menuId);
            }
        } catch (Exception e) {
            log.error("插入{}菜单失败: {}", menuName, e.getMessage());
        }
        return count;
    }

    private void assignWorkOrderMenuPermissionsToAdmin() {
        try {
            List<Map<String, Object>> menuIds = jdbcTemplate.queryForList(
                "SELECT id FROM sys_menu WHERE path LIKE '/erp/work-order%' OR path LIKE '/erp/process%' OR path LIKE '/erp/tracking%' OR menu_name IN ('生产工单与执行管理', '工单全流程', '进度跟踪', '工单管理', '工单看板')"
            );
            
            for (Map<String, Object> menu : menuIds) {
                Long menuId = ((Number) menu.get("id")).longValue();
                jdbcTemplate.update(
                    "INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (?, ?)",
                    1, menuId
                );
            }
            
            log.info("已给超级管理员分配工单管理菜单权限");
        } catch (Exception e) {
            log.error("分配工单管理菜单权限失败: {}", e.getMessage());
        }
    }
}
