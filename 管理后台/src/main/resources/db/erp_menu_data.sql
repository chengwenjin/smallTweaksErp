-- ============================================
-- 产品与BOM管理菜单数据
-- ============================================

USE `small_tweaks_erp`;

-- 1. 插入顶级菜单：产品与BOM管理
-- 先检查是否已存在
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT 0, '产品与BOM管理', 1, NULL, '/erp', 'Layout', 'Goods', 70, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '产品与BOM管理' AND `parent_id` = 0
);

-- 2. 获取产品与BOM管理的菜单ID
SET @erp_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '产品与BOM管理' AND `parent_id` = 0);

-- 3. 插入二级菜单：基础数据管理
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @erp_menu_id, '基础数据管理', 1, NULL, 'base-data', 'Layout', 'Setting', 1, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '基础数据管理' AND `parent_id` = @erp_menu_id
);

-- 4. 获取基础数据管理的菜单ID
SET @base_data_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '基础数据管理' AND `parent_id` = @erp_menu_id);

-- 5. 插入三级菜单：产品档案
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`)
SELECT @base_data_menu_id, '产品档案', 2, 'erp:product:list', 'product', 'erp/Product', 'Goods', 1, 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '产品档案' AND `parent_id` = @base_data_menu_id
);

-- 6. 获取产品档案的菜单ID
SET @product_menu_id = (SELECT `id` FROM `sys_menu` WHERE `menu_name` = '产品档案' AND `parent_id` = @base_data_menu_id);

-- 7. 插入按钮权限
-- 新增产品
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @product_menu_id, '新增产品', 3, 'erp:product:add', 1, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '新增产品' AND `parent_id` = @product_menu_id
);

-- 编辑产品
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @product_menu_id, '编辑产品', 3, 'erp:product:edit', 2, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '编辑产品' AND `parent_id` = @product_menu_id
);

-- 删除产品
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`)
SELECT @product_menu_id, '删除产品', 3, 'erp:product:delete', 3, 1, 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `menu_name` = '删除产品' AND `parent_id` = @product_menu_id
);

-- 8. 给超级管理员分配所有新增的菜单权限
-- 先删除已有的关联（避免重复）
DELETE FROM `sys_role_menu` 
WHERE `role_id` = 1 
AND `menu_id` IN (
    SELECT `id` FROM `sys_menu` WHERE `path` LIKE '/erp%' OR `menu_name` LIKE '产品%' OR `menu_name` LIKE '基础数据%'
);

-- 插入新的关联
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu` 
WHERE `path` LIKE '/erp%' OR `menu_name` LIKE '产品%' OR `menu_name` LIKE '基础数据%' OR `menu_name` = '产品与BOM管理';

-- 9. 插入接口权限
-- 查询产品列表
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:product:list', '查询产品列表', '产品管理', 'GET', '/api/erp/products', '分页查询产品列表', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:product:list'
);

-- 新增产品
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:product:add', '新增产品', '产品管理', 'POST', '/api/erp/products', '创建新产品', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:product:add'
);

-- 编辑产品
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:product:edit', '编辑产品', '产品管理', 'PUT', '/api/erp/products/{id}', '更新产品信息', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:product:edit'
);

-- 删除产品
INSERT INTO `sys_api_permission` (`permission_key`, `api_name`, `module`, `request_method`, `api_path`, `description`, `status`)
SELECT 'erp:product:delete', '删除产品', '产品管理', 'DELETE', '/api/erp/products/{id}', '删除指定产品', 1
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_api_permission` WHERE `permission_key` = 'erp:product:delete'
);

-- 10. 给超级管理员分配接口权限
DELETE FROM `sys_role_api` 
WHERE `role_id` = 1 
AND `api_id` IN (
    SELECT `id` FROM `sys_api_permission` WHERE `permission_key` LIKE 'erp:product:%'
);

INSERT INTO `sys_role_api` (`role_id`, `api_id`)
SELECT 1, `id` FROM `sys_api_permission` WHERE `permission_key` LIKE 'erp:product:%';

SELECT '菜单数据插入完成！' AS message;
