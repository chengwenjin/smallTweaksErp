import request from '@/utils/request'

export function getFqcInspectionList(params: any) {
  return request.get('/erp/fqc-inspections', { params })
}

export function getFqcInspectionDetail(id: number) {
  return request.get(`/erp/fqc-inspections/${id}`)
}

export function createFqcInspection(data: any) {
  return request.post('/erp/fqc-inspections', data)
}

export function updateFqcInspection(id: number, data: any) {
  return request.put(`/erp/fqc-inspections/${id}`, data)
}

export function deleteFqcInspection(id: number) {
  return request.delete(`/erp/fqc-inspections/${id}`)
}

export function submitFqcInspection(id: number) {
  return request.put(`/erp/fqc-inspections/${id}/submit`)
}

export function completeFqcInspection(id: number, data: any) {
  return request.put(`/erp/fqc-inspections/${id}/complete`, data)
}

export function disposalFqcInspection(id: number, data: any) {
  return request.put(`/erp/fqc-inspections/${id}/disposal`, data)
}

export function cancelFqcInspection(id: number) {
  return request.put(`/erp/fqc-inspections/${id}/cancel`)
}
