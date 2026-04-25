package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.WorkGroupCreateDTO;
import com.erp.dto.WorkGroupQueryDTO;
import com.erp.dto.WorkGroupUpdateDTO;
import com.erp.entity.ErpWorkGroup;
import com.erp.mapper.WorkGroupMapper;
import com.erp.vo.WorkGroupVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkGroupService {

    private final WorkGroupMapper workGroupMapper;

    public PageResult<WorkGroupVO> pageWorkGroups(WorkGroupQueryDTO queryDTO) {
        Page<ErpWorkGroup> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpWorkGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getGroupCode() != null && !queryDTO.getGroupCode().isEmpty(), ErpWorkGroup::getGroupCode, queryDTO.getGroupCode())
               .like(queryDTO.getGroupName() != null && !queryDTO.getGroupName().isEmpty(), ErpWorkGroup::getGroupName, queryDTO.getGroupName())
               .eq(queryDTO.getGroupType() != null && !queryDTO.getGroupType().isEmpty(), ErpWorkGroup::getGroupType, queryDTO.getGroupType())
               .eq(queryDTO.getWorkshop() != null && !queryDTO.getWorkshop().isEmpty(), ErpWorkGroup::getWorkshop, queryDTO.getWorkshop())
               .eq(queryDTO.getWorkcenter() != null && !queryDTO.getWorkcenter().isEmpty(), ErpWorkGroup::getWorkcenter, queryDTO.getWorkcenter())
               .eq(queryDTO.getStatus() != null, ErpWorkGroup::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpWorkGroup::getCreateTime);
        
        Page<ErpWorkGroup> result = workGroupMapper.selectPage(page, wrapper);
        
        List<WorkGroupVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    public WorkGroupVO getWorkGroupById(Long id) {
        ErpWorkGroup workGroup = workGroupMapper.selectById(id);
        if (workGroup == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(workGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createWorkGroup(WorkGroupCreateDTO createDTO) {
        Long count = workGroupMapper.selectCount(
            new LambdaQueryWrapper<ErpWorkGroup>()
                .eq(ErpWorkGroup::getGroupCode, createDTO.getGroupCode())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "班组编码已存在");
        }

        ErpWorkGroup workGroup = new ErpWorkGroup();
        workGroup.setGroupCode(createDTO.getGroupCode());
        workGroup.setGroupName(createDTO.getGroupName());
        workGroup.setGroupType(createDTO.getGroupType());
        workGroup.setSupervisor(createDTO.getSupervisor());
        workGroup.setSupervisorPhone(createDTO.getSupervisorPhone());
        workGroup.setWorkshop(createDTO.getWorkshop());
        workGroup.setWorkcenter(createDTO.getWorkcenter());
        workGroup.setMemberCount(createDTO.getMemberCount());
        workGroup.setSkillLevel(createDTO.getSkillLevel());
        workGroup.setRemark(createDTO.getRemark());
        workGroup.setStatus(createDTO.getStatus());
        
        workGroupMapper.insert(workGroup);
        log.info("创建班组成功: {}", createDTO.getGroupCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateWorkGroup(WorkGroupUpdateDTO updateDTO) {
        ErpWorkGroup workGroup = workGroupMapper.selectById(updateDTO.getId());
        if (workGroup == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getGroupName() != null) {
            workGroup.setGroupName(updateDTO.getGroupName());
        }
        if (updateDTO.getGroupType() != null) {
            workGroup.setGroupType(updateDTO.getGroupType());
        }
        if (updateDTO.getSupervisor() != null) {
            workGroup.setSupervisor(updateDTO.getSupervisor());
        }
        if (updateDTO.getSupervisorPhone() != null) {
            workGroup.setSupervisorPhone(updateDTO.getSupervisorPhone());
        }
        if (updateDTO.getWorkshop() != null) {
            workGroup.setWorkshop(updateDTO.getWorkshop());
        }
        if (updateDTO.getWorkcenter() != null) {
            workGroup.setWorkcenter(updateDTO.getWorkcenter());
        }
        if (updateDTO.getMemberCount() != null) {
            workGroup.setMemberCount(updateDTO.getMemberCount());
        }
        if (updateDTO.getSkillLevel() != null) {
            workGroup.setSkillLevel(updateDTO.getSkillLevel());
        }
        if (updateDTO.getRemark() != null) {
            workGroup.setRemark(updateDTO.getRemark());
        }
        if (updateDTO.getStatus() != null) {
            workGroup.setStatus(updateDTO.getStatus());
        }
        
        workGroupMapper.updateById(workGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkGroup(Long id) {
        ErpWorkGroup workGroup = workGroupMapper.selectById(id);
        if (workGroup == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        workGroupMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateWorkGroupStatus(Long id, Integer status) {
        ErpWorkGroup workGroup = workGroupMapper.selectById(id);
        if (workGroup == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        workGroup.setStatus(status);
        workGroupMapper.updateById(workGroup);
    }

    private WorkGroupVO convertToVO(ErpWorkGroup workGroup) {
        String groupTypeName = workGroup.getGroupType();
        if (groupTypeName == null) {
            groupTypeName = "其他";
        }
        
        String skillLevelName = switch (workGroup.getSkillLevel()) {
            case "1" -> "初级";
            case "2" -> "中级";
            case "3" -> "高级";
            case "4" -> "专家级";
            default -> workGroup.getSkillLevel() != null ? workGroup.getSkillLevel() : "未知";
        };
        
        String statusName = switch (workGroup.getStatus()) {
            case 1 -> "启用";
            case 0 -> "停用";
            default -> "未知";
        };
        
        return WorkGroupVO.builder()
            .id(workGroup.getId())
            .groupCode(workGroup.getGroupCode())
            .groupName(workGroup.getGroupName())
            .groupType(workGroup.getGroupType())
            .groupTypeName(groupTypeName)
            .supervisor(workGroup.getSupervisor())
            .supervisorPhone(workGroup.getSupervisorPhone())
            .workshop(workGroup.getWorkshop())
            .workcenter(workGroup.getWorkcenter())
            .memberCount(workGroup.getMemberCount())
            .skillLevel(workGroup.getSkillLevel())
            .skillLevelName(skillLevelName)
            .remark(workGroup.getRemark())
            .status(workGroup.getStatus())
            .statusName(statusName)
            .createBy(workGroup.getCreateBy())
            .createTime(workGroup.getCreateTime())
            .updateBy(workGroup.getUpdateBy())
            .updateTime(workGroup.getUpdateTime())
            .build();
    }
}
