package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.BomVersionCreateDTO;
import com.erp.dto.BomVersionQueryDTO;
import com.erp.dto.BomVersionUpdateDTO;
import com.erp.entity.ErpBom;
import com.erp.entity.ErpBomVersion;
import com.erp.mapper.BomMapper;
import com.erp.mapper.BomVersionMapper;
import com.erp.vo.BomVersionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BOM版本服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BomVersionService {

    private final BomVersionMapper bomVersionMapper;
    private final BomMapper bomMapper;

    /**
     * 分页查询BOM版本列表
     */
    public PageResult<BomVersionVO> pageBomVersions(BomVersionQueryDTO queryDTO) {
        Page<ErpBomVersion> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpBomVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getBomId() != null, ErpBomVersion::getBomId, queryDTO.getBomId())
               .like(queryDTO.getVersion() != null, ErpBomVersion::getVersion, queryDTO.getVersion())
               .like(queryDTO.getEcnNumber() != null, ErpBomVersion::getEcnNumber, queryDTO.getEcnNumber())
               .eq(queryDTO.getStatus() != null, ErpBomVersion::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpBomVersion::getCreateTime);
        
        Page<ErpBomVersion> result = bomVersionMapper.selectPage(page, wrapper);
        
        List<BomVersionVO> voList = result.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(
            result.getTotal(), 
            voList, 
            (long) result.getCurrent(), 
            (long) result.getSize()
        );
    }

    /**
     * 根据ID查询BOM版本
     */
    public BomVersionVO getBomVersionById(Long id) {
        ErpBomVersion version = bomVersionMapper.selectById(id);
        if (version == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(version);
    }

    /**
     * 创建BOM版本
     */
    @Transactional(rollbackFor = Exception.class)
    public void createBomVersion(BomVersionCreateDTO createDTO) {
        // 检查BOM是否存在
        ErpBom bom = bomMapper.selectById(createDTO.getBomId());
        if (bom == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "BOM不存在");
        }

        // 检查版本号是否已存在
        Long count = bomVersionMapper.selectCount(
            new LambdaQueryWrapper<ErpBomVersion>()
                .eq(ErpBomVersion::getBomId, createDTO.getBomId())
                .eq(ErpBomVersion::getVersion, createDTO.getVersion())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.BOM_VERSION_EXISTS, "该BOM版本已存在");
        }

        ErpBomVersion version = new ErpBomVersion();
        version.setBomId(createDTO.getBomId());
        version.setVersion(createDTO.getVersion());
        version.setEcnNumber(createDTO.getEcnNumber());
        version.setChangeReason(createDTO.getChangeReason());
        version.setChangeContent(createDTO.getChangeContent());
        version.setEffectiveTime(createDTO.getEffectiveTime());
        version.setStatus(createDTO.getStatus());
        
        bomVersionMapper.insert(version);
    }

    /**
     * 更新BOM版本
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBomVersion(BomVersionUpdateDTO updateDTO) {
        ErpBomVersion version = bomVersionMapper.selectById(updateDTO.getId());
        if (version == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        if (updateDTO.getVersion() != null) {
            // 检查版本号是否已存在
            Long count = bomVersionMapper.selectCount(
                new LambdaQueryWrapper<ErpBomVersion>()
                    .eq(ErpBomVersion::getBomId, version.getBomId())
                    .eq(ErpBomVersion::getVersion, updateDTO.getVersion())
                    .ne(ErpBomVersion::getId, updateDTO.getId())
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.BOM_VERSION_EXISTS, "该BOM版本已存在");
            }
            version.setVersion(updateDTO.getVersion());
        }
        if (updateDTO.getEcnNumber() != null) {
            version.setEcnNumber(updateDTO.getEcnNumber());
        }
        if (updateDTO.getChangeReason() != null) {
            version.setChangeReason(updateDTO.getChangeReason());
        }
        if (updateDTO.getChangeContent() != null) {
            version.setChangeContent(updateDTO.getChangeContent());
        }
        if (updateDTO.getEffectiveTime() != null) {
            version.setEffectiveTime(updateDTO.getEffectiveTime());
        }
        if (updateDTO.getStatus() != null) {
            version.setStatus(updateDTO.getStatus());
        }
        
        bomVersionMapper.updateById(version);
    }

    /**
     * 删除BOM版本
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBomVersion(Long id) {
        ErpBomVersion version = bomVersionMapper.selectById(id);
        if (version == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        bomVersionMapper.deleteById(id);
    }

    /**
     * 更新BOM版本状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBomVersionStatus(Long id, Integer status) {
        ErpBomVersion version = bomVersionMapper.selectById(id);
        if (version == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        version.setStatus(status);
        bomVersionMapper.updateById(version);
    }

    /**
     * 转换为VO
     */
    private BomVersionVO convertToVO(ErpBomVersion version) {
        return BomVersionVO.builder()
            .id(version.getId())
            .bomId(version.getBomId())
            .version(version.getVersion())
            .ecnNumber(version.getEcnNumber())
            .changeReason(version.getChangeReason())
            .changeContent(version.getChangeContent())
            .effectiveTime(version.getEffectiveTime())
            .status(version.getStatus())
            .createBy(version.getCreateBy())
            .createTime(version.getCreateTime())
            .updateBy(version.getUpdateBy())
            .updateTime(version.getUpdateTime())
            .build();
    }
}
