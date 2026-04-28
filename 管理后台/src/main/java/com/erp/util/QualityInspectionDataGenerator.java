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
@Order(11)
@RequiredArgsConstructor
public class QualityInspectionDataGenerator implements ApplicationRunner {

    private final IqcInspectionMapper iqcInspectionMapper;
    private final IpqcInspectionMapper ipqcInspectionMapper;
    private final FqcInspectionMapper fqcInspectionMapper;
    private final JdbcTemplate jdbcTemplate;

    private final Random random = new Random();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String[] SUPPLIER_NAMES = {
            "深圳市华为技术有限公司", "阿里巴巴集团", "腾讯科技有限公司", "百度在线网络技术有限公司",
            "京东集团", "小米科技有限公司", "OPPO广东移动通信有限公司", "维沃移动通信有限公司",
            "中兴通讯股份有限公司", "联想集团有限公司", "海尔集团公司", "美的集团股份有限公司",
            "格力电器股份有限公司", "比亚迪股份有限公司", "宁德时代新能源科技股份有限公司"
    };

    private static final String[] MATERIAL_NAMES = {
            "铝合金板材-6061", "不锈钢板材-304", "碳钢板材-Q235", "铜材-C1100", "铝材-6063",
            "塑料-ABS", "塑料-PC", "橡胶-天然橡胶", "电子元件-电阻10KΩ", "电子元件-电容100μF",
            "电子元件-二极管1N4007", "电子元件-三极管NPN", "电子元件-IC芯片STM32",
            "电子元件-连接器2.54mm", "螺丝-M3*10", "螺丝-M4*15", "螺母-M3", "螺母-M4",
            "垫片-Φ10", "轴承-6001", "齿轮-模数2", "弹簧-钢丝直径1mm", "密封圈-Φ20",
            "润滑剂-润滑油", "PCB板-双层板", "PCB板-四层板", "PCB板-六层板",
            "集成电路芯片-STM32F103", "集成电路芯片-ATMEGA328", "集成电路芯片-ESP32",
            "传感器-温度传感器", "传感器-压力传感器", "传感器-湿度传感器", "传感器-位移传感器"
    };

    private static final String[] PRODUCT_NAMES = {
            "智能手机-PRO-X", "智能手机-LITE-X", "笔记本电脑-ULTRA", "笔记本电脑-GAMING",
            "平板电脑-10寸", "平板电脑-12寸", "智能手表-标准版", "智能手表-运动版",
            "蓝牙耳机-TWS", "蓝牙耳机-头戴式", "智能音箱-标准版", "智能音箱-高配版",
            "智能门锁-指纹版", "智能门锁-人脸识别", "智能摄像头-室内版", "智能摄像头-室外版",
            "智能电视-55寸", "智能电视-65寸", "智能冰箱-对开门", "智能冰箱-多门",
            "智能洗衣机-滚筒", "智能洗衣机-波轮", "智能空调-挂壁式", "智能空调-柜式",
            "汽车导航系统", "汽车仪表盘", "汽车中控系统", "汽车娱乐系统",
            "医疗设备-监护仪", "医疗设备-心电图机", "医疗设备-血压计", "医疗设备-血糖仪"
    };

    private static final String[] PROCESS_NAMES = {
            "冲压工序", "焊接工序", "机加工工序", "热处理工序", "表面处理工序",
            "SMT贴片工序", "插件工序", "焊接工序", "测试工序", "组装工序",
            "包装工序", "检验工序", "入库工序", "出库工序", "返修工序",
            "CNC加工工序", "注塑成型工序", "喷涂工序", "电镀工序", "氧化工序",
            "蚀刻工序", "清洗工序", "干燥工序", "固化工序", "检测工序"
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
            "波峰焊-WF-001", "波峰焊-WF-002",
            "AOI检测设备-AOI-001", "X-Ray检测设备-XRAY-001",
            "ICT测试设备-ICT-001", "FCT测试设备-FCT-001"
    };

    private static final String[] INSPECTOR_NAMES = {
            "质检员-张三", "质检员-李四", "质检员-王五", "质检员-赵六", "质检员-钱七",
            "质检员-孙八", "质检员-周九", "质检员-吴十", "质检员-郑十一", "质检员-王十二",
            "质检员-刘十三", "质检员-陈十四", "质检员-杨十五", "质检员-黄十六", "质检员-朱十七",
            "质检员-林十八", "质检员-何十九", "质检员-高二十", "质检员-梁廿一", "质检员-宋廿二"
    };

    private static final String[] SPECIFICATIONS = {
            "厚度:2mm", "厚度:3mm", "厚度:5mm", "直径:10mm", "直径:20mm",
            "直径:30mm", "长度:100mm", "长度:200mm", "长度:300mm",
            "宽度:50mm", "宽度:100mm", "宽度:150mm",
            "精度:±0.01mm", "精度:±0.05mm", "精度:±0.1mm",
            "型号:TYPE-A", "型号:TYPE-B", "型号:TYPE-C",
            "等级:A级", "等级:B级", "等级:合格级",
            "表面处理:阳极氧化", "表面处理:电镀镍", "表面处理:喷漆",
            "材质:铝合金", "材质:不锈钢", "材质:工程塑料"
    };

    private static final String[] UNITS = {"个", "件", "套", "千克", "米", "平方米", "立方米", "升", "箱", "台", "PCS", "KG", "M"};

    private static final String[] BATCH_NOS = {
            "B202401001", "B202401002", "B202401003", "B202402001", "B202402002",
            "B202402003", "B202403001", "B202403002", "B202403003", "B202404001",
            "B202404002", "B202404003", "B202405001", "B202405002", "B202405003",
            "B202406001", "B202406002", "B202406003", "B202407001", "B202407002"
    };

    private static final String[] WORK_ORDER_NOS = {
            "WO202401001", "WO202401002", "WO202401003", "WO202401004", "WO202401005",
            "WO202402001", "WO202402002", "WO202402003", "WO202402004", "WO202402005",
            "WO202403001", "WO202403002", "WO202403003", "WO202404001", "WO202404002"
    };

    private static final String[] PURCHASE_ORDER_NOS = {
            "PO202401001", "PO202401002", "PO202401003", "PO202402001", "PO202402002",
            "PO202402003", "PO202403001", "PO202403002", "PO202403003", "PO202404001",
            "PO202404002", "PO202404003", "PO202405001", "PO202405002", "PO202405003"
    };

    private static final String[] INSPECTION_STANDARDS = {
            "GB/T 19001-2016 质量管理体系", "GB/T 2828.1-2012 计数抽样检验程序",
            "GB/T 2829-2002 周期检验计数抽样程序", "GB/T 19580-2012 卓越绩效评价准则",
            "ISO 9001:2015 Quality Management", "企业内部标准-QC-001",
            "企业内部标准-QC-002", "客户指定检验标准", "行业标准-QC-IND-001"
    };

    private static final String[] REMARKS = {
            "正常检验", "紧急订单检验", "特采检验", "返工后复检", "首批检验",
            "常规检验", "年度审核检验", "客户投诉复检", "质量异常追踪", "供应商质量评估",
            "生产过程巡检", "成品入库检验", "发货前抽检", "定期质量抽查", "特殊要求检验"
    };

    private static final String[] DISPOSAL_TYPES = {"退货", "特采", "返工", "报废"};
    private static final String[] NEXT_PROCESS_STATUSES = {"放行", "扣留", "返工"};
    private static final String[] INSPECTION_TYPES = {"全检", "抽检"};
    private static final String[] INSPECTION_RESULTS = {"合格", "不合格"};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== 开始生成质量检验管理测试数据 ===");

        try {
            String tableCheckSql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name IN (?, ?, ?)";
            Integer tableCount = jdbcTemplate.queryForObject(tableCheckSql, Integer.class,
                    "erp_iqc_inspection", "erp_ipqc_inspection", "erp_fqc_inspection");

            if (tableCount == null || tableCount < 3) {
                log.warn("数据库表不存在，跳过测试数据生成");
                return;
            }

            generateIqcInspectionData();
            generateIpqcInspectionData();
            generateFqcInspectionData();

            log.info("=== 质量检验管理测试数据生成完成 ===");
        } catch (Exception e) {
            log.error("测试数据生成失败", e);
            throw e;
        }
    }

    private void generateIqcInspectionData() {
        log.info("开始生成来料检验 (IQC) 测试数据...");

        LambdaQueryWrapper<ErpIqcInspection> countWrapper = new LambdaQueryWrapper<>();
        Long existingCount = iqcInspectionMapper.selectCount(countWrapper);

        if (existingCount != null && existingCount >= 30) {
            log.info("来料检验数据已存在({}条)，跳过生成", existingCount);
            return;
        }

        List<ErpIqcInspection> inspections = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (int i = 0; i < 35; i++) {
            ErpIqcInspection inspection = new ErpIqcInspection();
            inspection.setInspectionNo(generateSafeString("IQC" + dtf.format(baseTime.plusHours(i * 2)) + String.format("%03d", i + 1)));
            inspection.setPurchaseOrderId((long) (random.nextInt(15) + 1));
            inspection.setPurchaseOrderNo(generateSafeString(PURCHASE_ORDER_NOS[random.nextInt(PURCHASE_ORDER_NOS.length)]));
            inspection.setSupplierId((long) (random.nextInt(15) + 1));
            inspection.setSupplierCode(generateSafeString("SUP" + String.format("%05d", random.nextInt(1000) + 1)));
            inspection.setSupplierName(generateSafeString(SUPPLIER_NAMES[random.nextInt(SUPPLIER_NAMES.length)]));
            inspection.setMaterialId((long) (random.nextInt(35) + 1));
            inspection.setMaterialCode(generateSafeString("MAT" + String.format("%05d", random.nextInt(1000) + 1)));
            inspection.setMaterialName(generateSafeString(MATERIAL_NAMES[random.nextInt(MATERIAL_NAMES.length)]));
            inspection.setSpecification(generateSafeString(SPECIFICATIONS[random.nextInt(SPECIFICATIONS.length)]));
            inspection.setUnit(generateSafeString(UNITS[random.nextInt(UNITS.length)]));
            inspection.setBatchNo(generateSafeString(BATCH_NOS[random.nextInt(BATCH_NOS.length)]));

            BigDecimal receivedQty = BigDecimal.valueOf(random.nextInt(500) + 50);
            inspection.setReceivedQuantity(receivedQty);
            inspection.setSampleQuantity(BigDecimal.valueOf(random.nextInt(50) + 10));
            inspection.setInspectionType(generateSafeString(INSPECTION_TYPES[random.nextInt(INSPECTION_TYPES.length)]));
            inspection.setInspectionStandard(generateSafeString(INSPECTION_STANDARDS[random.nextInt(INSPECTION_STANDARDS.length)]));

            LocalDateTime inspectTime = baseTime.plusDays(random.nextInt(25)).plusHours(random.nextInt(12));
            inspection.setInspectionDate(inspectTime);
            inspection.setInspectorId((long) (random.nextInt(20) + 1));
            inspection.setInspectorName(generateSafeString(INSPECTOR_NAMES[random.nextInt(INSPECTOR_NAMES.length)]));

            int sampleQty = random.nextInt(100) + 10;
            int unqualifiedQty = random.nextInt(sampleQty / 5);
            int qualifiedQty = sampleQty - unqualifiedQty;
            inspection.setQualifiedCount(qualifiedQty);
            inspection.setUnqualifiedCount(unqualifiedQty);
            inspection.setQualifiedRate(BigDecimal.valueOf((double) qualifiedQty / sampleQty * 100).setScale(2, BigDecimal.ROUND_HALF_UP));

            String result = unqualifiedQty > 0 ? "不合格" : "合格";
            inspection.setInspectionResult(generateSafeString(result));

            int status = random.nextInt(6) + 1;
            inspection.setStatus(status);

            if (status >= 4 && "不合格".equals(result)) {
                inspection.setDisposalType(generateSafeString(DISPOSAL_TYPES[random.nextInt(DISPOSAL_TYPES.length)]));
                inspection.setDisposalRemark(generateSafeString("不合格品处理意见：" + REMARKS[random.nextInt(REMARKS.length)]));
                inspection.setDisposalDate(inspectTime.plusHours(random.nextInt(48)));
                inspection.setDisposalUserId((long) (random.nextInt(10) + 1));
                inspection.setDisposalUserName(generateSafeString("处理人-" + (random.nextInt(20) + 1)));
            }

            if (random.nextBoolean()) {
                inspection.setWorkOrderId((long) (random.nextInt(15) + 1));
                inspection.setWorkOrderNo(generateSafeString(WORK_ORDER_NOS[random.nextInt(WORK_ORDER_NOS.length)]));
            }

            inspection.setRemark(generateSafeString(REMARKS[random.nextInt(REMARKS.length)]));
            inspection.setIsDeleted(0);
            inspection.setCreateBy("system");
            inspection.setCreateTime(inspectTime.minusHours(random.nextInt(24) + 1));
            inspection.setUpdateBy("system");
            inspection.setUpdateTime(inspectTime.plusHours(random.nextInt(72)));

            inspections.add(inspection);
        }

        for (ErpIqcInspection inspection : inspections) {
            iqcInspectionMapper.insert(inspection);
        }

        log.info("成功生成{}条来料检验 (IQC) 测试数据", inspections.size());
    }

    private void generateIpqcInspectionData() {
        log.info("开始生成过程检验 (IPQC) 测试数据...");

        LambdaQueryWrapper<ErpIpqcInspection> countWrapper = new LambdaQueryWrapper<>();
        Long existingCount = ipqcInspectionMapper.selectCount(countWrapper);

        if (existingCount != null && existingCount >= 30) {
            log.info("过程检验数据已存在({}条)，跳过生成", existingCount);
            return;
        }

        List<ErpIpqcInspection> inspections = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (int i = 0; i < 35; i++) {
            ErpIpqcInspection inspection = new ErpIpqcInspection();
            inspection.setInspectionNo(generateSafeString("IPQC" + dtf.format(baseTime.plusHours(i * 2)) + String.format("%03d", i + 1)));
            inspection.setWorkOrderId((long) (random.nextInt(15) + 1));
            inspection.setWorkOrderNo(generateSafeString(WORK_ORDER_NOS[random.nextInt(WORK_ORDER_NOS.length)]));
            inspection.setProductId((long) (random.nextInt(30) + 1));
            inspection.setProductCode(generateSafeString("PROD" + String.format("%05d", random.nextInt(1000) + 1)));
            inspection.setProductName(generateSafeString(PRODUCT_NAMES[random.nextInt(PRODUCT_NAMES.length)]));
            inspection.setSpecification(generateSafeString(SPECIFICATIONS[random.nextInt(SPECIFICATIONS.length)]));
            inspection.setUnit(generateSafeString(UNITS[random.nextInt(UNITS.length)]));
            inspection.setProcessId((long) (random.nextInt(25) + 1));
            inspection.setProcessCode(generateSafeString("PROC" + String.format("%03d", random.nextInt(100) + 1)));
            inspection.setProcessName(generateSafeString(PROCESS_NAMES[random.nextInt(PROCESS_NAMES.length)]));
            inspection.setProcessSequence(random.nextInt(10) + 1);
            inspection.setEquipmentId((long) (random.nextInt(28) + 1));
            inspection.setEquipmentCode(generateSafeString("EQP" + String.format("%03d", random.nextInt(100) + 1)));
            inspection.setEquipmentName(generateSafeString(EQUIPMENT_NAMES[random.nextInt(EQUIPMENT_NAMES.length)]));
            inspection.setBatchNo(generateSafeString(BATCH_NOS[random.nextInt(BATCH_NOS.length)]));

            BigDecimal productionQty = BigDecimal.valueOf(random.nextInt(200) + 20);
            inspection.setProductionQuantity(productionQty);
            inspection.setInspectionQuantity(BigDecimal.valueOf(random.nextInt(50) + 10));
            inspection.setInspectionType(generateSafeString(INSPECTION_TYPES[random.nextInt(INSPECTION_TYPES.length)]));
            inspection.setInspectionStandard(generateSafeString(INSPECTION_STANDARDS[random.nextInt(INSPECTION_STANDARDS.length)]));

            LocalDateTime inspectTime = baseTime.plusDays(random.nextInt(25)).plusHours(random.nextInt(12));
            inspection.setInspectionDate(inspectTime);
            inspection.setInspectorId((long) (random.nextInt(20) + 1));
            inspection.setInspectorName(generateSafeString(INSPECTOR_NAMES[random.nextInt(INSPECTOR_NAMES.length)]));

            int sampleQty = random.nextInt(100) + 10;
            int unqualifiedQty = random.nextInt(sampleQty / 6);
            int reworkQty = random.nextInt(unqualifiedQty / 2);
            int scrappedQty = unqualifiedQty - reworkQty;
            int qualifiedQty = sampleQty - unqualifiedQty;
            inspection.setQualifiedCount(qualifiedQty);
            inspection.setUnqualifiedCount(unqualifiedQty);
            inspection.setReworkCount(reworkQty);
            inspection.setScrappedCount(scrappedQty);
            inspection.setQualifiedRate(BigDecimal.valueOf((double) qualifiedQty / sampleQty * 100).setScale(2, BigDecimal.ROUND_HALF_UP));

            String result = unqualifiedQty > 0 ? "不合格" : "合格";
            inspection.setInspectionResult(generateSafeString(result));

            String nextProcessStatus = "合格".equals(result) ? "放行" : NEXT_PROCESS_STATUSES[random.nextInt(NEXT_PROCESS_STATUSES.length)];
            inspection.setNextProcessStatus(generateSafeString(nextProcessStatus));

            int status = random.nextInt(6) + 1;
            inspection.setStatus(status);

            if (status >= 4 && "不合格".equals(result)) {
                inspection.setDisposalType(generateSafeString(DISPOSAL_TYPES[random.nextInt(DISPOSAL_TYPES.length)]));
                inspection.setDisposalRemark(generateSafeString("不合格品处理意见：" + REMARKS[random.nextInt(REMARKS.length)]));
                inspection.setDisposalDate(inspectTime.plusHours(random.nextInt(48)));
                inspection.setDisposalUserId((long) (random.nextInt(10) + 1));
                inspection.setDisposalUserName(generateSafeString("处理人-" + (random.nextInt(20) + 1)));
            }

            inspection.setRemark(generateSafeString(REMARKS[random.nextInt(REMARKS.length)]));
            inspection.setIsDeleted(0);
            inspection.setCreateBy("system");
            inspection.setCreateTime(inspectTime.minusHours(random.nextInt(24) + 1));
            inspection.setUpdateBy("system");
            inspection.setUpdateTime(inspectTime.plusHours(random.nextInt(72)));

            inspections.add(inspection);
        }

        for (ErpIpqcInspection inspection : inspections) {
            ipqcInspectionMapper.insert(inspection);
        }

        log.info("成功生成{}条过程检验 (IPQC) 测试数据", inspections.size());
    }

    private void generateFqcInspectionData() {
        log.info("开始生成成品检验 (FQC) 测试数据...");

        LambdaQueryWrapper<ErpFqcInspection> countWrapper = new LambdaQueryWrapper<>();
        Long existingCount = fqcInspectionMapper.selectCount(countWrapper);

        if (existingCount != null && existingCount >= 30) {
            log.info("成品检验数据已存在({}条)，跳过生成", existingCount);
            return;
        }

        List<ErpFqcInspection> inspections = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusDays(30);

        for (int i = 0; i < 35; i++) {
            ErpFqcInspection inspection = new ErpFqcInspection();
            inspection.setInspectionNo(generateSafeString("FQC" + dtf.format(baseTime.plusHours(i * 2)) + String.format("%03d", i + 1)));
            inspection.setWorkOrderId((long) (random.nextInt(15) + 1));
            inspection.setWorkOrderNo(generateSafeString(WORK_ORDER_NOS[random.nextInt(WORK_ORDER_NOS.length)]));
            inspection.setProductId((long) (random.nextInt(30) + 1));
            inspection.setProductCode(generateSafeString("PROD" + String.format("%05d", random.nextInt(1000) + 1)));
            inspection.setProductName(generateSafeString(PRODUCT_NAMES[random.nextInt(PRODUCT_NAMES.length)]));
            inspection.setSpecification(generateSafeString(SPECIFICATIONS[random.nextInt(SPECIFICATIONS.length)]));
            inspection.setUnit(generateSafeString(UNITS[random.nextInt(UNITS.length)]));
            inspection.setBatchNo(generateSafeString(BATCH_NOS[random.nextInt(BATCH_NOS.length)]));

            BigDecimal finishedQty = BigDecimal.valueOf(random.nextInt(100) + 10);
            inspection.setFinishedQuantity(finishedQty);
            inspection.setInspectionQuantity(BigDecimal.valueOf(random.nextInt(30) + 5));
            inspection.setInspectionType(generateSafeString(INSPECTION_TYPES[random.nextInt(INSPECTION_TYPES.length)]));
            inspection.setInspectionStandard(generateSafeString(INSPECTION_STANDARDS[random.nextInt(INSPECTION_STANDARDS.length)]));

            LocalDateTime inspectTime = baseTime.plusDays(random.nextInt(25)).plusHours(random.nextInt(12));
            inspection.setInspectionDate(inspectTime);
            inspection.setInspectorId((long) (random.nextInt(20) + 1));
            inspection.setInspectorName(generateSafeString(INSPECTOR_NAMES[random.nextInt(INSPECTOR_NAMES.length)]));

            int sampleQty = random.nextInt(50) + 10;
            int unqualifiedQty = random.nextInt(sampleQty / 8);
            int reworkQty = random.nextInt(unqualifiedQty / 2);
            int scrappedQty = unqualifiedQty - reworkQty;
            int qualifiedQty = sampleQty - unqualifiedQty;
            inspection.setQualifiedCount(qualifiedQty);
            inspection.setUnqualifiedCount(unqualifiedQty);
            inspection.setReworkCount(reworkQty);
            inspection.setScrappedCount(scrappedQty);
            inspection.setQualifiedRate(BigDecimal.valueOf((double) qualifiedQty / sampleQty * 100).setScale(2, BigDecimal.ROUND_HALF_UP));

            String result = unqualifiedQty > 0 ? "不合格" : "合格";
            inspection.setInspectionResult(generateSafeString(result));

            int status = random.nextInt(6) + 1;
            inspection.setStatus(status);

            if ("合格".equals(result) && status >= 3) {
                inspection.setCertificateNo(generateSafeString("CERT" + dtf.format(inspectTime) + String.format("%04d", i + 1)));
                inspection.setCertificateDate(inspectTime);
                inspection.setAllowWarehousing(1);
            } else {
                inspection.setAllowWarehousing(0);
            }

            if (status >= 4 && "不合格".equals(result)) {
                inspection.setDisposalType(generateSafeString(DISPOSAL_TYPES[random.nextInt(DISPOSAL_TYPES.length)]));
                inspection.setDisposalRemark(generateSafeString("不合格品处理意见：" + REMARKS[random.nextInt(REMARKS.length)]));
                inspection.setDisposalDate(inspectTime.plusHours(random.nextInt(48)));
                inspection.setDisposalUserId((long) (random.nextInt(10) + 1));
                inspection.setDisposalUserName(generateSafeString("处理人-" + (random.nextInt(20) + 1)));
            }

            inspection.setRemark(generateSafeString(REMARKS[random.nextInt(REMARKS.length)]));
            inspection.setIsDeleted(0);
            inspection.setCreateBy("system");
            inspection.setCreateTime(inspectTime.minusHours(random.nextInt(24) + 1));
            inspection.setUpdateBy("system");
            inspection.setUpdateTime(inspectTime.plusHours(random.nextInt(72)));

            inspections.add(inspection);
        }

        for (ErpFqcInspection inspection : inspections) {
            fqcInspectionMapper.insert(inspection);
        }

        log.info("成功生成{}条成品检验 (FQC) 测试数据", inspections.size());
    }

    private String generateSafeString(String input) {
        if (input == null) {
            return null;
        }
        String safe = new String(input.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        return safe.length() > 255 ? safe.substring(0, 255) : safe;
    }
}
