package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.EmployeeScheduleCreateDTO;
import com.erp.dto.EmployeeScheduleQueryDTO;
import com.erp.dto.EmployeeScheduleUpdateDTO;
import com.erp.entity.ErpEmployeeSchedule;
import com.erp.mapper.EmployeeScheduleMapper;
import com.erp.vo.EmployeeScheduleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeScheduleService {

    private final EmployeeScheduleMapper employeeScheduleMapper;

    public PageResult<EmployeeScheduleVO> pageEmployeeSchedules(EmployeeScheduleQueryDTO queryDTO) {
        Page<ErpEmployeeSchedule> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpEmployeeSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getScheduleNo() != null, ErpEmployeeSchedule::getScheduleNo, queryDTO.getScheduleNo())
               .like(queryDTO.getGroupCode() != null, ErpEmployeeSchedule::getGroupCode, queryDTO.getGroupCode())
               .like(queryDTO.getGroupName() != null, ErpEmployeeSchedule::getGroupName, queryDTO.getGroupName())
               .eq(queryDTO.getScheduleDate() != null, ErpEmployeeSchedule::getScheduleDate, queryDTO.getScheduleDate())
               .eq(queryDTO.getShiftType() != null, ErpEmployeeSchedule::getShiftType, queryDTO.getShiftType())
               .eq(queryDTO.getStatus() != null, ErpEmployeeSchedule::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpEmployeeSchedule::getScheduleDate)
               .orderByDesc(ErpEmployeeSchedule::getCreateTime);
        
        Page<ErpEmployeeSchedule> result = employeeScheduleMapper.selectPage(page, wrapper);
        
        List<EmployeeScheduleVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public EmployeeScheduleVO getEmployeeScheduleById(Long id) {
        ErpEmployeeSchedule schedule = employeeScheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(schedule);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createEmployeeSchedule(EmployeeScheduleCreateDTO createDTO) {
        if (createDTO.getScheduleNo() == null || createDTO.getScheduleNo().isEmpty()) {
            String scheduleNo = "ES" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                String.format("%06d", System.currentTimeMillis() % 1000000);
            createDTO.setScheduleNo(scheduleNo);
        }

        ErpEmployeeSchedule schedule = new ErpEmployeeSchedule();
        schedule.setScheduleNo(createDTO.getScheduleNo());
        schedule.setGroupId(createDTO.getGroupId());
        schedule.setGroupCode(createDTO.getGroupCode());
        schedule.setGroupName(createDTO.getGroupName());
        schedule.setScheduleDate(createDTO.getScheduleDate());
        schedule.setShiftType(createDTO.getShiftType());
        schedule.setStartTime(createDTO.getStartTime());
        schedule.setEndTime(createDTO.getEndTime());
        schedule.setWorkHours(createDTO.getWorkHours());
        schedule.setEmployeeNames(createDTO.getEmployeeNames());
        schedule.setResponsiblePerson(createDTO.getResponsiblePerson());
        schedule.setRemark(createDTO.getRemark());
        schedule.setStatus(createDTO.getStatus());
        
        employeeScheduleMapper.insert(schedule);
        log.info("创建排班成功: {}", createDTO.getScheduleNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateEmployeeSchedule(EmployeeScheduleUpdateDTO updateDTO) {
        ErpEmployeeSchedule schedule = employeeScheduleMapper.selectById(updateDTO.getId());
        if (schedule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getGroupId() != null) {
            schedule.setGroupId(updateDTO.getGroupId());
        }
        if (updateDTO.getGroupCode() != null) {
            schedule.setGroupCode(updateDTO.getGroupCode());
        }
        if (updateDTO.getGroupName() != null) {
            schedule.setGroupName(updateDTO.getGroupName());
        }
        if (updateDTO.getScheduleDate() != null) {
            schedule.setScheduleDate(updateDTO.getScheduleDate());
        }
        if (updateDTO.getShiftType() != null) {
            schedule.setShiftType(updateDTO.getShiftType());
        }
        if (updateDTO.getStartTime() != null) {
            schedule.setStartTime(updateDTO.getStartTime());
        }
        if (updateDTO.getEndTime() != null) {
            schedule.setEndTime(updateDTO.getEndTime());
        }
        if (updateDTO.getWorkHours() != null) {
            schedule.setWorkHours(updateDTO.getWorkHours());
        }
        if (updateDTO.getEmployeeNames() != null) {
            schedule.setEmployeeNames(updateDTO.getEmployeeNames());
        }
        if (updateDTO.getResponsiblePerson() != null) {
            schedule.setResponsiblePerson(updateDTO.getResponsiblePerson());
        }
        if (updateDTO.getRemark() != null) {
            schedule.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            schedule.setStatus(updateDTO.getStatus());
        }
        
        employeeScheduleMapper.updateById(schedule);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployeeSchedule(Long id) {
        ErpEmployeeSchedule schedule = employeeScheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        employeeScheduleMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateEmployeeScheduleStatus(Long id, Integer status) {
        ErpEmployeeSchedule schedule = employeeScheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        schedule.setStatus(status);
        employeeScheduleMapper.updateById(schedule);
    }

    private EmployeeScheduleVO convertToVO(ErpEmployeeSchedule schedule) {
        String shiftTypeName = switch (schedule.getShiftType()) {
            case 1 -> "白班";
            case 2 -> "夜班";
            case 3 -> "中班";
            default -> "未知";
        };
        
        String statusName = switch (schedule.getStatus()) {
            case 1 -> "正常";
            case 2 -> "取消";
            case 3 -> "完成";
            default -> "未知";
        };
        
        return EmployeeScheduleVO.builder()
            .id(schedule.getId())
            .scheduleNo(schedule.getScheduleNo())
            .groupId(schedule.getGroupId())
            .groupCode(schedule.getGroupCode())
            .groupName(schedule.getGroupName())
            .scheduleDate(schedule.getScheduleDate())
            .shiftType(schedule.getShiftType())
            .shiftTypeName(shiftTypeName)
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .workHours(schedule.getWorkHours())
            .employeeNames(schedule.getEmployeeNames())
            .responsiblePerson(schedule.getResponsiblePerson())
            .remark(schedule.getRemark())
            .status(schedule.getStatus())
            .statusName(statusName)
            .createBy(schedule.getCreateBy())
            .createTime(schedule.getCreateTime())
            .updateBy(schedule.getUpdateBy())
            .updateTime(schedule.getUpdateTime())
            .build();
    }
}
