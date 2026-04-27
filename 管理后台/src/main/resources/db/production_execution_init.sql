-- ============================================
-- 现场执行与损耗管控功能数据库初始化脚本
-- ============================================

USE `small_tweaks_erp`;

-- ============================================
-- 1. 创建新表
-- ============================================

-- 工序报工记录表
CREATE TABLE IF NOT EXISTS `erp_process_report` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `report_no` VARCHAR(50) NOT NULL COMMENT '报工单号',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '工单号',
    `work_order_name` VARCHAR(100) DEFAULT NULL COMMENT '工单名称',
    `process_id` BIGINT DEFAULT NULL COMMENT '工序ID',
    `process_code` VARCHAR(50) DEFAULT NULL COMMENT '工序编码',
    `process_name` VARCHAR(100) DEFAULT NULL COMMENT '工序名称',
    `equipment_id` BIGINT DEFAULT NULL COMMENT '设备ID',
    `equipment_code` VARCHAR(50) DEFAULT NULL COMMENT '设备编码',
    `equipment_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人员ID',
    `operator_code` VARCHAR(50) DEFAULT NULL COMMENT '操作人员编码',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人员名称',
    `report_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '报工数量',
    `qualified_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '合格数量',
    `scrapped_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '报废数量',
    `rework_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '返工数量',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `work_hours` DECIMAL(10,2) DEFAULT NULL COMMENT '工时（小时）',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `barcode` VARCHAR(100) DEFAULT NULL COMMENT '条码',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1待报工 2已报工 3已审核',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_report_no` (`report_no`),
    KEY `idx_work_order_id` (`work_order_id`),
    KEY `idx_work_order_no` (`work_order_no`),
    KEY `idx_operator_name` (`operator_name`),
    KEY `idx_barcode` (`barcode`),
    KEY `idx_status` (`status`, `is_deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工序报工记录表';

-- 领料单表
CREATE TABLE IF NOT EXISTS `erp_material_pick` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pick_no` VARCHAR(50) NOT NULL COMMENT '领料单号',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '工单号',
    `work_order_name` VARCHAR(100) DEFAULT NULL COMMENT '工单名称',
    `bom_id` BIGINT DEFAULT NULL COMMENT 'BOM ID',
    `bom_version_id` BIGINT DEFAULT NULL COMMENT 'BOM版本ID',
    `bom_version_no` VARCHAR(50) DEFAULT NULL COMMENT 'BOM版本号',
    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
    `material_id` BIGINT DEFAULT NULL COMMENT '物料ID',
    `material_code` VARCHAR(50) DEFAULT NULL COMMENT '物料编码',
    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物料名称',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `plan_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '计划领料数量（BOM定额）',
    `picked_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '已领料数量',
    `remaining_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '剩余领料数量',
    `plan_pick_date` DATE DEFAULT NULL COMMENT '计划领料日期',
    `actual_pick_date` DATE DEFAULT NULL COMMENT '实际领料日期',
    `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
    `warehouse_code` VARCHAR(50) DEFAULT NULL COMMENT '仓库编码',
    `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
    `picker_id` BIGINT DEFAULT NULL COMMENT '领料人ID',
    `picker_name` VARCHAR(50) DEFAULT NULL COMMENT '领料人名称',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1新建 2待审批 3已审批 4已领料 5已超领待审批',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_pick_no` (`pick_no`),
    KEY `idx_work_order_id` (`work_order_id`),
    KEY `idx_work_order_no` (`work_order_no`),
    KEY `idx_material_code` (`material_code`),
    KEY `idx_material_name` (`material_name`),
    KEY `idx_status` (`status`, `is_deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领料单表';

-- 退补料单表
CREATE TABLE IF NOT EXISTS `erp_material_return` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `return_no` VARCHAR(50) NOT NULL COMMENT '退补料单号',
    `return_type` TINYINT NOT NULL DEFAULT 1 COMMENT '退补料类型：1余料退回 2不良品补料',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '工单号',
    `work_order_name` VARCHAR(100) DEFAULT NULL COMMENT '工单名称',
    `pick_id` BIGINT DEFAULT NULL COMMENT '领料单ID',
    `pick_no` VARCHAR(50) DEFAULT NULL COMMENT '领料单号',
    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
    `material_id` BIGINT DEFAULT NULL COMMENT '物料ID',
    `material_code` VARCHAR(50) DEFAULT NULL COMMENT '物料编码',
    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物料名称',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `return_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '退补料数量',
    `return_value` DECIMAL(18,4) DEFAULT NULL COMMENT '退补料价值',
    `return_reason` VARCHAR(255) DEFAULT NULL COMMENT '退补料原因',
    `return_date` DATE DEFAULT NULL COMMENT '退补料日期',
    `warehouse_id` BIGINT DEFAULT NULL COMMENT '仓库ID',
    `warehouse_code` VARCHAR(50) DEFAULT NULL COMMENT '仓库编码',
    `warehouse_name` VARCHAR(100) DEFAULT NULL COMMENT '仓库名称',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人员ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人员名称',
    `batch_no` VARCHAR(50) DEFAULT NULL COMMENT '批次号',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1新建 2待审批 3已审批 4已完成',
    `approval_user_id` BIGINT DEFAULT NULL COMMENT '审批用户ID',
    `approval_user_name` VARCHAR(50) DEFAULT NULL COMMENT '审批用户名',
    `approval_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `approval_opinion` VARCHAR(255) DEFAULT NULL COMMENT '审批意见',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_return_no` (`return_no`),
    KEY `idx_work_order_id` (`work_order_id`),
    KEY `idx_work_order_no` (`work_order_no`),
    KEY `idx_material_code` (`material_code`),
    KEY `idx_material_name` (`material_name`),
    KEY `idx_return_type` (`return_type`),
    KEY `idx_status` (`status`, `is_deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退补料单表';

-- 超领审批单表
CREATE TABLE IF NOT EXISTS `erp_over_pick_approval` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `approval_no` VARCHAR(50) NOT NULL COMMENT '审批单号',
    `work_order_id` BIGINT DEFAULT NULL COMMENT '工单ID',
    `work_order_no` VARCHAR(50) DEFAULT NULL COMMENT '工单号',
    `work_order_name` VARCHAR(100) DEFAULT NULL COMMENT '工单名称',
    `pick_id` BIGINT DEFAULT NULL COMMENT '领料单ID',
    `pick_no` VARCHAR(50) DEFAULT NULL COMMENT '领料单号',
    `product_id` BIGINT DEFAULT NULL COMMENT '产品ID',
    `product_code` VARCHAR(50) DEFAULT NULL COMMENT '产品编码',
    `product_name` VARCHAR(100) DEFAULT NULL COMMENT '产品名称',
    `material_id` BIGINT DEFAULT NULL COMMENT '物料ID',
    `material_code` VARCHAR(50) DEFAULT NULL COMMENT '物料编码',
    `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物料名称',
    `specification` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `plan_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '计划领料数量（BOM定额）',
    `over_pick_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '超领数量',
    `total_quantity` DECIMAL(18,4) DEFAULT 0 COMMENT '总领料数量',
    `over_pick_reason` VARCHAR(255) DEFAULT NULL COMMENT '超领原因',
    `applicant_id` BIGINT DEFAULT NULL COMMENT '申请人ID',
    `applicant_name` VARCHAR(50) DEFAULT NULL COMMENT '申请人名称',
    `application_time` DATETIME DEFAULT NULL COMMENT '申请时间',
    `approval_user_id` BIGINT DEFAULT NULL COMMENT '审批用户ID',
    `approval_user_name` VARCHAR(50) DEFAULT NULL COMMENT '审批用户名',
    `approval_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `approval_opinion` VARCHAR(255) DEFAULT NULL COMMENT '审批意见',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1草稿 2待审批 3已通过 4已驳回 5已撤销',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除：0未删除 1已删除',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `ext1` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段1',
    `ext2` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段2',
    `ext3` VARCHAR(255) DEFAULT NULL COMMENT '预留扩展字段3',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_approval_no` (`approval_no`),
    KEY `idx_work_order_id` (`work_order_id`),
    KEY `idx_work_order_no` (`work_order_no`),
    KEY `idx_pick_id` (`pick_id`),
    KEY `idx_material_code` (`material_code`),
    KEY `idx_material_name` (`material_name`),
    KEY `idx_status` (`status`, `is_deleted`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='超领审批单表';

-- ============================================
-- 2. 插入菜单数据
-- ============================================

-- 查找"生产工单与执行"菜单ID
SET @work_order_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '生产工单与执行' AND `parent_id` = 0);

-- 如果没有找到，则插入顶级菜单
IF @work_order_menu_id IS NULL THEN
    INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
    VALUES (0, '生产工单与执行', 1, NULL, '/work-order', 'Layout', 'List', 100, 1, 1, 1);
    SET @work_order_menu_id = LAST_INSERT_ID();
END IF;

-- 插入"现场执行与损耗管控"二级菜单
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @work_order_menu_id, '现场执行与损耗管控', 1, NULL, 'execution', 'Layout', 'List', 3, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '现场执行与损耗管控' AND `parent_id` = @work_order_menu_id
);

SET @execution_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '现场执行与损耗管控' AND `parent_id` = @work_order_menu_id);

-- 插入三级菜单：扫码报工
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @execution_menu_id, '扫码报工', 2, 'erp:processReport:list', 'process-reports', 'erp/ProcessReport', 'Document', 1, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '扫码报工' AND `parent_id` = @execution_menu_id
);

SET @process_report_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '扫码报工' AND `parent_id` = @execution_menu_id);

-- 插入扫码报工按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @process_report_menu_id, '新增报工', 3, 'erp:processReport:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增报工' AND `parent_id` = @process_report_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @process_report_menu_id, '编辑报工', 3, 'erp:processReport:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑报工' AND `parent_id` = @process_report_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @process_report_menu_id, '删除报工', 3, 'erp:processReport:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除报工' AND `parent_id` = @process_report_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @process_report_menu_id, '扫码报工', 3, 'erp:processReport:scan', 4, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '扫码报工' AND `parent_id` = @process_report_menu_id AND `menu_type` = 3
);

-- 插入三级菜单：限额领料
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @execution_menu_id, '限额领料', 2, 'erp:materialPick:list', 'material-picks', 'erp/MaterialPick', 'Box', 2, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '限额领料' AND `parent_id` = @execution_menu_id
);

SET @material_pick_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '限额领料' AND `parent_id` = @execution_menu_id);

-- 插入限额领料按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_pick_menu_id, '新增领料', 3, 'erp:materialPick:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增领料' AND `parent_id` = @material_pick_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_pick_menu_id, '编辑领料', 3, 'erp:materialPick:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑领料' AND `parent_id` = @material_pick_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_pick_menu_id, '删除领料', 3, 'erp:materialPick:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除领料' AND `parent_id` = @material_pick_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_pick_menu_id, '领料', 3, 'erp:materialPick:pick', 4, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '领料' AND `parent_id` = @material_pick_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_pick_menu_id, '审批', 3, 'erp:materialPick:approval', 5, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '审批' AND `parent_id` = @material_pick_menu_id
);

-- 插入三级菜单：退补料管理
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @execution_menu_id, '退补料管理', 2, 'erp:materialReturn:list', 'material-returns', 'erp/MaterialReturn', 'Refresh', 3, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '退补料管理' AND `parent_id` = @execution_menu_id
);

SET @material_return_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '退补料管理' AND `parent_id` = @execution_menu_id);

-- 插入退补料管理按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_return_menu_id, '新增退补料', 3, 'erp:materialReturn:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增退补料' AND `parent_id` = @material_return_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_return_menu_id, '编辑退补料', 3, 'erp:materialReturn:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑退补料' AND `parent_id` = @material_return_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_return_menu_id, '删除退补料', 3, 'erp:materialReturn:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除退补料' AND `parent_id` = @material_return_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @material_return_menu_id, '审批', 3, 'erp:materialReturn:approval', 4, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '审批' AND `parent_id` = @material_return_menu_id AND `menu_type` = 3
);

-- 插入三级菜单：超领审批
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @execution_menu_id, '超领审批', 2, 'erp:overPickApproval:list', 'over-pick-approvals', 'erp/OverPickApproval', 'DocumentChecked', 4, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '超领审批' AND `parent_id` = @execution_menu_id
);

SET @over_pick_approval_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '超领审批' AND `parent_id` = @execution_menu_id);

-- 插入超领审批按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @over_pick_approval_menu_id, '新增申请', 3, 'erp:overPickApproval:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增申请' AND `parent_id` = @over_pick_approval_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @over_pick_approval_menu_id, '编辑申请', 3, 'erp:overPickApproval:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑申请' AND `parent_id` = @over_pick_approval_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @over_pick_approval_menu_id, '删除申请', 3, 'erp:overPickApproval:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除申请' AND `parent_id` = @over_pick_approval_menu_id
);

INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @over_pick_approval_menu_id, '审批', 3, 'erp:overPickApproval:approval', 4, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '审批' AND `parent_id` = @over_pick_approval_menu_id
);

-- ============================================
-- 3. 给超级管理员分配菜单权限
-- ============================================

-- 获取新增菜单ID列表
SET @new_menu_ids = (
    SELECT GROUP_CONCAT(`id`) FROM `sys_menu` 
    WHERE `path` LIKE '%process-reports%' 
       OR `path` LIKE '%material-picks%' 
       OR `path` LIKE '%material-returns%' 
       OR `path` LIKE '%over-pick-approvals%'
       OR `menu_name` = '现场执行与损耗管控'
       OR `parent_id` IN (
           SELECT `id` FROM `sys_menu` 
           WHERE `path` LIKE '%process-reports%' 
              OR `path` LIKE '%material-picks%' 
              OR `path` LIKE '%material-returns%' 
              OR `path` LIKE '%over-pick-approvals%'
       )
);

-- 删除已有的关联（避免重复）
DELETE FROM `sys_role_menu` 
WHERE `role_id` = 1 
AND `menu_id` IN (
    SELECT `id` FROM `sys_menu` 
    WHERE `path` LIKE '%process-reports%' 
       OR `path` LIKE '%material-picks%' 
       OR `path` LIKE '%material-returns%' 
       OR `path` LIKE '%over-pick-approvals%'
       OR `menu_name` = '现场执行与损耗管控'
);

-- 插入新的关联
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu` 
WHERE `path` LIKE '%process-reports%' 
   OR `path` LIKE '%material-picks%' 
   OR `path` LIKE '%material-returns%' 
   OR `path` LIKE '%over-pick-approvals%'
   OR `menu_name` = '现场执行与损耗管控';

-- ============================================
-- 4. 插入接口权限
-- ============================================

-- 扫码报工接口权限
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:processReport:list', '查询工序报工列表', '扫码报工', 'GET', '/api/erp/process-reports/list', '分页查询工序报工列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:processReport:list'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:processReport:add', '新增工序报工', '扫码报工', 'POST', '/api/erp/process-reports', '创建工序报工记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:processReport:add'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:processReport:edit', '编辑工序报工', '扫码报工', 'PUT', '/api/erp/process-reports', '更新工序报工记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:processReport:edit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:processReport:delete', '删除工序报工', '扫码报工', 'DELETE', '/api/erp/process-reports/{id}', '删除工序报工记录', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:processReport:delete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:processReport:scan', '扫码报工', '扫码报工', 'POST', '/api/erp/process-reports/scan', '扫码报工', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:processReport:scan'
);

-- 限额领料接口权限
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialPick:list', '查询领料单列表', '限额领料', 'GET', '/api/erp/material-picks/list', '分页查询领料单列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialPick:list'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialPick:add', '新增领料单', '限额领料', 'POST', '/api/erp/material-picks', '创建领料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialPick:add'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialPick:edit', '编辑领料单', '限额领料', 'PUT', '/api/erp/material-picks', '更新领料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialPick:edit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialPick:delete', '删除领料单', '限额领料', 'DELETE', '/api/erp/material-picks/{id}', '删除领料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialPick:delete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialPick:pick', '领料', '限额领料', 'POST', '/api/erp/material-picks/pick', '执行领料', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialPick:pick'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialPick:approval', '审批领料单', '限额领料', 'POST', '/api/erp/material-picks/approval', '审批领料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialPick:approval'
);

-- 退补料管理接口权限
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialReturn:list', '查询退补料单列表', '退补料管理', 'GET', '/api/erp/material-returns/list', '分页查询退补料单列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialReturn:list'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialReturn:add', '新增退补料单', '退补料管理', 'POST', '/api/erp/material-returns', '创建退补料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialReturn:add'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialReturn:edit', '编辑退补料单', '退补料管理', 'PUT', '/api/erp/material-returns', '更新退补料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialReturn:edit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialReturn:delete', '删除退补料单', '退补料管理', 'DELETE', '/api/erp/material-returns/{id}', '删除退补料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialReturn:delete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:materialReturn:approval', '审批退补料单', '退补料管理', 'POST', '/api/erp/material-returns/approval', '审批退补料单', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:materialReturn:approval'
);

-- 超领审批接口权限
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:overPickApproval:list', '查询超领审批单列表', '超领审批', 'GET', '/api/erp/over-pick-approvals/list', '分页查询超领审批单列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:overPickApproval:list'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:overPickApproval:add', '新增超领申请', '超领审批', 'POST', '/api/erp/over-pick-approvals', '创建超领申请', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:overPickApproval:add'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:overPickApproval:edit', '编辑超领申请', '超领审批', 'PUT', '/api/erp/over-pick-approvals', '更新超领申请', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:overPickApproval:edit'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:overPickApproval:delete', '删除超领申请', '超领审批', 'DELETE', '/api/erp/over-pick-approvals/{id}', '删除超领申请', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:overPickApproval:delete'
);

INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:overPickApproval:approval', '审批超领申请', '超领审批', 'POST', '/api/erp/over-pick-approvals/approval', '审批超领申请', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:overPickApproval:approval'
);

-- ============================================
-- 5. 给超级管理员分配接口权限
-- ============================================

-- 删除已有的关联（避免重复）
DELETE FROM `sys_role_api` 
WHERE `role_id` = 1 
AND `api_id` IN (
    SELECT `id` FROM `sys_api_permission` 
    WHERE `permission_key` LIKE 'erp:processReport:%' 
       OR `permission_key` LIKE 'erp:materialPick:%' 
       OR `permission_key` LIKE 'erp:materialReturn:%' 
       OR `permission_key` LIKE 'erp:overPickApproval:%'
);

-- 插入新的关联
INSERT INTO `sys_role_api` (`role_id`, `api_id`)
SELECT 1, `id` FROM `sys_api_permission` 
WHERE `permission_key` LIKE 'erp:processReport:%' 
   OR `permission_key` LIKE 'erp:materialPick:%' 
   OR `permission_key` LIKE 'erp:materialReturn:%' 
   OR `permission_key` LIKE 'erp:overPickApproval:%';

SELECT '数据库初始化完成！' AS message;
