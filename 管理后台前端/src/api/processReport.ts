import request from '@/utils/request'

export function getProcessReportList(params: any) {
  return request.get('/erp/process-reports/list', { params })
}

export function getProcessReportDetail(id: number) {
  return request.get(`/erp/process-reports/${id}`)
}

export function createProcessReport(data: any) {
  return request.post('/erp/process-reports', data)
}

export function updateProcessReport(data: any) {
  return request.put('/erp/process-reports', data)
}

export function deleteProcessReport(id: number) {
  return request.delete(`/erp/process-reports/${id}`)
}

export function scanReport(data: any) {
  return request.post('/erp/process-reports/scan', data)
}
