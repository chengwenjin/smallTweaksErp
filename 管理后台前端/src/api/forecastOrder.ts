import request from '@/utils/request'

export function getForecastOrderList(params: any) {
  return request.get('/erp/forecast-orders', { params })
}

export function getForecastOrderDetail(id: number) {
  return request.get(`/erp/forecast-orders/${id}`)
}

export function createForecastOrder(data: any) {
  return request.post('/erp/forecast-orders', data)
}

export function updateForecastOrder(id: number, data: any) {
  return request.put(`/erp/forecast-orders/${id}`, data)
}

export function deleteForecastOrder(id: number) {
  return request.delete(`/erp/forecast-orders/${id}`)
}

export function updateForecastOrderStatus(id: number, status: number) {
  return request.put(`/erp/forecast-orders/${id}/status`, { status })
}
