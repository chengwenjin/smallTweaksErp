import request from '@/utils/request'

export function getWorkGroupList(params: any) {
  return request.get('/erp/work-groups', { params })
}

export function getWorkGroupDetail(id: number) {
  return request.get(`/erp/work-groups/${id}`)
}

export function createWorkGroup(data: any) {
  return request.post('/erp/work-groups', data)
}

export function updateWorkGroup(id: number, data: any) {
  return request.put(`/erp/work-groups/${id}`, data)
}

export function deleteWorkGroup(id: number) {
  return request.delete(`/erp/work-groups/${id}`)
}

export function updateWorkGroupStatus(id: number, status: number) {
  return request.put(`/erp/work-groups/${id}/status?status=${status}`)
}

export function updateWorkGroupStatusBatch(ids: number[], status: number) {
  return request.put('/erp/work-groups/batch/status', { ids, status })
}

export function deleteWorkGroupBatch(ids: number[]) {
  return request.delete('/erp/work-groups/batch', { data: { ids } })
}
