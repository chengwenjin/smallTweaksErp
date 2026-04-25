import request from '@/utils/request'

export function getWorkOrderList(params: any) {
  return request.get('/erp/work-orders/list', { params })
}

export function getWorkOrderDetail(id: number) {
  return request.get(`/erp/work-orders/${id}`)
}

export function createWorkOrder(data: any) {
  return request.post('/erp/work-orders', data)
}

export function updateWorkOrder(data: any) {
  return request.put('/erp/work-orders', data)
}

export function deleteWorkOrder(id: number) {
  return request.delete(`/erp/work-orders/${id}`)
}

export function submitApproval(id: number) {
  return request.post(`/erp/work-orders/submit-approval/${id}`)
}

export function submitApprovalBatch(ids: number[]) {
  return request.post('/erp/work-orders/submit-approval/batch', ids)
}

export function approvalWorkOrder(data: any) {
  return request.post('/erp/work-orders/approval', data)
}

export function issueWorkOrder(data: any) {
  return request.post('/erp/work-orders/issue', data)
}

export function pickWorkOrder(data: any) {
  return request.post('/erp/work-orders/pick', data)
}

export function startProduction(id: number) {
  return request.post(`/erp/work-orders/start-production/${id}`)
}

export function reportWorkOrder(data: any) {
  return request.post('/erp/work-orders/report', data)
}

export function pendingStorage(id: number) {
  return request.post(`/erp/work-orders/pending-storage/${id}`)
}

export function completeWorkOrder(data: any) {
  return request.post('/erp/work-orders/complete', data)
}

export function cancelWorkOrder(id: number) {
  return request.post(`/erp/work-orders/cancel/${id}`)
}

export function getWorkOrderLogs(workOrderId: number) {
  return request.get(`/erp/work-orders/logs/${workOrderId}`)
}

export function getWorkOrderDashboard() {
  return request.get('/erp/work-orders/dashboard')
}
