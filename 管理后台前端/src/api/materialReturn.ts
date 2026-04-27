import request from '@/utils/request'

export function getMaterialReturnList(params: any) {
  return request.get('/erp/material-returns/list', { params })
}

export function getMaterialReturnDetail(id: number) {
  return request.get(`/erp/material-returns/${id}`)
}

export function createMaterialReturn(data: any) {
  return request.post('/erp/material-returns', data)
}

export function updateMaterialReturn(data: any) {
  return request.put('/erp/material-returns', data)
}

export function deleteMaterialReturn(id: number) {
  return request.delete(`/erp/material-returns/${id}`)
}

export function submitApproval(id: number) {
  return request.post(`/erp/material-returns/submit-approval/${id}`)
}

export function approvalMaterialReturn(id: number, approved: boolean, opinion?: string) {
  return request.post('/erp/material-returns/approval', null, {
    params: { id, approved, opinion }
  })
}

export function completeMaterialReturn(id: number) {
  return request.post(`/erp/material-returns/complete/${id}`)
}
