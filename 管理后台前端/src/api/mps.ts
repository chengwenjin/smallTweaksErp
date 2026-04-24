import request from '@/utils/request'

export function getMpsList(params: any) {
  return request.get('/erp/mps', { params })
}

export function getMpsDetail(id: number) {
  return request.get(`/erp/mps/${id}`)
}

export function createMps(data: any) {
  return request.post('/erp/mps', data)
}

export function updateMps(id: number, data: any) {
  return request.put(`/erp/mps/${id}`, data)
}

export function deleteMps(id: number) {
  return request.delete(`/erp/mps/${id}`)
}

export function updateMpsStatus(id: number, status: number) {
  return request.put(`/erp/mps/${id}/status?status=${status}`)
}

export function confirmMps(id: number) {
  return request.post(`/erp/mps/${id}/confirm`)
}

export function cancelMps(id: number) {
  return request.post(`/erp/mps/${id}/cancel`)
}

export function generateMpsFromNetRequirement() {
  return request.post('/erp/mps/generate-from-net-requirement')
}

export function updateMpsStatusBatch(ids: number[], status: number) {
  return request.put('/erp/mps/batch/status', { ids, status })
}

export function deleteMpsBatch(ids: number[]) {
  return request.delete('/erp/mps/batch', { data: { ids } })
}
