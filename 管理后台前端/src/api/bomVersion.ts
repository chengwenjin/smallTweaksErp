import request from '@/utils/request'

// BOM版本列表（分页）
export function getBomVersionList(params: any) {
  return request.get('/erp/bom-versions', { params })
}

// BOM版本详情
export function getBomVersionDetail(id: number) {
  return request.get(`/erp/bom-versions/${id}`)
}

// 创建BOM版本
export function createBomVersion(data: any) {
  return request.post('/erp/bom-versions', data)
}

// 更新BOM版本
export function updateBomVersion(id: number, data: any) {
  return request.put(`/erp/bom-versions/${id}`, data)
}

// 删除BOM版本
export function deleteBomVersion(id: number) {
  return request.delete(`/erp/bom-versions/${id}`)
}

// 更新BOM版本状态
export function updateBomVersionStatus(id: number, status: number) {
  return request.put(`/erp/bom-versions/${id}/status`, { status })
}

// 替代料列表（分页）
export function getAlternativeMaterialList(params: any) {
  return request.get('/erp/alternative-materials', { params })
}

// 替代料详情
export function getAlternativeMaterialDetail(id: number) {
  return request.get(`/erp/alternative-materials/${id}`)
}

// 获取主料的替代料列表
export function getAlternativeMaterialsByMainMaterial(mainMaterialId: number, mainMaterialType: number) {
  return request.get('/erp/alternative-materials/by-main', { params: { mainMaterialId, mainMaterialType } })
}

// 创建替代料
export function createAlternativeMaterial(data: any) {
  return request.post('/erp/alternative-materials', data)
}

// 更新替代料
export function updateAlternativeMaterial(id: number, data: any) {
  return request.put(`/erp/alternative-materials/${id}`, data)
}

// 删除替代料
export function deleteAlternativeMaterial(id: number) {
  return request.delete(`/erp/alternative-materials/${id}`)
}

// 更新替代料状态
export function updateAlternativeMaterialStatus(id: number, status: number) {
  return request.put(`/erp/alternative-materials/${id}/status`, { status })
}
