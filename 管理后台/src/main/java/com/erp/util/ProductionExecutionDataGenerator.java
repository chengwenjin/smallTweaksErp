package com.erp.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.entity.*;
import com.erp.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class ProductionExecutionDataGenerator implements ApplicationRunner {

    private final ProcessReportMapper processReportMapper;
    private final MaterialPickMapper materialPickMapper;
    private final MaterialReturnMapper materialReturnMapper;
    private final OverPickApprovalMapper overPickApprovalMapper;
    private final JdbcTemplate jdbcTemplate;

    private final Random random = new Random();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String[] WORK_ORDER_NOS = {
            "WO202401001", "WO202401002", "WO202401003", "WO202401004", "WO202401005",
            "WO202402001", "WO202402002", "WO202402003", "WO202402004", "WO202402005"
    };

    private static final String[] WORK_ORDER_NAMES = {
            "智能手机生产线A", "笔记本电脑装配线", "汽车零部件加工线", "电子元件SMT生产线",
            "医疗器械组装线", "家用电器生产线", "精密仪器加工线", "通信设备生产线",
            "新能源电池生产线", "半导体封装线"
    };

    private static final String[] PROCESS_NAMES = {
            "冲压工序", "焊接工序", "机加工工序", "热处理工序", "表面处理工序",
            "SMT贴片工序", "插件工序", "焊接工序", "测试工序", "组装工序",
            "包装工序", "检验工序", "入库工序", "出库工序", "返修工序"
    };

    private static final String[] EQUIPMENT_NAMES = {
            "数控车床-CNC-001", "数控车床-CNC-002", "数控车床-CNC-003",
            "铣床-XK-001", "铣床-XK-002", "铣床-XK-003",
            "磨床-MK-001", "磨床-MK-002",
            "钻床-ZK-001", "钻床-ZK-002",
            "焊接机-WEL-001", "焊接机-WEL-002", "焊接机-WEL-003",
            "冲压机-ST-001", "冲压机-ST-002", "冲压机-ST-003",
            "热处理炉-HT-001", "热处理炉-HT-002",
            "SMT贴片机-SMT-001", "SMT贴片机-SMT-002",
            "回流焊-REF-001", "回流焊-REF-002",
            "波峰焊-WF-001", "波峰焊-WF-002"
    };

    private static final String[] OPERATOR_NAMES = {
            "张三", "李四", "王五", "赵六", "钱七",
            "孙八", "周九", "吴十", "郑十一", "王十二",
            "刘十三", "陈十四", "杨十五", "黄十六", "朱十七",
            "林十八", "何十九", "高二十", "梁廿一", "宋廿二"
    };

    private static final String[] MATERIAL_NAMES = {
            "铝合金板材-6061", "不锈钢板材-304", "碳钢板材-Q235",
            "铜材-C1100", "铝材-6063", "塑料-ABS",
            "塑料-PC", "橡胶-天然橡胶", "电子元件-电阻10KΩ",
            "电子元件-电容100μF", "电子元件-二极管1N4007", "电子元件-三极管NPN",
            "电子元件-IC芯片STM32", "电子元件-连接器2.54mm", "螺丝-M3*10",
            "螺丝-M4*15", "螺母-M3", "螺母-M4",
            "垫片-Φ10", "轴承-6001", "齿轮-模数2",
            "弹簧-钢丝直径1mm", "密封圈-Φ20", "润滑剂-润滑油"
    };

    private static final String[] SPECIFICATIONS = {
            "厚度:2mm", "厚度:3mm", "厚度:5mm", "直径:10mm", "直径:20mm",
            "直径:30mm", "长度:100mm", "长度:200mm", "长度:300mm",
            "宽度:50mm", "宽度:100mm", "宽度:150mm",
            "精度:±0.01mm", "精度:±0.05mm", "精度:±0.1mm"
    };

    private static final String[] UNITS = {"个", "件", "套", "千克", "米", "平方米", "立方米", "升", "箱", "台"};

    private static final String[] BATCH_NOS = {
            "B202401001", "B202401002", "B202401003", "B202402001", "B202402002",
            "B202402003", "B202403001", "B202403002", "B202403003", "B202404001"
    };

    private static final String[] WAREHOUSE_NAMES = {
            "原料仓库-1号库", "原料仓库-2号库", "半成品仓库-A库", "半成品仓库-B库",
            "成品仓库-1号库", "成品仓库-2号库", "危险化学品仓库", "工具仓库",
            "备件仓库", "低值易耗品仓库"
    };

    private static final String[] REMARKS = {
            "正常生产报工", "超产报工", "返工报工", "补料申请", "余料退回",
            "质量问题", "设备故障延期", "人员调整", "材料更换", "工艺调整",
            "紧急订单", "试生产", "批量生产", "订单变更", "客户特殊要求"
    };

    private static final String[] OVER_PICK_REASONS = {
            "生产损耗超预期", "质量问题报废增加", "设备故障导致材料浪费",
            "工艺调整需要额外材料", "客户追加订单", "试生产材料损耗",
            "原材料质量问题需要补领", "工人操作失误", "材料规格不符需更换",
            "库存不足需超领"
    };

    private static final String[] RETURN_REASONS = {
            "生产剩余", "质量不合格", "规格不符", "超领退回", "工单取消",
            "材料损坏", "工艺变更", "订单变更", "库存调整", "盘点差异"
    };

    private static final String[] APPROVAL_OPINIONS = {
            "同意", "同意超领", "请控制用量", "已核实情况", "请查明损耗原因",
            "下次注意", "同意退回", "同意补料", "请核实数量", "请提供更多说明"
    };

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== 开始生成现场执行与损耗管控测试数据 ===");

        try {
            String tableCheckSql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name IN (?, ?, ?, ?)";
            Integer tableCount = jdbcTemplate.queryForObject(tableCheckSql, Integer.class,
                    "erp_process_report", "erp_material_pick", "erp_material_return", "erp_over_pick_approval");

            if (tableCount == null || tableCount < 4) {
                log.warn("数据库表不存在，跳过测试数据生成");
                return;
            }

            generateProcessReportData();
            generateMaterialPickData();
            generateMaterialReturnData();
            generateOverPickApprovalData();

            log.info("=== 现场执行与损耗管控测试数据生成完成 ===");
        } catch (Exception e) {
            log.error("测试数据生成失败", e);
            throw e;
        }
    }

    private void generateProcessReportData() {
        log.info("开始生成工序报工测试数据...");

        LambdaQueryWrapper<ErpProcessReport> countWrapper = new LambdaQueryWrapper<>();
        Long existingCount = processReportMapper.selectCount(countWrapper);

        if (existingCount != null && existingCount >= 30) {
            log.info("工序报工数据已存在({}条)，跳过生成", existingCount);
            return;
        }

        List<ErpProcessReport> reports = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (int i = 0; i < 35; i++) {
            ErpProcessReport report = new ErpProcessReport();
            report.setReportNo(generateSafeString("PR" + dtf.format(baseTime.plusHours(i * 2)) + String.format("%03d", i + 1)));
            report.setWorkOrderId((long) (random.nextInt(10) + 1));
            report.setWorkOrderNo(generateSafeString(WORK_ORDER_NOS[random.nextInt(WORK_ORDER_NOS.length)]));
            report.setWorkOrderName(generateSafeString(WORK_ORDER_NAMES[random.nextInt(WORK_ORDER_NAMES.length)]));
            report.setProcessId((long) (random.nextInt(15) + 1));
            report.setProcessCode(generateSafeString("PROC" + String.format("%03d", random.nextInt(100) + 1)));
            report.setProcessName(generateSafeString(PROCESS_NAMES[random.nextInt(PROCESS_NAMES.length)]));
            report.setEquipmentId((long) (random.nextInt(24) + 1));
            report.setEquipmentCode(generateSafeString("EQP" + String.format("%03d", random.nextInt(100) + 1)));
            report.setEquipmentName(generateSafeString(EQUIPMENT_NAMES[random.nextInt(EQUIPMENT_NAMES.length)]));
            report.setOperatorId((long) (random.nextInt(20) + 1));
            report.setOperatorCode(generateSafeString("EMP" + String.format("%05d", random.nextInt(10000) + 1)));
            report.setOperatorName(generateSafeString(OPERATOR_NAMES[random.nextInt(OPERATOR_NAMES.length)]));

            BigDecimal reportQty = BigDecimal.valueOf(random.nextInt(100) + 10);
            BigDecimal scrappedQty = BigDecimal.valueOf(random.nextInt(5));
            BigDecimal reworkQty = BigDecimal.valueOf(random.nextInt(3));
            BigDecimal qualifiedQty = reportQty.subtract(scrappedQty).subtract(reworkQty);
            if (qualifiedQty.compareTo(BigDecimal.ZERO) < 0) {
                qualifiedQty = BigDecimal.ZERO;
            }

            report.setReportQuantity(reportQty);
            report.setQualifiedQuantity(qualifiedQty);
            report.setScrappedQuantity(scrappedQty);
            report.setReworkQuantity(reworkQty);
            report.setUnit(generateSafeString(UNITS[random.nextInt(UNITS.length)]));
            report.setWorkHours(BigDecimal.valueOf(random.nextInt(24) + 0.5));

            LocalDateTime startTime = baseTime.plusDays(random.nextInt(25)).plusHours(random.nextInt(12));
            report.setStartTime(startTime);
            report.setEndTime(startTime.plusHours(random.nextInt(8) + 1));
            report.setBarcode(generateSafeString("BC" + dtf.format(startTime) + String.format("%05d", i + 1)));
            report.setBatchNo(generateSafeString(BATCH_NOS[random.nextInt(BATCH_NOS.length)]));
            report.setRemark(generateSafeString(REMARKS[random.nextInt(REMARKS.length)]));
            report.setStatus(random.nextInt(3) + 1);
            report.setIsDeleted(0);
            report.setCreateBy("system");
            report.setCreateTime(startTime);
            report.setUpdateBy("system");
            report.setUpdateTime(startTime.plusHours(random.nextInt(24)));

            reports.add(report);
        }

        for (ErpProcessReport report : reports) {
            processReportMapper.insert(report);
        }

        log.info("成功生成{}条工序报工测试数据", reports.size());
    }

    private void generateMaterialPickData() {
        log.info("开始生成领料单测试数据...");

        LambdaQueryWrapper<ErpMaterialPick> countWrapper = new LambdaQueryWrapper<>();
        Long existingCount = materialPickMapper.selectCount(countWrapper);

        if (existingCount != null && existingCount >= 30) {
            log.info("领料单数据已存在({}条)，跳过生成", existingCount);
            return;
        }

        List<ErpMaterialPick> picks = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (int i = 0; i < 35; i++) {
            ErpMaterialPick pick = new ErpMaterialPick();
            pick.setPickNo(generateSafeString("MP" + dtf.format(baseTime.plusHours(i * 2)) + String.format("%03d", i + 1)));
            pick.setWorkOrderId((long) (random.nextInt(10) + 1));
            pick.setWorkOrderNo(generateSafeString(WORK_ORDER_NOS[random.nextInt(WORK_ORDER_NOS.length)]));
            pick.setWorkOrderName(generateSafeString(WORK_ORDER_NAMES[random.nextInt(WORK_ORDER_NAMES.length)]));
            pick.setBomId((long) (random.nextInt(10) + 1));
            pick.setBomVersionId((long) (random.nextInt(5) + 1));
            pick.setBomVersionNo(generateSafeString("V" + (random.nextInt(5) + 1) + ".0"));
            pick.setProductId((long) (random.nextInt(20) + 1));
            pick.setProductCode(generateSafeString("PROD" + String.format("%05d", random.nextInt(1000) + 1)));
            pick.setProductName(generateSafeString(WORK_ORDER_NAMES[random.nextInt(WORK_ORDER_NAMES.length)]));
            pick.setMaterialId((long) (random.nextInt(24) + 1));
            pick.setMaterialCode(generateSafeString("MAT" + String.format("%05d", random.nextInt(1000) + 1)));
            pick.setMaterialName(generateSafeString(MATERIAL_NAMES[random.nextInt(MATERIAL_NAMES.length)]));
            pick.setSpecification(generateSafeString(SPECIFICATIONS[random.nextInt(SPECIFICATIONS.length)]));
            pick.setUnit(generateSafeString(UNITS[random.nextInt(UNITS.length)]));

            BigDecimal planQty = BigDecimal.valueOf(random.nextInt(100) + 10);
            BigDecimal pickedQty = planQty.multiply(BigDecimal.valueOf(random.nextDouble() * 0.8 + 0.2));
            BigDecimal remainingQty = planQty.subtract(pickedQty);
            if (remainingQty.compareTo(BigDecimal.ZERO) < 0) {
                remainingQty = BigDecimal.ZERO;
            }

            pick.setPlanQuantity(planQty);
            pick.setPickedQuantity(pickedQty.setScale(4, BigDecimal.ROUND_HALF_UP));
            pick.setRemainingQuantity(remainingQty.setScale(4, BigDecimal.ROUND_HALF_UP));

            LocalDateTime pickDate = baseTime.plusDays(random.nextInt(25));
            pick.setPlanPickDate(pickDate.toLocalDate());
            pick.setActualPickDate(pickDate.plusDays(random.nextInt(3)).toLocalDate());
            pick.setWarehouseId((long) (random.nextInt(10) + 1));
            pick.setWarehouseCode(generateSafeString("WH" + String.format("%03d", random.nextInt(10) + 1)));
            pick.setWarehouseName(generateSafeString(WAREHOUSE_NAMES[random.nextInt(WAREHOUSE_NAMES.length)]));
            pick.setPickerId((long) (random.nextInt(20) + 1));
            pick.setPickerName(generateSafeString(OPERATOR_NAMES[random.nextInt(OPERATOR_NAMES.length)]));
            pick.setBatchNo(generateSafeString(BATCH_NOS[random.nextInt(BATCH_NOS.length)]));
            pick.setRemark(generateSafeString(REMARKS[random.nextInt(REMARKS.length)]));

            int status = random.nextInt(5) + 1;
            pick.setStatus(status);
            pick.setIsDeleted(0);
            pick.setCreateBy("system");
            pick.setCreateTime(pickDate);
            pick.setUpdateBy("system");
            pick.setUpdateTime(pickDate.plusHours(random.nextInt(48)));

            picks.add(pick);
        }

        for (ErpMaterialPick pick : picks) {
            materialPickMapper.insert(pick);
        }

        log.info("成功生成{}条领料单测试数据", picks.size());
    }

    private void generateMaterialReturnData() {
        log.info("开始生成退补料单测试数据...");

        LambdaQueryWrapper<ErpMaterialReturn> countWrapper = new LambdaQueryWrapper<>();
        Long existingCount = materialReturnMapper.selectCount(countWrapper);

        if (existingCount != null && existingCount >= 30) {
            log.info("退补料单数据已存在({}条)，跳过生成", existingCount);
            return;
        }

        List<ErpMaterialReturn> returns = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (int i = 0; i < 35; i++) {
            ErpMaterialReturn ret = new ErpMaterialReturn();
            ret.setReturnNo(generateSafeString("MR" + dtf.format(baseTime.plusHours(i * 2)) + String.format("%03d", i + 1)));
            ret.setReturnType(random.nextInt(2) + 1);
            ret.setWorkOrderId((long) (random.nextInt(10) + 1));
            ret.setWorkOrderNo(generateSafeString(WORK_ORDER_NOS[random.nextInt(WORK_ORDER_NOS.length)]));
            ret.setWorkOrderName(generateSafeString(WORK_ORDER_NAMES[random.nextInt(WORK_ORDER_NAMES.length)]));
            ret.setPickId((long) (random.nextInt(10) + 1));
            ret.setPickNo(generateSafeString("MP" + dtf.format(baseTime) + String.format("%03d", random.nextInt(100) + 1)));
            ret.setProductId((long) (random.nextInt(20) + 1));
            ret.setProductCode(generateSafeString("PROD" + String.format("%05d", random.nextInt(1000) + 1)));
            ret.setProductName(generateSafeString(WORK_ORDER_NAMES[random.nextInt(WORK_ORDER_NAMES.length)]));
            ret.setMaterialId((long) (random.nextInt(24) + 1));
            ret.setMaterialCode(generateSafeString("MAT" + String.format("%05d", random.nextInt(1000) + 1)));
            ret.setMaterialName(generateSafeString(MATERIAL_NAMES[random.nextInt(MATERIAL_NAMES.length)]));
            ret.setSpecification(generateSafeString(SPECIFICATIONS[random.nextInt(SPECIFICATIONS.length)]));
            ret.setUnit(generateSafeString(UNITS[random.nextInt(UNITS.length)]));

            BigDecimal returnQty = BigDecimal.valueOf(random.nextInt(20) + 1);
            ret.setReturnQuantity(returnQty);
            ret.setReturnValue(returnQty.multiply(BigDecimal.valueOf(random.nextDouble() * 100 + 10)).setScale(4, BigDecimal.ROUND_HALF_UP));
            ret.setReturnReason(generateSafeString(RETURN_REASONS[random.nextInt(RETURN_REASONS.length)]));

            LocalDateTime returnDate = baseTime.plusDays(random.nextInt(25));
            ret.setReturnDate(returnDate.toLocalDate());
            ret.setWarehouseId((long) (random.nextInt(10) + 1));
            ret.setWarehouseCode(generateSafeString("WH" + String.format("%03d", random.nextInt(10) + 1)));
            ret.setWarehouseName(generateSafeString(WAREHOUSE_NAMES[random.nextInt(WAREHOUSE_NAMES.length)]));
            ret.setOperatorId((long) (random.nextInt(20) + 1));
            ret.setOperatorName(generateSafeString(OPERATOR_NAMES[random.nextInt(OPERATOR_NAMES.length)]));
            ret.setBatchNo(generateSafeString(BATCH_NOS[random.nextInt(BATCH_NOS.length)]));
            ret.setRemark(generateSafeString(REMARKS[random.nextInt(REMARKS.length)]));

            int status = random.nextInt(4) + 1;
            ret.setStatus(status);

            if (status >= 3) {
                ret.setApprovalUserId((long) (random.nextInt(5) + 1));
                ret.setApprovalUserName(generateSafeString("审批人" + (random.nextInt(10) + 1)));
                ret.setApprovalTime(returnDate.plusHours(random.nextInt(48)));
                ret.setApprovalOpinion(generateSafeString(APPROVAL_OPINIONS[random.nextInt(APPROVAL_OPINIONS.length)]));
            }

            ret.setIsDeleted(0);
            ret.setCreateBy("system");
            ret.setCreateTime(returnDate);
            ret.setUpdateBy("system");
            ret.setUpdateTime(returnDate.plusHours(random.nextInt(72)));

            returns.add(ret);
        }

        for (ErpMaterialReturn ret : returns) {
            materialReturnMapper.insert(ret);
        }

        log.info("成功生成{}条退补料单测试数据", returns.size());
    }

    private void generateOverPickApprovalData() {
        log.info("开始生成超领审批单测试数据...");

        LambdaQueryWrapper<ErpOverPickApproval> countWrapper = new LambdaQueryWrapper<>();
        Long existingCount = overPickApprovalMapper.selectCount(countWrapper);

        if (existingCount != null && existingCount >= 30) {
            log.info("超领审批单数据已存在({}条)，跳过生成", existingCount);
            return;
        }

        List<ErpOverPickApproval> approvals = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (int i = 0; i < 35; i++) {
            ErpOverPickApproval approval = new ErpOverPickApproval();
            approval.setApprovalNo(generateSafeString("OPA" + dtf.format(baseTime.plusHours(i * 2)) + String.format("%03d", i + 1)));
            approval.setWorkOrderId((long) (random.nextInt(10) + 1));
            approval.setWorkOrderNo(generateSafeString(WORK_ORDER_NOS[random.nextInt(WORK_ORDER_NOS.length)]));
            approval.setWorkOrderName(generateSafeString(WORK_ORDER_NAMES[random.nextInt(WORK_ORDER_NAMES.length)]));
            approval.setPickId((long) (random.nextInt(10) + 1));
            approval.setPickNo(generateSafeString("MP" + dtf.format(baseTime) + String.format("%03d", random.nextInt(100) + 1)));
            approval.setProductId((long) (random.nextInt(20) + 1));
            approval.setProductCode(generateSafeString("PROD" + String.format("%05d", random.nextInt(1000) + 1)));
            approval.setProductName(generateSafeString(WORK_ORDER_NAMES[random.nextInt(WORK_ORDER_NAMES.length)]));
            approval.setMaterialId((long) (random.nextInt(24) + 1));
            approval.setMaterialCode(generateSafeString("MAT" + String.format("%05d", random.nextInt(1000) + 1)));
            approval.setMaterialName(generateSafeString(MATERIAL_NAMES[random.nextInt(MATERIAL_NAMES.length)]));
            approval.setSpecification(generateSafeString(SPECIFICATIONS[random.nextInt(SPECIFICATIONS.length)]));
            approval.setUnit(generateSafeString(UNITS[random.nextInt(UNITS.length)]));

            BigDecimal planQty = BigDecimal.valueOf(random.nextInt(100) + 10);
            BigDecimal overPickQty = planQty.multiply(BigDecimal.valueOf(random.nextDouble() * 0.5 + 0.1));
            BigDecimal totalQty = planQty.add(overPickQty);

            approval.setPlanQuantity(planQty);
            approval.setOverPickQuantity(overPickQty.setScale(4, BigDecimal.ROUND_HALF_UP));
            approval.setTotalQuantity(totalQty.setScale(4, BigDecimal.ROUND_HALF_UP));
            approval.setOverPickReason(generateSafeString(OVER_PICK_REASONS[random.nextInt(OVER_PICK_REASONS.length)]));
            approval.setApplicantId((long) (random.nextInt(20) + 1));
            approval.setApplicantName(generateSafeString(OPERATOR_NAMES[random.nextInt(OPERATOR_NAMES.length)]));

            LocalDateTime appTime = baseTime.plusDays(random.nextInt(25));
            approval.setApplicationTime(appTime);

            int status = random.nextInt(5) + 1;
            approval.setStatus(status);

            if (status >= 3) {
                approval.setApprovalUserId((long) (random.nextInt(5) + 1));
                approval.setApprovalUserName(generateSafeString("审批人" + (random.nextInt(10) + 1)));
                approval.setApprovalTime(appTime.plusHours(random.nextInt(48)));
                approval.setApprovalOpinion(generateSafeString(APPROVAL_OPINIONS[random.nextInt(APPROVAL_OPINIONS.length)]));
            }

            approval.setRemark(generateSafeString(REMARKS[random.nextInt(REMARKS.length)]));
            approval.setIsDeleted(0);
            approval.setCreateBy("system");
            approval.setCreateTime(appTime);
            approval.setUpdateBy("system");
            approval.setUpdateTime(appTime.plusHours(random.nextInt(72)));

            approvals.add(approval);
        }

        for (ErpOverPickApproval approval : approvals) {
            overPickApprovalMapper.insert(approval);
        }

        log.info("成功生成{}条超领审批单测试数据", approvals.size());
    }

    private String generateSafeString(String input) {
        if (input == null) {
            return null;
        }
        String safe = new String(input.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        return safe.length() > 255 ? safe.substring(0, 255) : safe;
    }
}
