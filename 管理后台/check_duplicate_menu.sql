-- 检查重复的 permission_key
SELECT permission_key, COUNT(*) as cnt 
FROM sys_menu 
WHERE permission_key IS NOT NULL 
GROUP BY permission_key 
HAVING COUNT(*) > 1;

-- 查看所有使用 erp:bom-version:list 的菜单
SELECT id, parent_id, menu_name, menu_type, permission_key, path 
FROM sys_menu 
WHERE permission_key = 'erp:bom-version:list';
