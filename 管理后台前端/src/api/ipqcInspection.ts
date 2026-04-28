import request from '@/utils/request'

export function getIpqcInspectionList(params: any) {
  return request.get('/erp/ipqc-inspections', { params })
}

export function getIpqcInspectionDetail(id: number) {
  return request.get(`/erp/ipqc-inspections/${id}`)
}

export function createIpqcInspection(data: any) {
  return request.post('/erp/ipqc-inspections', data)
}

export function updateIpqcInspection(id: number, data: any) {
  return request.put(`/erp/ipqc-inspections/${id}`, data)
}

export function deleteIpqcInspection(id: number) {
  return request.delete(`/erp/ipqc-inspections/${id}`)
}

export function submitIpqcInspection(id: number) {
  return request.put(`/erp/ipqc-inspections/${id}/submit`)
}

export function completeIpqcInspection(id: number, data: any) {
  return request.put(`/erp/ipqc-inspections/${id}/complete`, data)
}

export function disposalIpqcInspection(id: number, data: any) {
  return request.put(`/erp/ipqc-inspections/${id}/disposal`, data)
}

export function cancelIpqcInspection(id: number) {
  return request.put(`/erp/ipqc-inspections/${id}/cancel`)
}
