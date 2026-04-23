import request from '@/utils/request'

export function getDemandSourceList(params: any) {
  return request.get('/erp/demand-sources', { params })
}

export function getDemandSourceDetail(id: number) {
  return request.get(`/erp/demand-sources/${id}`)
}

export function syncFromSalesOrders() {
  return request.post('/erp/demand-sources/sync-sales')
}

export function syncFromForecastOrders() {
  return request.post('/erp/demand-sources/sync-forecast')
}

export function syncAllDemandSources() {
  return request.post('/erp/demand-sources/sync-all')
}

export function deleteDemandSource(id: number) {
  return request.delete(`/erp/demand-sources/${id}`)
}

export function updateDemandSourceStatus(id: number, status: number) {
  return request.put(`/erp/demand-sources/${id}/status`, { status })
}
