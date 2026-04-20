package com.erp.service;

import com.baserbac.common.enums.ResultCode;
import com.baserbac.common.exception.BusinessException;
import com.baserbac.common.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.dto.ProductCreateDTO;
import com.erp.dto.ProductQueryDTO;
import com.erp.dto.ProductUpdateDTO;
import com.erp.entity.ErpProduct;
import com.erp.mapper.ProductMapper;
import com.erp.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 产品服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    /**
     * 分页查询产品列表
     */
    public PageResult<ProductVO> pageProducts(ProductQueryDTO queryDTO) {
        Page<ErpProduct> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErpProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(queryDTO.getProductCode() != null, ErpProduct::getProductCode, queryDTO.getProductCode())
               .like(queryDTO.getProductName() != null, ErpProduct::getProductName, queryDTO.getProductName())
               .eq(queryDTO.getCategory() != null, ErpProduct::getCategory, queryDTO.getCategory())
               .eq(queryDTO.getBrand() != null, ErpProduct::getBrand, queryDTO.getBrand())
               .eq(queryDTO.getStatus() != null, ErpProduct::getStatus, queryDTO.getStatus())
               .orderByDesc(ErpProduct::getCreateTime);
        
        Page<ErpProduct> result = productMapper.selectPage(page, wrapper);
        
        List<ProductVO> voList = result.getRecords().stream()
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
     * 根据ID查询产品
     */
    public ProductVO getProductById(Long id) {
        ErpProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return convertToVO(product);
    }

    /**
     * 创建产品
     */
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(ProductCreateDTO createDTO) {
        // 检查产品编码是否存在
        Long count = productMapper.selectCount(
            new LambdaQueryWrapper<ErpProduct>()
                .eq(ErpProduct::getProductCode, createDTO.getProductCode())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.PRODUCT_CODE_EXISTS, "产品编码已存在");
        }

        // 创建产品
        ErpProduct product = new ErpProduct();
        product.setProductCode(createDTO.getProductCode());
        product.setProductName(createDTO.getProductName());
        product.setSpecification(createDTO.getSpecification());
        product.setUnit(createDTO.getUnit());
        product.setCategory(createDTO.getCategory());
        product.setBrand(createDTO.getBrand());
        product.setTechnicalParams(createDTO.getTechnicalParams());
        product.setDescription(createDTO.getDescription());
        product.setStatus(createDTO.getStatus());
        
        productMapper.insert(product);
    }

    /**
     * 更新产品
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductUpdateDTO updateDTO) {
        ErpProduct product = productMapper.selectById(updateDTO.getId());
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 更新产品信息
        if (updateDTO.getProductName() != null) {
            product.setProductName(updateDTO.getProductName());
        }
        if (updateDTO.getSpecification() != null) {
            product.setSpecification(updateDTO.getSpecification());
        }
        if (updateDTO.getUnit() != null) {
            product.setUnit(updateDTO.getUnit());
        }
        if (updateDTO.getCategory() != null) {
            product.setCategory(updateDTO.getCategory());
        }
        if (updateDTO.getBrand() != null) {
            product.setBrand(updateDTO.getBrand());
        }
        if (updateDTO.getTechnicalParams() != null) {
            product.setTechnicalParams(updateDTO.getTechnicalParams());
        }
        if (updateDTO.getDescription() != null) {
            product.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getStatus() != null) {
            product.setStatus(updateDTO.getStatus());
        }
        
        productMapper.updateById(product);
    }

    /**
     * 删除产品（软删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        ErpProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        // 软删除产品
        productMapper.deleteById(id);
    }

    /**
     * 更新产品状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long id, Integer status) {
        ErpProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        product.setStatus(status);
        productMapper.updateById(product);
    }

    /**
     * 转换为VO
     */
    private ProductVO convertToVO(ErpProduct product) {
        return ProductVO.builder()
            .id(product.getId())
            .productCode(product.getProductCode())
            .productName(product.getProductName())
            .specification(product.getSpecification())
            .unit(product.getUnit())
            .category(product.getCategory())
            .brand(product.getBrand())
            .technicalParams(product.getTechnicalParams())
            .description(product.getDescription())
            .status(product.getStatus())
            .createBy(product.getCreateBy())
            .createTime(product.getCreateTime())
            .updateBy(product.getUpdateBy())
            .updateTime(product.getUpdateTime())
            .build();
    }
}
