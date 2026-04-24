package com.erp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.dto.GanttDragDTO;
import com.erp.dto.GanttQueryDTO;
import com.erp.dto.MpsCreateDTO;
import com.erp.entity.*;
import com.erp.mapper.*;
import com.erp.vo.GanttChartVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GanttChartService {

    private final MpsMapper mpsMapper;
    private final EquipmentMapper equipmentMapper;
    private final ProductMapper productMapper;
    private final WorkGroupMapper workGroupMapper;
    private final MpsService mpsService;

    public GanttChartVO.ChartDataVO getGanttDataByEquipment(GanttQueryDTO queryDTO) {
        LocalDate startDate = queryDTO.getStartDate() != null ? queryDTO.getStartDate() : LocalDate.now().minusDays(7);
        LocalDate endDate = queryDTO.getEndDate() != null ? queryDTO.getEndDate() : LocalDate.now().plusDays(30);
        
        List<ErpMps> mpsList = queryMpsList(queryDTO);
        
        List<ErpEquipment> equipments = equipmentMapper.selectList(
            new LambdaQueryWrapper<ErpEquipment>()
                .eq(ErpEquipment::getStatus, 1)
                .orderByAsc(ErpEquipment::getId)
        );
        
        LocalDate minDate = startDate;
        LocalDate maxDate = endDate;
        for (ErpMps mps : mpsList) {
            if (mps.getPlanStartDate() != null && mps.getPlanStartDate().isBefore(minDate)) {
                minDate = mps.getPlanStartDate();
            }
            if (mps.getPlanEndDate() != null && mps.getPlanEndDate().isAfter(maxDate)) {
                maxDate = mps.getPlanEndDate();
            }
        }
        
        List<GanttChartVO.TaskVO> allTasks = convertToTaskVOList(mpsList);
        
        Map<Long, List<ErpMps>> mpsByEquipment = mpsList.stream()
            .collect(Collectors.groupingBy(mps -> 
                mps.getEquipmentId() != null ? mps.getEquipmentId() : -1L
            ));
        
        List<GanttChartVO> resources = new ArrayList<>();
        for (ErpEquipment equipment : equipments) {
            List<ErpMps> mpsForEquipment = mpsByEquipment.getOrDefault(equipment.getId(), Collections.emptyList());
            List<GanttChartVO.TaskVO> tasks = convertToTaskVOList(mpsForEquipment);
            
            GanttChartVO resource = GanttChartVO.builder()
                .resourceType("EQUIPMENT")
                .resourceId(equipment.getId())
                .resourceCode(equipment.getEquipmentCode())
                .resourceName(equipment.getEquipmentName())
                .resourceTypeDesc(equipment.getEquipmentType())
                .tasks(tasks)
                .build();
            resources.add(resource);
        }
        
        List<ErpMps> unassignedMps = mpsByEquipment.getOrDefault(-1L, Collections.emptyList());
        if (!unassignedMps.isEmpty()) {
            GanttChartVO unassignedResource = GanttChartVO.builder()
                .resourceType("UNASSIGNED")
                .resourceId(-1L)
                .resourceCode("UNASSIGNED")
                .resourceName("未分配设备")
                .resourceTypeDesc("待分配")
                .tasks(convertToTaskVOList(unassignedMps))
                .build();
            resources.add(0, unassignedResource);
        }
        
        GanttChartVO.GroupVO group = GanttChartVO.GroupVO.builder()
            .groupType("EQUIPMENT")
            .groupTypeName("按设备分组")
            .resources(resources)
            .build();
        
        return GanttChartVO.ChartDataVO.builder()
            .minDate(minDate.minusDays(1))
            .maxDate(maxDate.plusDays(1))
            .groups(Collections.singletonList(group))
            .allTasks(allTasks)
            .build();
    }

    public GanttChartVO.ChartDataVO getGanttDataByProduct(GanttQueryDTO queryDTO) {
        List<ErpMps> mpsList = queryMpsList(queryDTO);
        
        List<ErpProduct> products = productMapper.selectList(
            new LambdaQueryWrapper<ErpProduct>()
                .eq(ErpProduct::getStatus, 1)
                .orderByAsc(ErpProduct::getId)
        );
        
        Map<Long, List<ErpMps>> mpsByProduct = mpsList.stream()
            .collect(Collectors.groupingBy(mps -> 
                mps.getProductId() != null ? mps.getProductId() : -1L
            ));
        
        LocalDate minDate = LocalDate.now();
        LocalDate maxDate = LocalDate.now();
        for (ErpMps mps : mpsList) {
            if (mps.getPlanStartDate() != null && mps.getPlanStartDate().isBefore(minDate)) {
                minDate = mps.getPlanStartDate();
            }
            if (mps.getPlanEndDate() != null && mps.getPlanEndDate().isAfter(maxDate)) {
                maxDate = mps.getPlanEndDate();
            }
        }
        
        List<GanttChartVO.TaskVO> allTasks = convertToTaskVOList(mpsList);
        
        List<GanttChartVO> resources = new ArrayList<>();
        for (ErpProduct product : products) {
            List<ErpMps> mpsForProduct = mpsByProduct.getOrDefault(product.getId(), Collections.emptyList());
            if (mpsForProduct.isEmpty()) continue;
            
            List<GanttChartVO.TaskVO> tasks = convertToTaskVOList(mpsForProduct);
            
            GanttChartVO resource = GanttChartVO.builder()
                .resourceType("PRODUCT")
                .resourceId(product.getId())
                .resourceCode(product.getProductCode())
                .resourceName(product.getProductName())
                .resourceTypeDesc(product.getCategory())
                .tasks(tasks)
                .build();
            resources.add(resource);
        }
        
        GanttChartVO.GroupVO group = GanttChartVO.GroupVO.builder()
            .groupType("PRODUCT")
            .groupTypeName("按产品分组")
            .resources(resources)
            .build();
        
        return GanttChartVO.ChartDataVO.builder()
            .minDate(minDate.minusDays(1))
            .maxDate(maxDate.plusDays(1))
            .groups(Collections.singletonList(group))
            .allTasks(allTasks)
            .build();
    }

    public GanttChartVO.ChartDataVO getGanttDataByPriority(GanttQueryDTO queryDTO) {
        List<ErpMps> mpsList = queryMpsList(queryDTO);
        
        Map<Integer, List<ErpMps>> mpsByPriority = mpsList.stream()
            .collect(Collectors.groupingBy(mps -> 
                mps.getPriority() != null ? mps.getPriority() : 2
            ));
        
        LocalDate minDate = LocalDate.now();
        LocalDate maxDate = LocalDate.now();
        for (ErpMps mps : mpsList) {
            if (mps.getPlanStartDate() != null && mps.getPlanStartDate().isBefore(minDate)) {
                minDate = mps.getPlanStartDate();
            }
            if (mps.getPlanEndDate() != null && mps.getPlanEndDate().isAfter(maxDate)) {
                maxDate = mps.getPlanEndDate();
            }
        }
        
        List<GanttChartVO.TaskVO> allTasks = convertToTaskVOList(mpsList);
        
        Map<Integer, String> priorityNames = new HashMap<>();
        priorityNames.put(1, "高优先级");
        priorityNames.put(2, "中优先级");
        priorityNames.put(3, "低优先级");
        
        List<GanttChartVO> resources = new ArrayList<>();
        for (int priority : Arrays.asList(1, 2, 3)) {
            List<ErpMps> mpsForPriority = mpsByPriority.getOrDefault(priority, Collections.emptyList());
            
            List<GanttChartVO.TaskVO> tasks = convertToTaskVOList(mpsForPriority);
            
            GanttChartVO resource = GanttChartVO.builder()
                .resourceType("PRIORITY")
                .resourceId((long) priority)
                .resourceCode("PRIORITY-" + priority)
                .resourceName(priorityNames.get(priority))
                .resourceTypeDesc(priority == 1 ? "紧急" : priority == 2 ? "正常" : "延后")
                .tasks(tasks)
                .build();
            resources.add(resource);
        }
        
        GanttChartVO.GroupVO group = GanttChartVO.GroupVO.builder()
            .groupType("PRIORITY")
            .groupTypeName("按优先级分组")
            .resources(resources)
            .build();
        
        return GanttChartVO.ChartDataVO.builder()
            .minDate(minDate.minusDays(1))
            .maxDate(maxDate.plusDays(1))
            .groups(Collections.singletonList(group))
            .allTasks(allTasks)
            .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void dragAdjustPlan(GanttDragDTO dragDTO) {
        log.info("拖拽调整计划: id={}, 新开始日期={}, 新结束日期={}, 新资源ID={}", 
            dragDTO.getId(), dragDTO.getNewStartDate(), dragDTO.getNewEndDate(), dragDTO.getNewResourceId());
        
        ErpMps mps = mpsMapper.selectById(dragDTO.getId());
        if (mps == null) {
            throw new RuntimeException("计划不存在: " + dragDTO.getId());
        }
        
        if (dragDTO.getNewStartDate() != null) {
            mps.setPlanStartDate(dragDTO.getNewStartDate());
        }
        if (dragDTO.getNewEndDate() != null) {
            mps.setPlanEndDate(dragDTO.getNewEndDate());
        }
        if (dragDTO.getNewResourceId() != null && dragDTO.getNewResourceId() > 0) {
            ErpEquipment equipment = equipmentMapper.selectById(dragDTO.getNewResourceId());
            if (equipment != null) {
                mps.setEquipmentId(equipment.getId());
                mps.setEquipmentCode(equipment.getEquipmentCode());
                mps.setEquipmentName(equipment.getEquipmentName());
            }
        }
        if (dragDTO.getNewPriority() != null) {
            mps.setPriority(dragDTO.getNewPriority());
        }
        
        mpsMapper.updateById(mps);
        
        log.info("计划更新完成: id={}", mps.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void reschedule(GanttDragDTO dragDTO) {
        log.info("重新排程: id={}", dragDTO.getId());
        
        ErpMps changedMps = mpsMapper.selectById(dragDTO.getId());
        if (changedMps == null) {
            return;
        }
        
        if (dragDTO.getNewStartDate() != null) {
            changedMps.setPlanStartDate(dragDTO.getNewStartDate());
        }
        if (dragDTO.getNewEndDate() != null) {
            changedMps.setPlanEndDate(dragDTO.getNewEndDate());
        }
        if (dragDTO.getNewResourceId() != null && dragDTO.getNewResourceId() > 0) {
            ErpEquipment equipment = equipmentMapper.selectById(dragDTO.getNewResourceId());
            if (equipment != null) {
                changedMps.setEquipmentId(equipment.getId());
                changedMps.setEquipmentCode(equipment.getEquipmentCode());
                changedMps.setEquipmentName(equipment.getEquipmentName());
            }
        }
        mpsMapper.updateById(changedMps);
        
        adjustSubsequentPlans(changedMps);
        
        log.info("重新排程完成: id={}", changedMps.getId());
    }

    private void adjustSubsequentPlans(ErpMps changedMps) {
        if (changedMps.getEquipmentId() == null) {
            return;
        }
        
        List<ErpMps> relatedPlans = mpsMapper.selectList(
            new LambdaQueryWrapper<ErpMps>()
                .eq(ErpMps::getEquipmentId, changedMps.getEquipmentId())
                .in(ErpMps::getStatus, Arrays.asList(1, 2, 3))
                .orderByAsc(ErpMps::getPlanStartDate)
        );
        
        LocalDate lastEndDate = changedMps.getPlanStartDate();
        
        for (ErpMps plan : relatedPlans) {
            if (plan.getId().equals(changedMps.getId())) {
                if (plan.getPlanEndDate() != null) {
                    lastEndDate = plan.getPlanEndDate();
                }
                continue;
            }
            
            if (plan.getPlanStartDate() != null && plan.getPlanStartDate().isBefore(lastEndDate.plusDays(1))) {
                LocalDate newStartDate = lastEndDate.plusDays(1);
                if (plan.getPlanStartDate() != null && plan.getPlanEndDate() != null) {
                    long days = plan.getPlanEndDate().toEpochDay() - plan.getPlanStartDate().toEpochDay();
                    plan.setPlanStartDate(newStartDate);
                    plan.setPlanEndDate(newStartDate.plusDays(days));
                    mpsMapper.updateById(plan);
                    
                    lastEndDate = plan.getPlanEndDate();
                    log.info("调整后续计划: id={}, 新开始日期={}, 新结束日期={}", 
                        plan.getId(), plan.getPlanStartDate(), plan.getPlanEndDate());
                }
            } else {
                if (plan.getPlanEndDate() != null) {
                    lastEndDate = plan.getPlanEndDate();
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void rescheduleAll() {
        log.info("开始全局重新排程...");
        
        List<ErpEquipment> equipments = equipmentMapper.selectList(
            new LambdaQueryWrapper<ErpEquipment>()
                .eq(ErpEquipment::getStatus, 1)
        );
        
        for (ErpEquipment equipment : equipments) {
            List<ErpMps> plans = mpsMapper.selectList(
                new LambdaQueryWrapper<ErpMps>()
                    .eq(ErpMps::getEquipmentId, equipment.getId())
                    .in(ErpMps::getStatus, Arrays.asList(1, 2, 3))
                    .orderByAsc(ErpMps::getPriority)
                    .orderByAsc(ErpMps::getPlanStartDate)
            );
            
            LocalDate currentDate = LocalDate.now();
            
            for (ErpMps plan : plans) {
                if (plan.getPlanStartDate() != null && plan.getPlanEndDate() != null) {
                    if (plan.getPlanStartDate().isBefore(currentDate)) {
                        long days = plan.getPlanEndDate().toEpochDay() - plan.getPlanStartDate().toEpochDay();
                        plan.setPlanStartDate(currentDate);
                        plan.setPlanEndDate(currentDate.plusDays(days));
                        mpsMapper.updateById(plan);
                    }
                    currentDate = plan.getPlanEndDate().plusDays(1);
                }
            }
        }
        
        log.info("全局重新排程完成");
    }

    @Transactional(rollbackFor = Exception.class)
    public int generateTestData() {
        log.info("开始生成甘特图测试数据...");
        
        List<ErpEquipment> equipments = equipmentMapper.selectList(
            new LambdaQueryWrapper<ErpEquipment>()
                .eq(ErpEquipment::getStatus, 1)
        );
        
        List<ErpProduct> products = productMapper.selectList(
            new LambdaQueryWrapper<ErpProduct>()
                .eq(ErpProduct::getStatus, 1)
        );
        
        List<ErpWorkGroup> workGroups = workGroupMapper.selectList(
            new LambdaQueryWrapper<ErpWorkGroup>()
                .eq(ErpWorkGroup::getStatus, 1)
        );
        
        if (equipments.isEmpty()) {
            throw new RuntimeException("没有可用的设备数据，请先生成设备数据");
        }
        if (products.isEmpty()) {
            throw new RuntimeException("没有可用的产品数据，请先生成产品数据");
        }
        
        int count = 0;
        Random random = new Random();
        LocalDate today = LocalDate.now();
        
        String[] planNames = {
            "春节生产计划", "Q1季度计划", "紧急订单A", "常规生产B", "测试批次C",
            "新产品试制", "订单2026-001", "客户定制X", "批量生产Y", "小批量试产"
        };
        
        for (int i = 1; i <= 40; i++) {
            ErpEquipment equipment = equipments.get(i % equipments.size());
            ErpProduct product = products.get(random.nextInt(products.size()));
            
            String mpsNo = "MPS-GANTT-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                String.format("%05d", i);
            
            String planName = planNames[i % planNames.length] + "-" + i;
            
            int startDayOffset = random.nextInt(15);
            int duration = 1 + random.nextInt(7);
            
            LocalDate startDate = today.plusDays(startDayOffset);
            LocalDate endDate = startDate.plusDays(duration);
            
            int priority = 1 + random.nextInt(3);
            int status = 1 + random.nextInt(4);
            
            MpsCreateDTO createDTO = new MpsCreateDTO();
            createDTO.setMpsNo(mpsNo);
            createDTO.setPlanName(planName);
            createDTO.setPlanType(1);
            createDTO.setProductId(product.getId());
            createDTO.setProductCode(product.getProductCode());
            createDTO.setProductName(product.getProductName());
            createDTO.setSpecification(product.getSpecification());
            createDTO.setUnit(product.getUnit());
            createDTO.setNetRequirement(new BigDecimal(100 + random.nextInt(1000)));
            createDTO.setPlannedQuantity(createDTO.getNetRequirement());
            createDTO.setPlanStartDate(startDate);
            createDTO.setPlanEndDate(endDate);
            createDTO.setEquipmentId(equipment.getId());
            createDTO.setEquipmentCode(equipment.getEquipmentCode());
            createDTO.setEquipmentName(equipment.getEquipmentName());
            
            if (!workGroups.isEmpty()) {
                ErpWorkGroup workGroup = workGroups.get(random.nextInt(workGroups.size()));
                createDTO.setGroupId(workGroup.getId());
                createDTO.setGroupCode(workGroup.getGroupCode());
                createDTO.setGroupName(workGroup.getGroupName());
            }
            
            createDTO.setPriority(priority);
            createDTO.setRemark("甘特图测试数据-" + i + "，用于可视化排程测试");
            createDTO.setStatus(status > 4 ? 1 : status);
            
            try {
                mpsService.createMps(createDTO);
                count++;
                log.info("生成测试计划: {} - {} - 优先级:{} - 状态:{}", 
                    mpsNo, planName, priority, createDTO.getStatus());
            } catch (Exception e) {
                log.warn("生成计划失败: {}", e.getMessage());
            }
        }
        
        log.info("测试数据生成完成，共生成 {} 条计划", count);
        return count;
    }

    private List<ErpMps> queryMpsList(GanttQueryDTO queryDTO) {
        LambdaQueryWrapper<ErpMps> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.in(ErpMps::getStatus, Arrays.asList(1, 2, 3, 4));
        
        if (queryDTO.getStartDate() != null) {
            wrapper.ge(ErpMps::getPlanStartDate, queryDTO.getStartDate());
        }
        if (queryDTO.getEndDate() != null) {
            wrapper.le(ErpMps::getPlanEndDate, queryDTO.getEndDate());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(ErpMps::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getPriority() != null) {
            wrapper.eq(ErpMps::getPriority, queryDTO.getPriority());
        }
        if (queryDTO.getEquipmentCode() != null && !queryDTO.getEquipmentCode().isEmpty()) {
            wrapper.like(ErpMps::getEquipmentCode, queryDTO.getEquipmentCode());
        }
        if (queryDTO.getProductCode() != null && !queryDTO.getProductCode().isEmpty()) {
            wrapper.like(ErpMps::getProductCode, queryDTO.getProductCode());
        }
        if (queryDTO.getMpsNo() != null && !queryDTO.getMpsNo().isEmpty()) {
            wrapper.like(ErpMps::getMpsNo, queryDTO.getMpsNo());
        }
        
        wrapper.orderByAsc(ErpMps::getPriority)
               .orderByAsc(ErpMps::getPlanStartDate);
        
        return mpsMapper.selectList(wrapper);
    }

    private List<GanttChartVO.TaskVO> convertToTaskVOList(List<ErpMps> mpsList) {
        return mpsList.stream()
            .map(this::convertToTaskVO)
            .collect(Collectors.toList());
    }

    private GanttChartVO.TaskVO convertToTaskVO(ErpMps mps) {
        String priorityName = switch (mps.getPriority() == null ? 2 : mps.getPriority()) {
            case 1 -> "高";
            case 2 -> "中";
            case 3 -> "低";
            default -> "未知";
        };
        
        String statusName = switch (mps.getStatus() == null ? 1 : mps.getStatus()) {
            case 1 -> "草稿";
            case 2 -> "已确认";
            case 3 -> "生产中";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
        
        String color = switch (mps.getStatus() == null ? 1 : mps.getStatus()) {
            case 1 -> "#909399";
            case 2 -> "#409EFF";
            case 3 -> "#E6A23C";
            case 4 -> "#67C23A";
            case 5 -> "#F56C6C";
            default -> "#909399";
        };
        
        int progress = 0;
        if (mps.getStatus() != null) {
            if (mps.getStatus() == 4) {
                progress = 100;
            } else if (mps.getStatus() == 3) {
                progress = 50;
            } else if (mps.getStatus() == 2) {
                progress = 10;
            }
        }
        
        return GanttChartVO.TaskVO.builder()
            .id(mps.getId())
            .taskNo(mps.getMpsNo())
            .taskName(mps.getPlanName())
            .productId(mps.getProductId())
            .productCode(mps.getProductCode())
            .productName(mps.getProductName())
            .specification(mps.getSpecification())
            .plannedQuantity(mps.getPlannedQuantity())
            .actualQuantity(mps.getNetRequirement())
            .planStartDate(mps.getPlanStartDate())
            .planEndDate(mps.getPlanEndDate())
            .actualStartDate(mps.getActualStartDate())
            .actualEndDate(mps.getActualEndDate())
            .resourceId(mps.getEquipmentId())
            .resourceCode(mps.getEquipmentCode())
            .resourceName(mps.getEquipmentName())
            .priority(mps.getPriority())
            .priorityName(priorityName)
            .status(mps.getStatus())
            .statusName(statusName)
            .progress(progress)
            .color(color)
            .remark(mps.getRemark())
            .build();
    }
}
