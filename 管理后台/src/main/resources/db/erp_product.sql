-- ============================================
-- 产品档案表
-- ============================================

-- 创建产品档案表
CREATE TABLE IF NOT EXISTS `erp_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_code` VARCHAR(50) NOT NULL COMMENT '产品编码（全局唯一）',
    `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
    `specification` VARCHAR(200) DEFAULT NULL COMMENT '规格型号',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '计量单位',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '产品分类',
    `brand` VARCHAR(50) DEFAULT NULL COMMENT '品牌',
    `technical_params` TEXT DEFAULT NULL COMMENT '技术参数（JSON格式）',
    `description` TEXT DEFAULT NULL COMMENT '产品描述',
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
    UNIQUE KEY `uk_product_code` (`product_code`),
    KEY `idx_product_name` (`product_name`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品档案表';

-- 插入示例数据
INSERT INTO `erp_product` (`product_code`, `product_name`, `specification`, `unit`, `category`, `brand`, `technical_params`, `description`, `status`) VALUES
('PRD001', '台式计算机', 'ThinkCentre M90a', '台', '计算机设备', '联想', '{"cpu":"Intel i5-10400","memory":"16GB","storage":"512GB SSD","monitor":"23.8英寸"}', '联想台式计算机，商务办公用', 1),
('PRD002', '笔记本电脑', 'ThinkPad X1 Carbon', '台', '计算机设备', '联想', '{"cpu":"Intel i7-1165G7","memory":"16GB","storage":"1TB SSD","screen":"14英寸"}', '联想轻薄商务笔记本', 1),
('PRD003', '激光打印机', 'HP LaserJet Pro M404dn', '台', '办公设备', '惠普', '{"print_speed":"40ppm","resolution":"1200x1200dpi","connectivity":"USB, Ethernet"}', '惠普黑白激光打印机', 1),
('PRD004', '投影仪', 'Epson CB-X50', '台', '办公设备', '爱普生', '{"brightness":"3600流明","resolution":"1024x768","contrast":"16000:1"}', '爱普生商务投影仪', 1),
('PRD005', '网络交换机', 'Cisco Catalyst 2960', '台', '网络设备', '思科', '{"ports":"24口","speed":"10/100/1000Mbps","type":"千兆交换机"}', '思科千兆网络交换机', 1);

-- ============================================
-- 菜单数据（如果需要添加菜单）
-- ============================================

-- 插入菜单（如果需要）
-- 注意：这里的 parent_id 需要根据实际菜单结构进行调整
-- 以下是示例，实际使用时需要根据系统的菜单结构进行调整

-- 先查询是否已经存在"产品与BOM管理"目录
-- 如果不存在，可以插入：
-- INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES
-- (0, '产品与BOM管理', 1, NULL, '/erp', 'Layout', 'Goods', 70, 1, 1, 1);

-- 然后在该目录下插入子菜单
-- INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES
-- (@parent_id, '基础数据管理', 1, NULL, 'base-data', 'Layout', 'Setting', 1, 1, 1, 1);

-- 然后在基础数据管理下插入产品档案菜单
-- INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `path`, `component`, `icon`, `sort_order`, `is_visible`, `status`, `is_system`) VALUES
-- (@parent_id, '产品档案', 2, 'erp:product:list', 'product', 'erp/product/index', 'Goods', 1, 1, 1, 1);

-- 插入按钮权限
-- INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `permission_key`, `sort_order`, `status`, `is_system`) VALUES
-- (@product_menu_id, '新增产品', 3, 'erp:product:add', 1, 1, 1),
-- (@product_menu_id, '编辑产品', 3, 'erp:product:edit', 2, 1, 1),
-- (@product_menu_id, '删除产品', 3, 'erp:product:delete', 3, 1, 1);
