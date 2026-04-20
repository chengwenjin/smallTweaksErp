package com.erp.controller;

import com.baserbac.annotation.OperationLog;
import com.baserbac.common.result.R;
import com.baserbac.common.result.PageResult;
import com.erp.dto.ProductCreateDTO;
import com.erp.dto.ProductQueryDTO;
import com.erp.dto.ProductUpdateDTO;
import com.erp.service.ProductService;
import com.erp.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 产品管理控制器
 */
@Tag(name = "产品管理")
@RestController
@RequestMapping("/api/erp/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "分页查询产品列表")
    @GetMapping
    public R<PageResult<ProductVO>> pageProducts(ProductQueryDTO queryDTO) {
        return R.success(productService.pageProducts(queryDTO));
    }

    @Operation(summary = "根据ID查询产品")
    @GetMapping("/{id}")
    public R<ProductVO> getProduct(@PathVariable Long id) {
        return R.success(productService.getProductById(id));
    }

    @OperationLog(module = "产品管理", value = "新增产品")
    @Operation(summary = "创建产品")
    @PostMapping
    public R<Void> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        productService.createProduct(createDTO);
        return R.success();
    }

    @OperationLog(module = "产品管理", value = "编辑产品")
    @Operation(summary = "更新产品")
    @PutMapping("/{id}")
    public R<Void> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO updateDTO) {
        updateDTO.setId(id);
        productService.updateProduct(updateDTO);
        return R.success();
    }

    @OperationLog(module = "产品管理", value = "删除产品")
    @Operation(summary = "删除产品")
    @DeleteMapping("/{id}")
    public R<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return R.success();
    }

    @OperationLog(module = "产品管理", value = "更新产品状态")
    @Operation(summary = "更新产品状态")
    @PutMapping("/{id}/status")
    public R<Void> updateProductStatus(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        productService.updateProductStatus(id, statusDTO.getStatus());
        return R.success();
    }

    @lombok.Data
    public static class StatusDTO {
        private Integer status;
    }
}
