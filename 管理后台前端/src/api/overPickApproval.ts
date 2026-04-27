import request from '@/utils/request'

export function getOverPickApprovalList(params: any) {
  return request.get('/erp/over-pick-approvals/list', { params })
}

export function getOverPickApprovalDetail(id: number) {
  return request.get(`/erp/over-pick-approvals/${id}`)
}

export function createOverPickApproval(data: any) {
  return request.post('/erp/over-pick-approvals', data)
}

export function updateOverPickApproval(data: any) {
  return request.put('/erp/over-pick-approvals', data)
}

export function deleteOverPickApproval(id: number) {
  return request.delete(`/erp/over-pick-approvals/${id}`)
}

export function submitApproval(id: number) {
  return request.post(`/erp/over-pick-approvals/submit-approval/${id}`)
}

export function approvalOverPick(data: any) {
  return request.post('/erp/over-pick-approvals/approval', data)
}

export function cancelOverPick(id: number) {
  return request.post(`/erp/over-pick-approvals/cancel/${id}`)
}
