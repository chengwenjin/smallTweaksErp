import request from '@/utils/request'

export function getInventoryList(params: any) {
  return request.get('/erp/inventories', { params })
}

export function getInventoryDetail(id: number) {
  return request.get(`/erp/inventories/${id}`)
}

export function createInventory(data: any) {
  return request.post('/erp/inventories', data)
}

export function updateInventory(id: number, data: any) {
  return request.put(`/erp/inventories/${id}`, data)
}

export function deleteInventory(id: number) {
  return request.delete(`/erp/inventories/${id}`)
}

export function updateInventoryStatus(id: number, status: number) {
  return request.put(`/erp/inventories/${id}/status`, { status })
}
