import request from '@/utils/request'

export function getEquipmentList(params: any) {
  return request.get('/erp/equipments', { params })
}

export function getEquipmentDetail(id: number) {
  return request.get(`/erp/equipments/${id}`)
}

export function createEquipment(data: any) {
  return request.post('/erp/equipments', data)
}

export function updateEquipment(id: number, data: any) {
  return request.put(`/erp/equipments/${id}`, data)
}

export function deleteEquipment(id: number) {
  return request.delete(`/erp/equipments/${id}`)
}

export function updateEquipmentStatus(id: number, status: number) {
  return request.put(`/erp/equipments/${id}/status?status=${status}`)
}

export function updateEquipmentStatusBatch(ids: number[], status: number) {
  return request.put('/erp/equipments/batch/status', { ids, status })
}

export function deleteEquipmentBatch(ids: number[]) {
  return request.delete('/erp/equipments/batch', { data: { ids } })
}
