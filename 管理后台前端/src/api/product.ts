import request from '@/utils/request'

// 产品列表（分页）
export function getProductList(params: any) {
  return request.get('/erp/products', { params })
}

// 产品详情
export function getProductDetail(id: number) {
  return request.get(`/erp/products/${id}`)
}

// 创建产品
export function createProduct(data: any) {
  return request.post('/erp/products', data)
}

// 更新产品
export function updateProduct(id: number, data: any) {
  return request.put(`/erp/products/${id}`, data)
}

// 删除产品
export function deleteProduct(id: number) {
  return request.delete(`/erp/products/${id}`)
}

// 更新产品状态
export function updateProductStatus(id: number, status: number) {
  return request.put(`/erp/products/${id}/status`, { status })
}
