package com.erp.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GanttTestDataGenerator {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/small_tweaks_erp?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("=== 开始生成甘特图测试数据 ===");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("数据库连接成功！");
            
            int count = generateGanttTestData(conn);
            
            System.out.println("\n=== 测试数据生成完成 ===");
            System.out.println("成功生成 " + count + " 条主生产计划数据");
            
            checkData(conn);
            
        } catch (Exception e) {
            System.err.println("生成测试数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int generateGanttTestData(Connection conn) throws Exception {
        Random random = new Random();
        LocalDate today = LocalDate.now();
        
        List<Map<String, Object>> equipments = queryList(conn, 
            "SELECT id, equipment_code, equipment_name, equipment_type, workshop, workcenter FROM erp_equipment WHERE status = 1 AND is_deleted = 0"
        );
        
        List<Map<String, Object>> products = queryList(conn,
            "SELECT id, product_code, product_name, specification, unit FROM erp_product WHERE status = 1 AND is_deleted = 0"
        );
        
        List<Map<String, Object>> workGroups = queryList(conn,
            "SELECT id, group_code, group_name FROM erp_work_group WHERE status = 1 AND is_deleted = 0"
        );
        
        if (equipments.isEmpty()) {
            System.err.println("没有可用的设备数据，请先生成设备数据");
            return 0;
        }
        if (products.isEmpty()) {
            System.err.println("没有可用的产品数据，请先生成产品数据");
            return 0;
        }
        
        System.out.println("找到设备数量: " + equipments.size());
        System.out.println("找到产品数量: " + products.size());
        System.out.println("找到班组数量: " + workGroups.size());
        
        String[] planNames = {
            "春节生产计划", "Q1季度计划", "Q2季度计划", "Q3季度计划", "Q4季度计划",
            "紧急订单A", "紧急订单B", "常规生产C", "常规生产D", "测试批次E",
            "新产品试制", "客户定制X", "客户定制Y", "批量生产Z", "小批量试产",
            "库存补充计划", "促销备货计划", "新品上市计划", "年度盘点补货", "季度备货"
        };
        
        String[] priorities = {"高", "中", "低"};
        int[] priorityValues = {1, 2, 3};
        
        int count = 0;
        String insertSql = "INSERT IGNORE INTO `erp_mps` (`mps_no`, `plan_name`, `plan_type`, `product_id`, `product_code`, `product_name`, `specification`, `unit`, `net_requirement`, `planned_quantity`, `plan_start_date`, `plan_end_date`, `equipment_id`, `equipment_code`, `equipment_name`, `group_id`, `group_code`, `group_name`, `priority`, `capacity_required`, `capacity_available`, `capacity_utilization`, `remark`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            for (int i = 1; i <= 45; i++) {
                Map<String, Object> equipment = equipments.get((i - 1) % equipments.size());
                Map<String, Object> product = products.get(random.nextInt(products.size()));
                int priorityIndex = random.nextInt(3);
                int status = 1 + random.nextInt(4);
                
                String mpsNo = "MPS-GANTT-" + today.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                    String.format("%05d", i);
                
                String planName = planNames[(i - 1) % planNames.length] + "-" + i;
                
                int startDayOffset = -3 + random.nextInt(20);
                int duration = 1 + random.nextInt(7);
                
                LocalDate startDate = today.plusDays(startDayOffset);
                LocalDate endDate = startDate.plusDays(duration);
                
                BigDecimal netRequirement = new BigDecimal(100 + random.nextInt(1000));
                BigDecimal capacityPerHour = (BigDecimal) equipment.get("capacity_per_hour");
                if (capacityPerHour == null) {
                    capacityPerHour = new BigDecimal(10);
                }
                
                BigDecimal capacityPerDay = capacityPerHour.multiply(new BigDecimal(8));
                BigDecimal daysNeeded = netRequirement.divide(capacityPerDay, 0, java.math.RoundingMode.UP);
                BigDecimal capacityAvailable = capacityPerDay.multiply(daysNeeded);
                BigDecimal capacityUtilization = BigDecimal.ZERO;
                if (capacityAvailable.compareTo(BigDecimal.ZERO) > 0) {
                    capacityUtilization = netRequirement
                        .divide(capacityAvailable, 2, java.math.RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100));
                }
                
                pstmt.setString(1, mpsNo);
                pstmt.setString(2, planName);
                pstmt.setInt(3, 1);
                pstmt.setLong(4, ((Number) product.get("id")).longValue());
                pstmt.setString(5, (String) product.get("product_code"));
                pstmt.setString(6, (String) product.get("product_name"));
                pstmt.setString(7, (String) product.get("specification"));
                pstmt.setString(8, (String) product.get("unit"));
                pstmt.setBigDecimal(9, netRequirement);
                pstmt.setBigDecimal(10, netRequirement);
                pstmt.setDate(11, java.sql.Date.valueOf(startDate));
                pstmt.setDate(12, java.sql.Date.valueOf(endDate));
                pstmt.setLong(13, ((Number) equipment.get("id")).longValue());
                pstmt.setString(14, (String) equipment.get("equipment_code"));
                pstmt.setString(15, (String) equipment.get("equipment_name"));
                
                if (!workGroups.isEmpty()) {
                    Map<String, Object> workGroup = workGroups.get(random.nextInt(workGroups.size()));
                    pstmt.setLong(16, ((Number) workGroup.get("id")).longValue());
                    pstmt.setString(17, (String) workGroup.get("group_code"));
                    pstmt.setString(18, (String) workGroup.get("group_name"));
                } else {
                    pstmt.setNull(16, java.sql.Types.BIGINT);
                    pstmt.setNull(17, java.sql.Types.VARCHAR);
                    pstmt.setNull(18, java.sql.Types.VARCHAR);
                }
                
                pstmt.setInt(19, priorityValues[priorityIndex]);
                pstmt.setBigDecimal(20, netRequirement);
                pstmt.setBigDecimal(21, capacityAvailable);
                pstmt.setBigDecimal(22, capacityUtilization);
                pstmt.setString(23, "甘特图测试数据-" + i + "，优先级:" + priorities[priorityIndex] + "，状态:" + getStatusName(status));
                pstmt.setInt(24, status);
                
                int inserted = pstmt.executeUpdate();
                if (inserted > 0) {
                    count++;
                    System.out.println("  生成计划: " + mpsNo + " - " + planName + 
                        " (设备:" + equipment.get("equipment_name") + 
                        ", 产品:" + product.get("product_name") + 
                        ", 开始:" + startDate + ", 结束:" + endDate + 
                        ", 优先级:" + priorities[priorityIndex] + 
                        ", 状态:" + getStatusName(status) + ")");
                }
            }
        }
        
        return count;
    }

    private static String getStatusName(int status) {
        return switch (status) {
            case 1 -> "草稿";
            case 2 -> "已确认";
            case 3 -> "生产中";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
    }

    private static void checkData(Connection conn) throws Exception {
        System.out.println("\n=== 数据检查 ===");
        
        List<Map<String, Object>> stats = queryList(conn,
            "SELECT COUNT(*) as total_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as 草稿, " +
            "SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as 已确认, " +
            "SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) as 生产中, " +
            "SUM(CASE WHEN status = 4 THEN 1 ELSE 0 END) as 已完成, " +
            "SUM(CASE WHEN priority = 1 THEN 1 ELSE 0 END) as 高优先级, " +
            "SUM(CASE WHEN priority = 2 THEN 1 ELSE 0 END) as 中优先级, " +
            "SUM(CASE WHEN priority = 3 THEN 1 ELSE 0 END) as 低优先级 " +
            "FROM erp_mps WHERE mps_no LIKE 'MPS-GANTT-%'"
        );
        
        if (!stats.isEmpty()) {
            Map<String, Object> stat = stats.get(0);
            System.out.println("甘特图测试数据统计:");
            System.out.println("  总计: " + stat.get("total_count"));
            System.out.println("  草稿: " + stat.get("草稿"));
            System.out.println("  已确认: " + stat.get("已确认"));
            System.out.println("  生产中: " + stat.get("生产中"));
            System.out.println("  已完成: " + stat.get("已完成"));
            System.out.println("  高优先级: " + stat.get("高优先级"));
            System.out.println("  中优先级: " + stat.get("中优先级"));
            System.out.println("  低优先级: " + stat.get("低优先级"));
        }
        
        System.out.println("\n=== 检查中文编码 ===");
        List<Map<String, Object>> sampleData = queryList(conn,
            "SELECT id, mps_no, plan_name, product_name, equipment_name, remark FROM erp_mps WHERE mps_no LIKE 'MPS-GANTT-%' LIMIT 10"
        );
        
        boolean hasGarbled = false;
        for (Map<String, Object> row : sampleData) {
            String planName = (String) row.get("plan_name");
            String productName = (String) row.get("product_name");
            String equipmentName = (String) row.get("equipment_name");
            String remark = (String) row.get("remark");
            
            if (containsGarbled(planName) || containsGarbled(productName) || 
                containsGarbled(equipmentName) || containsGarbled(remark)) {
                hasGarbled = true;
                System.err.println("  警告: 发现疑似乱码数据 ID=" + row.get("id"));
                System.err.println("    plan_name: " + planName);
                System.err.println("    product_name: " + productName);
                System.err.println("    equipment_name: " + equipmentName);
            }
        }
        
        if (!hasGarbled) {
            System.out.println("  ✓ 未检测到中文乱码问题");
        }
        
        System.out.println("\n=== 检查日期范围 ===");
        List<Map<String, Object>> dateRange = queryList(conn,
            "SELECT MIN(plan_start_date) as min_start, MAX(plan_end_date) as max_end FROM erp_mps WHERE mps_no LIKE 'MPS-GANTT-%'"
        );
        
        if (!dateRange.isEmpty()) {
            System.out.println("  最早开始日期: " + dateRange.get(0).get("min_start"));
            System.out.println("  最晚结束日期: " + dateRange.get(0).get("max_end"));
        }
    }

    private static boolean containsGarbled(String str) {
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

    private static List<Map<String, Object>> queryList(Connection conn, String sql) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(rs.getMetaData().getColumnLabel(i).toLowerCase(), rs.getObject(i));
                }
                result.add(row);
            }
        }
        return result;
    }
}
