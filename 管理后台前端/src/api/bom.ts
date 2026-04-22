import request from '@/utils/request'

// BOM列表（分页）
export function getBomList(params: any) {
  return request.get('/erp/boms', { params })
}

// BOM详情
export function getBomDetail(id: number) {
  return request.get(`/erp/boms/${id}`)
}

// BOM层级结构
export function getBomTree(parentId: number, parentType: number) {
  return request.get('/erp/boms/tree', { params: { parentId, parentType } })
}

// 创建BOM
export function createBom(data: any) {
  return request.post('/erp/boms', data)
}

// 更新BOM
export function updateBom(id: number, data: any) {
  return request.put(`/erp/boms/${id}`, data)
}

// 删除BOM
export function deleteBom(id: number) {
  return request.delete(`/erp/boms/${id}`)
}

// 更新BOM状态
export function updateBomStatus(id: number, status: number) {
  return request.put(`/erp/boms/${id}/status`, { status })
}

// 获取产品列表（用于选择）
export function getProductList() {
  return request.get('/erp/products', { params: { pageNum: 1, pageSize: 100 } })
}

// 获取物料列表（用于选择）
export function getMaterialList() {
  return request.get('/erp/materials', { params: { pageNum: 1, pageSize: 100 } })
}
