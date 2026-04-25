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
}
