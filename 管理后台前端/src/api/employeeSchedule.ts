import request from '@/utils/request'

export function getEmployeeScheduleList(params: any) {
  return request.get('/erp/employee-schedules', { params })
}

export function getEmployeeScheduleDetail(id: number) {
  return request.get(`/erp/employee-schedules/${id}`)
}

export function createEmployeeSchedule(data: any) {
  return request.post('/erp/employee-schedules', data)
}

export function updateEmployeeSchedule(id: number, data: any) {
  return request.put(`/erp/employee-schedules/${id}`, data)
}

export function deleteEmployeeSchedule(id: number) {
  return request.delete(`/erp/employee-schedules/${id}`)
}

export function updateEmployeeScheduleStatus(id: number, status: number) {
  return request.put(`/erp/employee-schedules/${id}/status?status=${status}`)
}

export function updateEmployeeScheduleStatusBatch(ids: number[], status: number) {
  return request.put('/erp/employee-schedules/batch/status', { ids, status })
}

export function deleteEmployeeScheduleBatch(ids: number[]) {
  return request.delete('/erp/employee-schedules/batch', { data: { ids } })
}
