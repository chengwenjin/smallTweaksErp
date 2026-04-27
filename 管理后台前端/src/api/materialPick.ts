import request from '@/utils/request'

export function getMaterialPickList(params: any) {
  return request.get('/erp/material-picks/list', { params })
}

export function getMaterialPickDetail(id: number) {
  return request.get(`/erp/material-picks/${id}`)
}

export function createMaterialPick(data: any) {
  return request.post('/erp/material-picks', data)
}

export function updateMaterialPick(data: any) {
  return request.put('/erp/material-picks', data)
}

export function deleteMaterialPick(id: number) {
  return request.delete(`/erp/material-picks/${id}`)
}

export function submitApproval(id: number) {
  return request.post(`/erp/material-picks/submit-approval/${id}`)
}

export function approvalMaterialPick(id: number, approved: boolean, opinion?: string) {
  return request.post('/erp/material-picks/approval', null, {
    params: { id, approved, opinion }
  })
}

export function pickMaterial(id: number, quantity: number) {
  return request.post('/erp/material-picks/pick', null, {
    params: { id, quantity }
  })
}
