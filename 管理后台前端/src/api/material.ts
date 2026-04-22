import request from '@/utils/request'

// 物料列表（分页）
export function getMaterialList(params: any) {
  return request.get('/erp/materials', { params })
}

// 物料详情
export function getMaterialDetail(id: number) {
  return request.get(`/erp/materials/${id}`)
}

// 创建物料
export function createMaterial(data: any) {
  return request.post('/erp/materials', data)
}

// 更新物料
export function updateMaterial(id: number, data: any) {
  return request.put(`/erp/materials/${id}`, data)
}

// 删除物料
export function deleteMaterial(id: number) {
  return request.delete(`/erp/materials/${id}`)
}

// 更新物料状态
export function updateMaterialStatus(id: number, status: number) {
  return request.put(`/erp/materials/${id}/status`, { status })
}
