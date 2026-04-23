import request from '@/utils/request'

export function getNetRequirementList(params: any) {
  return request.get('/erp/net-requirements', { params })
}

export function getNetRequirementDetail(id: number) {
  return request.get(`/erp/net-requirements/${id}`)
}

export function calculateNetRequirements() {
  return request.post('/erp/net-requirements/calculate')
}

export function confirmNetRequirement(id: number) {
  return request.post(`/erp/net-requirements/${id}/confirm`)
}

export function deleteNetRequirement(id: number) {
  return request.delete(`/erp/net-requirements/${id}`)
}

export function updateNetRequirementStatus(id: number, status: number) {
  return request.put(`/erp/net-requirements/${id}/status`, { status })
}
