import request from '@/utils/request'

export interface GanttTaskVO {
  id: number
  taskNo: string
  taskName: string
  productId: number | null
  productCode: string
  productName: string
  specification: string
  plannedQuantity: number
  actualQuantity: number
  planStartDate: string
  planEndDate: string
  actualStartDate: string | null
  actualEndDate: string | null
  resourceId: number | null
  resourceCode: string
  resourceName: string
  priority: number
  priorityName: string
  status: number
  statusName: string
  progress: number
  color: string
  remark: string
}

export interface GanttResourceVO {
  resourceType: string
  resourceId: number
  resourceCode: string
  resourceName: string
  resourceTypeDesc: string
  tasks: GanttTaskVO[]
}

export interface GanttGroupVO {
  groupType: string
  groupTypeName: string
  resources: GanttResourceVO[]
}

export interface GanttChartDataVO {
  minDate: string
  maxDate: string
  groups: GanttGroupVO[]
  allTasks: GanttTaskVO[]
}

export interface GanttQueryDTO {
  groupBy?: string
  startDate?: string
  endDate?: string
  status?: number
  priority?: number
  equipmentCode?: string
  productCode?: string
  mpsNo?: string
}

export interface GanttDragDTO {
  id: number
  newStartDate?: string
  newEndDate?: string
  newResourceId?: number
  newResourceType?: string
  newPriority?: number
  reason?: string
}

export function getGanttByEquipment(params: GanttQueryDTO) {
  return request.get('/erp/gantt/by-equipment', { params })
}

export function getGanttByProduct(params: GanttQueryDTO) {
  return request.get('/erp/gantt/by-product', { params })
}

export function getGanttByPriority(params: GanttQueryDTO) {
  return request.get('/erp/gantt/by-priority', { params })
}

export function dragAdjustPlan(data: GanttDragDTO) {
  return request.post('/erp/gantt/drag', data)
}

export function reschedulePlan(data: GanttDragDTO) {
  return request.post('/erp/gantt/reschedule', data)
}

export function rescheduleAll() {
  return request.post('/erp/gantt/reschedule-all')
}

export function generateTestData() {
  return request.post('/erp/gantt/generate-test-data')
}
