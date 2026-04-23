import request from '@/utils/request'

export function getSalesOrderList(params: any) {
  return request.get('/erp/sales-orders', { params })
}

export function getSalesOrderDetail(id: number) {
  return request.get(`/erp/sales-orders/${id}`)
}

export function createSalesOrder(data: any) {
  return request.post('/erp/sales-orders', data)
}

export function updateSalesOrder(id: number, data: any) {
  return request.put(`/erp/sales-orders/${id}`, data)
}

export function deleteSalesOrder(id: number) {
  return request.delete(`/erp/sales-orders/${id}`)
}

export function updateSalesOrderStatus(id: number, status: number) {
  return request.put(`/erp/sales-orders/${id}/status`, { status })
}
