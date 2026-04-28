-- ============================================
-- 质量检验管理功能数据库初始化脚本
-- ============================================

USE `small_tweaks_erp`;

-- ============================================
-- 1. 创建新表
-- ============================================

-- 来料检验单表 (IQC)
CREATE TABLE IF NOT EXISTS `erp_iqc_inspection` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `inspection_no` VARCHAR(50) NOT NULL COMMENT '检验单号',
    `purchase_order_id` BIGINT DEFAULT NULL COMMENT '采购订单ID',
    `purchase_order_no` VARCHAR(50) DEFAULT NULL COMMENT '采购订单号',
    `supplier_id` BIGINT DEFAULT NULL COMMENT '供应商ID',
    `supplier_code` VARCHAR(50) DEFAULT NULL COMMENT '供应商编码',
    `supplier_name` VARCHAR(100) DEFAULT NULL COMMENT '供应商名称',
    `material_id` BIGINT DEFAULT NULL COMMENT '物料ID',
    `material_code` VARCHAR(50) DEFAULT NULL COMMENT '物料编码',
    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物料名称',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `received_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '来料数量',
    `sample_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '抽样数量',
    `inspection_type` VARCHAR(20) DEFAULT NULL COMMENT '检验类型：FULL全检 SAMPLE抽检',
    `inspection_standard` VARCHAR(500) DEFAULT NULL COMMENT '检验标准',
    `inspection_date` DATETIME DEFAULT NULL COMMENT '检验日期',
    `inspector_id` BIGINT DEFAULT NULL COMMENT '检验员ID',
    `inspector_name` VARCHAR(50) DEFAULT NULL COMMENT '检验员姓名',
    `qualified_count` INT DEFAULT 0 COMMENT '合格数量',
    `unqualified_count` INT DEFAULT 0 COMMENT '不合格数量',
    `qualified_rate` DECIMAL(10,2) DEFAULT NULL COMMENT '合格率(%)',
    `inspection_result` VARCHAR(20) DEFAULT NULL COMMENT '检验结果：QUALIFIED合格 UNQUALIFIED不合格',
    `disposal_type` VARCHAR(20) DEFAULT NULL COMMENT '处理方式：RETURN退货 SPECIAL_ACCEPT特采 REWORK返工 SCRAP报废',
    `disposal_remark` VARCHAR(500) DEFAULT NULL COMMENT '处理说明',
    `disposal_date` DATETIME DEFAULT NULL COMMENT '处理日期',
    `disposal_user_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `disposal_user_name` VARCHAR(50) DEFAULT NULL COMMENT '处理人姓名',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '关联工单ID',
    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '关联工单号',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1草稿 2待检验 3检验完成 4待处理 5已完成 6已取消',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inspection_no` (`inspection_no`),
    KEY `idx_purchase_order_id` (`purchase_order_id`),
    KEY `idx_purchase_order_no` (`purchase_order_no`),
    KEY `idx_supplier_name` (`supplier_name`),
    KEY `idx_material_code` (`material_code`),
    KEY `idx_material_name` (`material_name`),
    KEY `idx_batch_no` (`batch_no`),
    KEY `idx_inspection_result` (`inspection_result`),
    KEY `idx_status` (`status`, `is_deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='来料检验单表';

-- 过程检验单表 (IPQC)
CREATE TABLE IF NOT EXISTS `erp_ipqc_inspection` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `inspection_no` VARCHAR(50) NOT NULL COMMENT '检验单号',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '工单号',
    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `process_id` BIGINT DEFAULT NULL COMMENT '工序ID',
    `process_code` VARCHAR(50) DEFAULT NULL COMMENT '工序编码',
    `process_name` VARCHAR(100) DEFAULT NULL COMMENT '工序名称',
    `process_sequence` INT DEFAULT NULL COMMENT '工序顺序',
    `equipment_id` BIGINT DEFAULT NULL COMMENT '设备ID',
    `equipment_code` VARCHAR(50) DEFAULT NULL COMMENT '设备编码',
    `equipment_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `production_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '生产数量',
    `inspection_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '检验数量',
    `inspection_type` VARCHAR(20) DEFAULT NULL COMMENT '检验类型：FULL全检 SAMPLE抽检',
    `inspection_standard` VARCHAR(500) DEFAULT NULL COMMENT '检验标准',
    `inspection_date` DATETIME DEFAULT NULL COMMENT '检验日期',
    `inspector_id` BIGINT DEFAULT NULL COMMENT '检验员ID',
    `inspector_name` VARCHAR(50) DEFAULT NULL COMMENT '检验员姓名',
    `qualified_count` INT DEFAULT 0 COMMENT '合格数量',
    `unqualified_count` INT DEFAULT 0 COMMENT '不合格数量',
    `rework_count` INT DEFAULT 0 COMMENT '返工数量',
    `scrapped_count` INT DEFAULT 0 COMMENT '报废数量',
    `qualified_rate` DECIMAL(10,2) DEFAULT NULL COMMENT '合格率(%)',
    `inspection_result` VARCHAR(20) DEFAULT NULL COMMENT '检验结果：QUALIFIED合格 UNQUALIFIED不合格',
    `next_process_status` VARCHAR(20) DEFAULT NULL COMMENT '下工序状态：ALLOW放行 HOLD扣留 REWORK返工',
    `disposal_type` VARCHAR(20) DEFAULT NULL COMMENT '处理方式：REWORK返工 SCRAP报废 SPECIAL_ACCEPT特采',
    `disposal_remark` VARCHAR(500) DEFAULT NULL COMMENT '处理说明',
    `disposal_date` DATETIME DEFAULT NULL COMMENT '处理日期',
    `disposal_user_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `disposal_user_name` VARCHAR(50) DEFAULT NULL COMMENT '处理人姓名',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1草稿 2待检验 3检验完成 4待处理 5已完成 6已取消',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inspection_no` (`inspection_no`),
    KEY `idx_work_order_id` (`work_order_id`),
    KEY `idx_work_order_no` (`work_order_no`),
    KEY `idx_product_code` (`product_code`),
    KEY `idx_product_name` (`product_name`),
    KEY `idx_process_code` (`process_code`),
    KEY `idx_process_name` (`process_name`),
    KEY `idx_equipment_code` (`equipment_code`),
    KEY `idx_equipment_name` (`equipment_name`),
    KEY `idx_batch_no` (`batch_no`),
    KEY `idx_inspection_result` (`inspection_result`),
    KEY `idx_next_process_status` (`next_process_status`),
    KEY `idx_status` (`status`, `is_deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='过程检验单表';

-- 成品检验单表 (FQC)
CREATE TABLE IF NOT EXISTS `erp_fqc_inspection` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `inspection_no` VARCHAR(50) NOT NULL COMMENT '检验单号',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '工单号',
    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `finished_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '完工数量',
    `inspection_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '检验数量',
    `inspection_type` VARCHAR(20) DEFAULT NULL COMMENT '检验类型：FULL全检 SAMPLE抽检',
    `inspection_standard` VARCHAR(500) DEFAULT NULL COMMENT '检验标准',
    `inspection_date` DATETIME DEFAULT NULL COMMENT '检验日期',
    `inspector_id` BIGINT DEFAULT NULL COMMENT '检验员ID',
    `inspector_name` VARCHAR(50) DEFAULT NULL COMMENT '检验员姓名',
    `qualified_count` INT DEFAULT 0 COMMENT '合格数量',
    `unqualified_count` INT DEFAULT 0 COMMENT '不合格数量',
    `rework_count` INT DEFAULT 0 COMMENT '返工数量',
    `scrapped_count` INT DEFAULT 0 COMMENT '报废数量',
    `qualified_rate` DECIMAL(10,2) DEFAULT NULL COMMENT '合格率(%)',
    `inspection_result` VARCHAR(20) DEFAULT NULL COMMENT '检验结果：QUALIFIED合格 UNQUALIFIED不合格',
    `certificate_no` VARCHAR(50) DEFAULT NULL COMMENT '合格证号',
    `certificate_date` DATETIME DEFAULT NULL COMMENT '合格证日期',
    `disposal_type` VARCHAR(20) DEFAULT NULL COMMENT '处理方式：REWORK返工 SCRAP报废 SPECIAL_ACCEPT特采',
    `disposal_remark` VARCHAR(500) DEFAULT NULL COMMENT '处理说明',
    `disposal_date` DATETIME DEFAULT NULL COMMENT '处理日期',
    `disposal_user_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `disposal_user_name` VARCHAR(50) DEFAULT NULL COMMENT '处理人姓名',
    `allow_warehousing` TINYINT DEFAULT 0 COMMENT '允许入库：0禁止 1允许',
    `warehouse_order_id` BIGINT DEFAULT NULL COMMENT '入库单ID',
    `warehouse_order_no` VARCHAR(50) DEFAULT NULL COMMENT '入库单号',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1草稿 2待检验 3检验完成 4待处理 5已完成 6已取消',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_inspection_no` (`inspection_no`),
    UNIQUE KEY `uk_certificate_no` (`certificate_no`),
    KEY `idx_work_order_id` (`work_order_id`),
    KEY `idx_work_order_no` (`work_order_no`),
    KEY `idx_product_code` (`product_code`),
    KEY `idx_product_name` (`product_name`),
    KEY `idx_batch_no` (`batch_no`),
    KEY `idx_inspection_result` (`inspection_result`),
    KEY `idx_allow_warehousing` (`allow_warehousing`),
    KEY `idx_status` (`status`, `is_deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成品检验单表';

-- 质量检验标准表
CREATE TABLE IF NOT EXISTS `erp_quality_standard` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `standard_no` VARCHAR(50) NOT NULL COMMENT '标准编号',
    `standard_name` VARCHAR(100) NOT NULL COMMENT '标准名称',
    `inspection_type` VARCHAR(20) DEFAULT NULL COMMENT '检验类型：IQC来料检验 IPQC过程检验 FQC成品检验',
    `material_id` BIGINT DEFAULT NULL COMMENT '物料ID',
    `material_code` VARCHAR(50) DEFAULT NULL COMMENT '物料编码',
    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物料名称',
    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
    `process_code` VARCHAR(50) DEFAULT NULL COMMENT '工序编码',
    `process_name` VARCHAR(100) DEFAULT NULL COMMENT '工序名称',
    `inspection_item` VARCHAR(200) DEFAULT NULL COMMENT '检验项目',
    `inspection_method` VARCHAR(500) DEFAULT NULL COMMENT '检验方法',
    `standard_value` VARCHAR(100) DEFAULT NULL COMMENT '标准值',
    `tolerance_min` VARCHAR(100) DEFAULT NULL COMMENT '公差下限',
    `tolerance_max` VARCHAR(100) DEFAULT NULL COMMENT '公差上限',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `is_key_item` TINYINT DEFAULT 0 COMMENT '是否关键项目：0否 1是',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_standard_no` (`standard_no`),
    KEY `idx_inspection_type` (`inspection_type`),
    KEY `idx_material_code` (`material_code`),
    KEY `idx_product_code` (`product_code`),
    KEY `idx_process_code` (`process_code`),
    KEY `idx_status` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检验标准表';

-- ============================================
-- 2. 插入菜单数据
-- ============================================

-- 插入顶级菜单：质量检验管理
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT 0, '质量检验管理', 1, NULL, '/quality', 'Layout', 'DocumentChecked', 120, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '质量检验管理' AND `parent_id` = 0
);

SET @quality_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '质量检验管理' AND `parent_id` = 0);

-- 插入二级菜单：全流程质检
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @quality_menu_id, '全流程质检', 1, NULL, 'full-inspection', 'Layout', 'List', 1, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '全流程质检' AND `parent_id` = @quality_menu_id
);

SET @full_inspection_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '全流程质检' AND `parent_id` = @quality_menu_id);

-- 插入三级菜单：来料检验 (IQC)
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @full_inspection_menu_id, '来料检验 (IQC)', 2, 'erp:iqcInspection:list', 'iqc-inspections', 'erp/IqcInspection', 'Document', 1, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '来料检验 (IQC)' AND `parent_id` = @full_inspection_menu_id
);

SET @iqc_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '来料检验 (IQC)' AND `parent_id` = @full_inspection_menu_id);

-- 插入来料检验按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @iqc_menu_id, '新增检验', 3, 'erp:iqcInspection:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增检验' AND `parent_id` = @iqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @iqc_menu_id, '编辑检验', 3, 'erp:iqcInspection:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑检验' AND `parent_id` = @iqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @iqc_menu_id, '删除检验', 3, 'erp:iqcInspection:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除检验' AND `parent_id` = @iqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @iqc_menu_id, '提交检验', 3, 'erp:iqcInspection:submit', 4, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '提交检验' AND `parent_id` = @iqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @iqc_menu_id, '完成检验', 3, 'erp:iqcInspection:complete', 5, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '完成检验' AND `parent_id` = @iqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @iqc_menu_id, '不合格品处理', 3, 'erp:iqcInspection:disposal', 6, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '不合格品处理' AND `parent_id` = @iqc_menu_id
);

-- 插入三级菜单：过程检验 (IPQC)
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @full_inspection_menu_id, '过程检验 (IPQC)', 2, 'erp:ipqcInspection:list', 'ipqc-inspections', 'erp/IpqcInspection', 'Document', 2, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '过程检验 (IPQC)' AND `parent_id` = @full_inspection_menu_id
);

SET @ipqc_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '过程检验 (IPQC)' AND `parent_id` = @full_inspection_menu_id);

-- 插入过程检验按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @ipqc_menu_id, '新增检验', 3, 'erp:ipqcInspection:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增检验' AND `parent_id` = @ipqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @ipqc_menu_id, '编辑检验', 3, 'erp:ipqcInspection:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑检验' AND `parent_id` = @ipqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @ipqc_menu_id, '删除检验', 3, 'erp:ipqcInspection:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除检验' AND `parent_id` = @ipqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @ipqc_menu_id, '提交检验', 3, 'erp:ipqcInspection:submit', 4, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '提交检验' AND `parent_id` = @ipqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @ipqc_menu_id, '完成检验', 3, 'erp:ipqcInspection:complete', 5, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '完成检验' AND `parent_id` = @ipqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @ipqc_menu_id, '不合格品处理', 3, 'erp:ipqcInspection:disposal', 6, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '不合格品处理' AND `parent_id` = @ipqc_menu_id
);

-- 插入三级菜单：成品检验 (FQC)
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @full_inspection_menu_id, '成品检验 (FQC)', 2, 'erp:fqcInspection:list', 'fqc-inspections', 'erp/FqcInspection', 'Document', 3, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '成品检验 (FQC)' AND `parent_id` = @full_inspection_menu_id
);

SET @fqc_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '成品检验 (FQC)' AND `parent_id` = @full_inspection_menu_id);

-- 插入成品检验按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @fqc_menu_id, '新增检验', 3, 'erp:fqcInspection:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增检验' AND `parent_id` = @fqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @fqc_menu_id, '编辑检验', 3, 'erp:fqcInspection:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑检验' AND `parent_id` = @fqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @fqc_menu_id, '删除检验', 3, 'erp:fqcInspection:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除检验' AND `parent_id` = @fqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @fqc_menu_id, '提交检验', 3, 'erp:fqcInspection:submit', 4, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '提交检验' AND `parent_id` = @fqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @fqc_menu_id, '完成检验', 3, 'erp:fqcInspection:complete', 5, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '完成检验' AND `parent_id` = @fqc_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @fqc_menu_id, '不合格品处理', 3, 'erp:fqcInspection:disposal', 6, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '不合格品处理' AND `parent_id` = @fqc_menu_id
);

-- ============================================
-- 3. 给超级管理员分配菜单权限
-- ============================================

-- 删除已有的关联（避免重复）
DELETE FROM `sys_role_menu` 
WHERE `role_id` = 1 
AND `menu_id` IN (
    SELECT `id` FROM `sys_menu` 
    WHERE `path` LIKE '%iqc-inspections%' 
       OR `path` LIKE '%ipqc-inspections%' 
       OR `path` LIKE '%fqc-inspections%'
       OR `menu_name` = '质量检验管理'
       OR `menu_name` = '全流程质检'
);

-- 插入新的关联
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu` 
WHERE `path` LIKE '%iqc-inspections%' 
   OR `path` LIKE '%ipqc-inspections%' 
   OR `path` LIKE '%fqc-inspections%'
   OR `menu_name` = '质量检验管理'
   OR `menu_name` = '全流程质检';

-- ============================================
-- 4. 插入接口权限
-- ============================================

-- 来料检验接口权限
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:iqcInspection:list', '查询来料检验列表', '来料检验', 'GET', '/api/erp/iqc-inspections', '分页查询来料检验列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:iqcInspection:list'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:iqcInspection:add', '新增来料检验', '来料检验', 'POST', '/api/erp/iqc-inspections', '创建来料检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:iqcInspection:add'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:iqcInspection:edit', '编辑来料检验', '来料检验', 'PUT', '/api/erp/iqc-inspections/{id}', '更新来料检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:iqcInspection:edit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:iqcInspection:delete', '删除来料检验', '来料检验', 'DELETE', '/api/erp/iqc-inspections/{id}', '删除来料检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:iqcInspection:delete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:iqcInspection:submit', '提交检验', '来料检验', 'PUT', '/api/erp/iqc-inspections/{id}/submit', '提交检验', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:iqcInspection:submit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:iqcInspection:complete', '完成检验', '来料检验', 'PUT', '/api/erp/iqc-inspections/{id}/complete', '完成检验', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:iqcInspection:complete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:iqcInspection:disposal', '不合格品处理', '来料检验', 'PUT', '/api/erp/iqc-inspections/{id}/disposal', '不合格品处理', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:iqcInspection:disposal'
);

-- 过程检验接口权限
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:ipqcInspection:list', '查询过程检验列表', '过程检验', 'GET', '/api/erp/ipqc-inspections', '分页查询过程检验列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:ipqcInspection:list'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:ipqcInspection:add', '新增过程检验', '过程检验', 'POST', '/api/erp/ipqc-inspections', '创建过程检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:ipqcInspection:add'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:ipqcInspection:edit', '编辑过程检验', '过程检验', 'PUT', '/api/erp/ipqc-inspections/{id}', '更新过程检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:ipqcInspection:edit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:ipqcInspection:delete', '删除过程检验', '过程检验', 'DELETE', '/api/erp/ipqc-inspections/{id}', '删除过程检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:ipqcInspection:delete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:ipqcInspection:submit', '提交检验', '过程检验', 'PUT', '/api/erp/ipqc-inspections/{id}/submit', '提交检验', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:ipqcInspection:submit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:ipqcInspection:complete', '完成检验', '过程检验', 'PUT', '/api/erp/ipqc-inspections/{id}/complete', '完成检验', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:ipqcInspection:complete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:ipqcInspection:disposal', '不合格品处理', '过程检验', 'PUT', '/api/erp/ipqc-inspections/{id}/disposal', '不合格品处理', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:ipqcInspection:disposal'
);

-- 成品检验接口权限
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:fqcInspection:list', '查询成品检验列表', '成品检验', 'GET', '/api/erp/fqc-inspections', '分页查询成品检验列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:fqcInspection:list'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:fqcInspection:add', '新增成品检验', '成品检验', 'POST', '/api/erp/fqc-inspections', '创建成品检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:fqcInspection:add'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:fqcInspection:edit', '编辑成品检验', '成品检验', 'PUT', '/api/erp/fqc-inspections/{id}', '更新成品检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:fqcInspection:edit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:fqcInspection:delete', '删除成品检验', '成品检验', 'DELETE', '/api/erp/fqc-inspections/{id}', '删除成品检验记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:fqcInspection:delete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:fqcInspection:submit', '提交检验', '成品检验', 'PUT', '/api/erp/fqc-inspections/{id}/submit', '提交检验', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:fqcInspection:submit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:fqcInspection:complete', '完成检验', '成品检验', 'PUT', '/api/erp/fqc-inspections/{id}/complete', '完成检验', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:fqcInspection:complete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:fqcInspection:disposal', '不合格品处理', '成品检验', 'PUT', '/api/erp/fqc-inspections/{id}/disposal', '不合格品处理', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:fqcInspection:disposal'
);

-- ============================================
-- 5. 给超级管理员分配接口权限
-- ============================================

-- 删除已有的关联（避免重复）
DELETE FROM `sys_role_api` 
WHERE `role_id` = 1 
AND `api_id` IN (
    SELECT `id` FROM `sys_api_permission` 
    WHERE `permission_key` LIKE 'erp:iqcInspection:%' 
       OR `permission_key` LIKE 'erp:ipqcInspection:%' 
       OR `permission_key` LIKE 'erp:fqcInspection:%'
);

-- 插入新的关联
INSERT INTO `sys_role_api` (`role_id`, `api_id`)
SELECT 1, `id` FROM `sys_api_permission` 
WHERE `permission_key` LIKE 'erp:iqcInspection:%' 
   OR `permission_key` LIKE 'erp:ipqcInspection:%' 
   OR `permission_key` LIKE 'erp:fqcInspection:%';

SELECT '质量检验管理数据库初始化完成！' AS message;
