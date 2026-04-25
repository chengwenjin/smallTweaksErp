package com.erp.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataGeneratorUtil {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/small_tweaks_erp?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private static final String[] GROUP_TYPES = {"1", "2", "3"};
    private static final String[] GROUP_TYPE_NAMES = {"生产班", "维修班", "质检班"};
    private static final String[] WORKSHOPS = {"一车间", "二车间", "三车间", "四车间"};
    private static final String[] WORKCENTERS = {"数控中心", "机加工区", "电加工区", "钣金区", "冲压区", "热处理区", "表面处理区", "装配区", "测试区"};
    private static final String[] SKILL_LEVELS = {"初级", "中级", "高级", "技师"};
    private static final String[] SURNAMES = {"张", "李", "王", "刘", "陈", "赵", "孙", "周", "吴", "郑", "冯", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤"};

    private static final String[] EQUIPMENT_TYPES = {"车床", "铣床", "磨床", "钻床", "加工中心", "线切割", "电火花", "折弯机", "剪板机", "激光切割机", "冲床", "镗床", "热处理炉", "喷涂线", "装配线", "测试台", "叉车", "空压机"};
    private static final String[] BRANDS = {"发那科", "沈阳机床", "北京一机", "杭州机床", "中捷", "苏州三光", "台一", "扬力", "大族激光", "沃得精机", "大连机床", "昆明机床", "重庆机床", "南京摄炉", "安捷特", "永创", "合力", "阿特拉斯"};
    private static final String[] MODELS = {"FANUC 0i-MF", "VMC-850", "X6132", "M7130", "Z3050", "DK7740", "EDM450", "WC67Y-100", "QC12Y-6", "F3015", "JH21-80", "CK6140", "TX611", "Y3180", "RT-90-9", "DY-2020", "ZX-100", "GA-37"};

    public static void main(String[] args) {
        System.out.println("=== 开始数据生成 ===");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("数据库连接成功！");
            
            int workGroupCount = generateWorkGroups(conn);
            System.out.println("生成班组数据: " + workGroupCount + " 条");
            
            int equipmentCount = generateEquipments(conn);
            System.out.println("生成设备数据: " + equipmentCount + " 条");
            
            System.out.println("\n=== 检查中文乱码问题 ===");
            List<Map<String, Object>> issues = checkCharset(conn);
            if (issues.isEmpty()) {
                System.out.println("未检测到中文乱码问题");
            } else {
                System.out.println("检测到 " + issues.size() + " 处潜在乱码问题:");
                for (Map<String, Object> issue : issues) {
                    System.out.println("  - 表: " + issue.get("table") + 
                                     ", 字段: " + issue.get("field") + 
                                     " (" + issue.get("field_desc") + ")" +
                                     ", 值: " + issue.get("value"));
                }
            }
            
            System.out.println("\n=== 数据生成完成 ===");
            System.out.println("班组数据: " + workGroupCount + " 条");
            System.out.println("设备数据: " + equipmentCount + " 条");
            
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int generateWorkGroups(Connection conn) throws SQLException {
        int count = 0;
        String sql = "INSERT IGNORE INTO `erp_work_group` (`group_code`, `group_name`, `group_type`, `supervisor`, `supervisor_phone`, `workshop`, `workcenter`, `member_count`, `skill_level`, `remark`, `status`, `is_deleted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int typeIndex = 0; typeIndex < GROUP_TYPES.length; typeIndex++) {
                String groupType = GROUP_TYPES[typeIndex];
                String typeName = GROUP_TYPE_NAMES[typeIndex];
                
                for (int i = 1; i <= 5; i++) {
                    String groupCode = String.format("GRP-2026-%s-%02d", groupType, i);
                    String groupName = String.format("%s第%d班", typeName, i);
                    String supervisor = SURNAMES[(typeIndex * 5 + i - 1) % SURNAMES.length] + "师傅";
                    String supervisorPhone = generatePhoneNumber();
                    String workshop = WORKSHOPS[(typeIndex * 5 + i - 1) % WORKSHOPS.length];
                    String workcenter = WORKCENTERS[(typeIndex * 5 + i - 1) % WORKCENTERS.length];
                    int memberCount = 5 + (int)(Math.random() * 10);
                    String skillLevel = SKILL_LEVELS[(typeIndex * 5 + i - 1) % SKILL_LEVELS.length];
                    String remark = String.format("这是%s，负责%s的%s工作", groupName, workshop, workcenter);
                    
                    pstmt.setString(1, groupCode);
                    pstmt.setString(2, groupName);
                    pstmt.setString(3, groupType);
                    pstmt.setString(4, supervisor);
                    pstmt.setString(5, supervisorPhone);
                    pstmt.setString(6, workshop);
                    pstmt.setString(7, workcenter);
                    pstmt.setInt(8, memberCount);
                    pstmt.setString(9, skillLevel);
                    pstmt.setString(10, remark);
                    pstmt.setInt(11, 1);
                    
                    int inserted = pstmt.executeUpdate();
                    if (inserted > 0) {
                        count++;
                        System.out.println("  插入班组: " + groupName);
                    }
                }
            }
        }
        
        return count;
    }

    private static int generateEquipments(Connection conn) throws SQLException {
        int count = 0;
        LocalDate today = LocalDate.now();
        String sql = "INSERT IGNORE INTO `erp_equipment` (`equipment_code`, `equipment_name`, `equipment_type`, `specification`, `brand`, `model`, `capacity_per_hour`, `capacity_unit`, `purchase_date`, `warranty_expiry_date`, `workshop`, `workcenter`, `maintenance_date`, `maintenance_interval_days`, `responsible_person`, `remark`, `status`, `is_deleted`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 50; i++) {
                int typeIndex = (i - 1) % EQUIPMENT_TYPES.length;
                String equipmentType = EQUIPMENT_TYPES[typeIndex];
                String brand = BRANDS[typeIndex];
                String model = MODELS[typeIndex];
                
                String equipmentCode = String.format("EQ-2026-%03d", i);
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
                
                pstmt.setString(1, equipmentCode);
                pstmt.setString(2, equipmentName);
                pstmt.setString(3, equipmentType);
                pstmt.setString(4, specification);
                pstmt.setString(5, brand);
                pstmt.setString(6, model);
                pstmt.setBigDecimal(7, capacityPerHour);
                pstmt.setString(8, capacityUnit);
                pstmt.setDate(9, java.sql.Date.valueOf(purchaseDate));
                pstmt.setDate(10, java.sql.Date.valueOf(warrantyExpiryDate));
                pstmt.setString(11, workshop);
                pstmt.setString(12, workcenter);
                pstmt.setDate(13, java.sql.Date.valueOf(maintenanceDate));
                pstmt.setInt(14, maintenanceIntervalDays);
                pstmt.setString(15, responsiblePerson);
                pstmt.setString(16, remark);
                pstmt.setInt(17, status);
                
                int inserted = pstmt.executeUpdate();
                if (inserted > 0) {
                    count++;
                    System.out.println("  插入设备: " + equipmentName);
                }
            }
        }
        
        return count;
    }

    private static List<Map<String, Object>> checkCharset(Connection conn) throws SQLException {
        List<Map<String, Object>> issues = new ArrayList<>();
        
        String[] workGroupFields = {
            "group_name", "班组名称",
            "supervisor", "班组长",
            "workshop", "车间",
            "workcenter", "工作中心",
            "skill_level", "技能等级",
            "remark", "备注"
        };
        
        String workGroupSql = "SELECT id, group_code, group_name, group_type, supervisor, workshop, workcenter, skill_level, remark FROM erp_work_group WHERE is_deleted = 0 LIMIT 20";
        try (PreparedStatement pstmt = conn.prepareStatement(workGroupSql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Long id = rs.getLong("id");
                for (int i = 0; i < workGroupFields.length; i += 2) {
                    String fieldName = workGroupFields[i];
                    String fieldDesc = workGroupFields[i + 1];
                    String value = rs.getString(fieldName);
                    
                    if (value != null && containsGarbledCharacters(value)) {
                        Map<String, Object> issue = new HashMap<>();
                        issue.put("table", "erp_work_group");
                        issue.put("id", id);
                        issue.put("field", fieldName);
                        issue.put("field_desc", fieldDesc);
                        issue.put("value", value);
                        issue.put("problem", "疑似包含乱码字符");
                        issues.add(issue);
                    }
                }
            }
        }
        
        String[] equipmentFields = {
            "equipment_name", "设备名称",
            "equipment_type", "设备类型",
            "specification", "规格型号",
            "brand", "品牌",
            "model", "型号",
            "workshop", "车间",
            "workcenter", "工作中心",
            "responsible_person", "负责人",
            "remark", "备注"
        };
        
        String equipmentSql = "SELECT id, equipment_code, equipment_name, equipment_type, specification, brand, model, workshop, workcenter, responsible_person, remark FROM erp_equipment WHERE is_deleted = 0 LIMIT 20";
        try (PreparedStatement pstmt = conn.prepareStatement(equipmentSql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Long id = rs.getLong("id");
                for (int i = 0; i < equipmentFields.length; i += 2) {
                    String fieldName = equipmentFields[i];
                    String fieldDesc = equipmentFields[i + 1];
                    String value = rs.getString(fieldName);
                    
                    if (value != null && containsGarbledCharacters(value)) {
                        Map<String, Object> issue = new HashMap<>();
                        issue.put("table", "erp_equipment");
                        issue.put("id", id);
                        issue.put("field", fieldName);
                        issue.put("field_desc", fieldDesc);
                        issue.put("value", value);
                        issue.put("problem", "疑似包含乱码字符");
                        issues.add(issue);
                    }
                }
            }
        }
        
        return issues;
    }

    private static boolean containsGarbledCharacters(String str) {
        if (str == null || str.isEmpty()) return false;
        
        for (char c : str.toCharArray()) {
            if (c == '\uFFFD' || c == '?') {
                return true;
            }
            if (c >= '\u0080' && c <= '\u00FF') {
                if (c != '\u00D7' && c != '\u00F7') {
                    return true;
                }
            }
        }
        
        return false;
    }

    private static String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder("138");
        for (int i = 0; i < 8; i++) {
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }
}
