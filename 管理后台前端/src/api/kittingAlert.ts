import request from '@/utils/request'

export function getKittingAlertList(params: any) {
  return request.get('/erp/kitting-alerts', { params })
}

export function getKittingAlertDetail(id: number) {
  return request.get(`/erp/kitting-alerts/${id}`)
}

export function createKittingAlert(data: any) {
  return request.post('/erp/kitting-alerts', data)
}

export function updateKittingAlert(id: number, data: any) {
  return request.put(`/erp/kitting-alerts/${id}`, data)
}

export function deleteKittingAlert(id: number) {
  return request.delete(`/erp/kitting-alerts/${id}`)
}

export function updateKittingAlertStatus(id: number, status: number) {
  return request.put(`/erp/kitting-alerts/${id}/status?status=${status}`)
}

export function calculateKittingAlerts() {
  return request.post('/erp/kitting-alerts/calculate')
}

export function syncToScrm(id: number) {
  return request.post(`/erp/kitting-alerts/${id}/sync-to-scrm`)
}

export function syncToScrmBatch(ids: number[]) {
  return request.post('/erp/kitting-alerts/batch/sync-to-scrm', { ids })
}

export function updateKittingAlertStatusBatch(ids: number[], status: number) {
  return request.put('/erp/kitting-alerts/batch/status', { ids, status })
}

export function deleteKittingAlertBatch(ids: number[]) {
  return request.delete('/erp/kitting-alerts/batch', { data: { ids } })
}
