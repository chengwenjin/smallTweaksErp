import request from '@/utils/request'

export function getIqcInspectionList(params: any) {
  return request.get('/erp/iqc-inspections', { params })
}

export function getIqcInspectionDetail(id: number) {
  return request.get(`/erp/iqc-inspections/${id}`)
}

export function createIqcInspection(data: any) {
  return request.post('/erp/iqc-inspections', data)
}

export function updateIqcInspection(id: number, data: any) {
  return request.put(`/erp/iqc-inspections/${id}`, data)
}

export function deleteIqcInspection(id: number) {
  return request.delete(`/erp/iqc-inspections/${id}`)
}

export function submitIqcInspection(id: number) {
  return request.put(`/erp/iqc-inspections/${id}/submit`)
}

export function completeIqcInspection(id: number, data: any) {
  return request.put(`/erp/iqc-inspections/${id}/complete`, data)
}

export function disposalIqcInspection(id: number, data: any) {
  return request.put(`/erp/iqc-inspections/${id}/disposal`, data)
}

export function cancelIqcInspection(id: number) {
  return request.put(`/erp/iqc-inspections/${id}/cancel`)
}
