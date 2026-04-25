-- 检查设备表数据
SELECT id, equipment_code, equipment_name, is_deleted, status 
FROM erp_equipment 
LIMIT 10;

-- 检查组表数据
SELECT id, group_code, group_name, is_deleted, status 
FROM erp_work_group 
LIMIT 10;

-- 修复设备表的is_deleted字段
UPDATE erp_equipment SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0;

-- 修复班组表的is_deleted字段
UPDATE erp_work_group SET is_deleted = 0 WHERE is_deleted IS NULL OR is_deleted != 0;

-- 检查修复后的数据
SELECT '设备表数量' as table_name, COUNT(*) as count FROM erp_equipment WHERE is_deleted = 0
UNION ALL
SELECT '班组表数量' as table_name, COUNT(*) as count FROM erp_work_group WHERE is_deleted = 0;
