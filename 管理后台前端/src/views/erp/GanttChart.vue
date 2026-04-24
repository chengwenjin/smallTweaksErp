<template>
  <div class="gantt-chart-container">
    <el-card class="toolbar-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分组方式">
          <el-select v-model="searchForm.groupBy" @change="fetchGanttData" style="width: 140px">
            <el-option label="按设备分组" value="equipment" />
            <el-option label="按产品分组" value="product" />
            <el-option label="按优先级分组" value="priority" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" @change="fetchGanttData" clearable placeholder="全部" style="width: 120px">
            <el-option label="草稿" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="生产中" :value="3" />
            <el-option label="已完成" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchGanttData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="success" @click="handleGenerateTestData">
            <el-icon><Plus /></el-icon>
            生成测试数据
          </el-button>
          <el-button type="warning" @click="handleRescheduleAll">
            <el-icon><Tools /></el-icon>
            全局重排
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="gantt-card">
      <div class="gantt-wrapper" ref="ganttWrapper">
        <div class="gantt-header">
          <div class="resource-header">
            <span>资源</span>
          </div>
          <div class="timeline-header" ref="timelineHeader">
            <div 
              v-for="(day, index) in timelineDays" 
              :key="index" 
              :class="['timeline-day', { weekend: isWeekend(day.date), today: isToday(day.date) }]"
              :style="{ width: dayWidth + 'px' }"
            >
              <div class="day-label">{{ day.day }}</div>
              <div class="date-label">{{ day.month }}/{{ day.date.getDate() }}</div>
            </div>
          </div>
        </div>

        <div class="gantt-body" @scroll="handleScroll">
          <div class="resource-list" ref="resourceList">
            <div 
              v-for="(group, groupIndex) in chartData.groups" 
              :key="groupIndex"
              class="resource-group"
            >
              <div class="group-title">
                <el-icon><Folder /></el-icon>
                {{ group.groupTypeName }}
              </div>
              <div 
                v-for="(resource, resourceIndex) in group.resources" 
                :key="resourceIndex"
                class="resource-row"
                :style="{ height: rowHeight + 'px' }"
              >
                <div class="resource-info">
                  <el-icon v-if="resource.resourceType === 'EQUIPMENT'"><Monitor /></el-icon>
                  <el-icon v-else-if="resource.resourceType === 'PRODUCT'"><Box /></el-icon>
                  <el-icon v-else><Flag /></el-icon>
                  <span class="resource-name">{{ resource.resourceName }}</span>
                  <span class="resource-code">{{ resource.resourceCode }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="timeline-body" ref="timelineBody">
            <div class="grid-overlay" :style="{ width: totalTimelineWidth + 'px' }">
              <div 
                v-for="(day, index) in timelineDays" 
                :key="index" 
                :class="['grid-column', { weekend: isWeekend(day.date), today: isToday(day.date) }]"
                :style="{ left: index * dayWidth + 'px', width: dayWidth + 'px' }"
              ></div>
            </div>

            <div 
              v-for="(group, groupIndex) in chartData.groups" 
              :key="'tasks-' + groupIndex"
              class="task-group"
            >
              <div class="group-placeholder"></div>
              <div 
                v-for="(resource, resourceIndex) in group.resources" 
                :key="'tasks-' + resourceIndex"
                class="task-row"
                :style="{ height: rowHeight + 'px', minWidth: totalTimelineWidth + 'px' }"
              >
                <div
                  v-for="task in resource.tasks"
                  :key="task.id"
                  class="task-bar"
                  :class="{
                    'task-dragging': draggingTaskId === task.id,
                    'task-selected': selectedTaskId === task.id
                  }"
                  :style="getTaskBarStyle(task)"
                  :draggable="task.status !== 4"
                  @mousedown="handleTaskMouseDown(task, $event)"
                  @click="handleTaskClick(task)"
                >
                  <div class="task-label">
                    <span class="task-name">{{ task.taskName }}</span>
                    <span class="task-qty">{{ task.plannedQuantity }}</span>
                  </div>
                  <div class="task-progress" :style="{ width: task.progress + '%' }"></div>
                  <el-tooltip :content="getTaskTooltip(task)" placement="top">
                    <div class="task-tooltip-trigger"></div>
                  </el-tooltip>
                </div>
              </div>
            </div>

            <div 
              v-if="isDragging && dragMarkerDate" 
              class="drag-marker"
              :style="{ left: getDragMarkerLeft() + 'px' }"
            >
              <el-icon><Pointer /></el-icon>
              <span>{{ formatDate(dragMarkerDate) }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="taskDetailVisible"
      title="计划详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="selectedTask">
        <el-descriptions-item label="计划编号">{{ selectedTask.taskNo }}</el-descriptions-item>
        <el-descriptions-item label="计划名称">{{ selectedTask.taskName }}</el-descriptions-item>
        <el-descriptions-item label="产品">{{ selectedTask.productName }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ selectedTask.specification }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ selectedTask.plannedQuantity }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="selectedTask.priority === 1 ? 'danger' : selectedTask.priority === 2 ? 'warning' : 'info'">
            {{ selectedTask.priorityName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="计划开始">{{ selectedTask.planStartDate }}</el-descriptions-item>
        <el-descriptions-item label="计划结束">{{ selectedTask.planEndDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(selectedTask.status)">
            {{ selectedTask.statusName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="进度">{{ selectedTask.progress }}%</el-descriptions-item>
        <el-descriptions-item label="设备">{{ selectedTask.resourceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备编码">{{ selectedTask.resourceCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ selectedTask.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button 
          v-if="selectedTask && selectedTask.status !== 4"
          type="primary" 
          @click="handleEditTask"
        >
          编辑
        </el-button>
        <el-button @click="taskDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="editDialogVisible"
      title="调整计划"
      width="500px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="editForm.planStartDate"
            type="date"
            placeholder="请选择开始日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="editForm.planEndDate"
            type="date"
            placeholder="请选择结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="editForm.priority" style="width: 100%">
            <el-option label="高" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="低" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input
            v-model="editForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button 
          type="primary" 
          @click="handleSaveEdit"
          :loading="submitLoading"
        >
          确定并重排
        </el-button>
        <el-button @click="editDialogVisible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Refresh, Plus, Tools, Folder, Monitor, Box, Flag, Pointer 
} from '@element-plus/icons-vue'
import { 
  getGanttByEquipment, 
  getGanttByProduct, 
  getGanttByPriority,
  dragAdjustPlan,
  reschedulePlan,
  rescheduleAll,
  generateTestData,
  type GanttChartDataVO,
  type GanttTaskVO,
  type GanttDragDTO
} from '@/api/gantt'

const dayWidth = 100
const rowHeight = 50
const groupTitleHeight = 36

const ganttWrapper = ref<HTMLElement>()
const timelineHeader = ref<HTMLElement>()
const resourceList = ref<HTMLElement>()
const timelineBody = ref<HTMLElement>()

const loading = ref(false)
const submitLoading = ref(false)
const taskDetailVisible = ref(false)
const editDialogVisible = ref(false)

const selectedTaskId = ref<number | null>(null)
const selectedTask = ref<GanttTaskVO | null>(null)
const draggingTaskId = ref<number | null>(null)
const isDragging = ref(false)
const dragStartX = ref(0)
const dragStartLeft = ref(0)
const dragMarkerDate = ref<Date | null>(null)

const dateRange = ref<[string, string] | null>(null)

const searchForm = reactive({
  groupBy: 'equipment',
  startDate: '',
  endDate: '',
  status: undefined as number | undefined,
  priority: undefined as number | undefined
})

const editForm = reactive({
  id: null as number | null,
  planStartDate: '',
  planEndDate: '',
  priority: 2,
  reason: ''
})

const chartData = ref<GanttChartDataVO>({
  minDate: '',
  maxDate: '',
  groups: [],
  allTasks: []
})

const timelineDays = computed(() => {
  const days: { date: Date; day: string; month: number }[] = []
  if (!chartData.value.minDate || !chartData.value.maxDate) return days

  const start = new Date(chartData.value.minDate)
  const end = new Date(chartData.value.maxDate)
  
  const dayNames = ['日', '一', '二', '三', '四', '五', '六']
  
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    days.push({
      date: new Date(d),
      day: dayNames[d.getDay()],
      month: d.getMonth() + 1
    })
  }
  
  return days
})

const totalTimelineWidth = computed(() => {
  return timelineDays.value.length * dayWidth
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'primary',
    3: 'warning',
    4: 'success',
    5: 'danger'
  }
  return types[status] || 'info'
}

const isWeekend = (date: Date) => {
  const day = date.getDay()
  return day === 0 || day === 6
}

const isToday = (date: Date) => {
  const today = new Date()
  return date.toDateString() === today.toDateString()
}

const formatDate = (date: Date) => {
  return date.toISOString().split('T')[0]
}

const getTaskBarStyle = (task: GanttTaskVO) => {
  const startDate = new Date(task.planStartDate)
  const endDate = new Date(task.planEndDate)
  const minDate = new Date(chartData.value.minDate)
  
  const startDiff = Math.floor((startDate.getTime() - minDate.getTime()) / (1000 * 60 * 60 * 24))
  const duration = Math.max(1, Math.floor((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1)
  
  return {
    left: startDiff * dayWidth + 5 + 'px',
    width: duration * dayWidth - 10 + 'px',
    backgroundColor: task.color,
    height: rowHeight - 10 + 'px',
    top: '5px'
  }
}

const getTaskTooltip = (task: GanttTaskVO) => {
  return `${task.taskName}\n产品: ${task.productName}\n数量: ${task.plannedQuantity}\n开始: ${task.planStartDate}\n结束: ${task.planEndDate}\n状态: ${task.statusName}`
}

const getDragMarkerLeft = () => {
  if (!dragMarkerDate.value || !chartData.value.minDate) return 0
  const minDate = new Date(chartData.value.minDate)
  const diff = Math.floor((dragMarkerDate.value.getTime() - minDate.getTime()) / (1000 * 60 * 60 * 24))
  return diff * dayWidth + dayWidth / 2
}

const fetchGanttData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm
    }
    
    let res
    if (searchForm.groupBy === 'equipment') {
      res = await getGanttByEquipment(params)
    } else if (searchForm.groupBy === 'product') {
      res = await getGanttByProduct(params)
    } else {
      res = await getGanttByPriority(params)
    }
    
    chartData.value = res.data
    console.log('甘特图数据:', chartData.value)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleDateRangeChange = (val: [string, string] | null) => {
  if (val && val.length === 2) {
    searchForm.startDate = val[0]
    searchForm.endDate = val[1]
  } else {
    searchForm.startDate = ''
    searchForm.endDate = ''
  }
  fetchGanttData()
}

const handleScroll = (e: Event) => {
  const target = e.target as HTMLElement
  if (timelineHeader.value) {
    timelineHeader.value.scrollLeft = target.scrollLeft
  }
  if (resourceList.value) {
    resourceList.value.scrollTop = target.scrollTop
  }
}

const handleTaskClick = (task: GanttTaskVO) => {
  selectedTaskId.value = task.id
  selectedTask.value = task
  taskDetailVisible.value = true
}

const handleTaskMouseDown = (task: GanttTaskVO, e: MouseEvent) => {
  if (task.status === 4) return
  
  e.preventDefault()
  draggingTaskId.value = task.id
  isDragging.value = true
  dragStartX.value = e.clientX
  
  const startDate = new Date(task.planStartDate)
  const minDate = new Date(chartData.value.minDate)
  const startDiff = Math.floor((startDate.getTime() - minDate.getTime()) / (1000 * 60 * 60 * 24))
  dragStartLeft.value = startDiff * dayWidth + 5
  
  document.addEventListener('mousemove', handleTaskMouseMove)
  document.addEventListener('mouseup', handleTaskMouseUp)
}

const handleTaskMouseMove = (e: MouseEvent) => {
  if (!isDragging.value || !timelineBody.value) return
  
  const rect = timelineBody.value.getBoundingClientRect()
  const scrollLeft = timelineBody.value.scrollLeft
  const relativeX = e.clientX - rect.left + scrollLeft
  
  const dayIndex = Math.floor(relativeX / dayWidth)
  if (dayIndex >= 0 && dayIndex < timelineDays.value.length) {
    dragMarkerDate.value = timelineDays.value[dayIndex].date
  }
}

const handleTaskMouseUp = async () => {
  if (isDragging.value && draggingTaskId.value !== null && dragMarkerDate.value) {
    const task = chartData.value.allTasks.find(t => t.id === draggingTaskId.value)
    if (task) {
      const oldStartDate = new Date(task.planStartDate)
      const oldEndDate = new Date(task.planEndDate)
      const duration = oldEndDate.getTime() - oldStartDate.getTime()
      
      const newStartDate = new Date(dragMarkerDate.value)
      const newEndDate = new Date(newStartDate.getTime() + duration)
      
      try {
        const dragData: GanttDragDTO = {
          id: task.id,
          newStartDate: newStartDate.toISOString().split('T')[0],
          newEndDate: newEndDate.toISOString().split('T')[0],
          reason: '拖拽调整'
        }
        
        await reschedulePlan(dragData)
        ElMessage.success('调整成功，已自动重排后续计划')
        fetchGanttData()
      } catch (error) {
        console.error(error)
      }
    }
  }
  
  isDragging.value = false
  draggingTaskId.value = null
  dragMarkerDate.value = null
  
  document.removeEventListener('mousemove', handleTaskMouseMove)
  document.removeEventListener('mouseup', handleTaskMouseUp)
}

const handleEditTask = () => {
  if (!selectedTask.value) return
  
  editForm.id = selectedTask.value.id
  editForm.planStartDate = selectedTask.value.planStartDate
  editForm.planEndDate = selectedTask.value.planEndDate
  editForm.priority = selectedTask.value.priority || 2
  editForm.reason = ''
  
  taskDetailVisible.value = false
  editDialogVisible.value = true
}

const handleSaveEdit = async () => {
  if (!editForm.id) return
  
  submitLoading.value = true
  try {
    const dragData: GanttDragDTO = {
      id: editForm.id,
      newStartDate: editForm.planStartDate,
      newEndDate: editForm.planEndDate,
      newPriority: editForm.priority,
      reason: editForm.reason || '手动调整'
    }
    
    await reschedulePlan(dragData)
    ElMessage.success('调整成功，已自动重排后续计划')
    editDialogVisible.value = false
    fetchGanttData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleGenerateTestData = () => {
  ElMessageBox.confirm(
    '确定要生成甘特图测试数据吗？系统将生成40条用于可视化排程测试的计划数据。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      loading.value = true
      const res = await generateTestData()
      ElMessage.success(`成功生成 ${res.data} 条测试数据`)
      fetchGanttData()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

const handleRescheduleAll = () => {
  ElMessageBox.confirm(
    '确定要进行全局重新排程吗？系统将根据当前时间自动调整所有未完成的计划。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      loading.value = true
      await rescheduleAll()
      ElMessage.success('全局重排成功')
      fetchGanttData()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  fetchGanttData()
})
</script>

<style scoped>
.gantt-chart-container {
  padding: 20px;
}

.toolbar-card {
  margin-bottom: 15px;
}

.search-form {
  margin-bottom: 0;
}

.gantt-card {
  height: calc(100vh - 200px);
  min-height: 500px;
  overflow: hidden;
}

.gantt-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.gantt-header {
  display: flex;
  border-bottom: 2px solid #e4e7ed;
  background-color: #f5f7fa;
  position: sticky;
  top: 0;
  z-index: 10;
}

.resource-header {
  width: 250px;
  min-width: 250px;
  padding: 10px 15px;
  font-weight: bold;
  border-right: 1px solid #e4e7ed;
}

.timeline-header {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.timeline-day {
  min-width: 100px;
  text-align: center;
  padding: 5px 0;
  border-right: 1px solid #ebeef5;
  flex-shrink: 0;
}

.timeline-day.weekend {
  background-color: #fafafa;
}

.timeline-day.today {
  background-color: #ecf5ff;
}

.day-label {
  font-size: 12px;
  color: #909399;
}

.date-label {
  font-size: 14px;
  font-weight: 500;
}

.gantt-body {
  display: flex;
  flex: 1;
  overflow: auto;
}

.resource-list {
  width: 250px;
  min-width: 250px;
  border-right: 1px solid #e4e7ed;
  background-color: #fafafa;
  overflow: hidden;
}

.resource-group {
  border-bottom: 1px solid #e4e7ed;
}

.group-title {
  padding: 8px 15px;
  background-color: #f0f2f5;
  font-weight: bold;
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 5px;
  height: 36px;
}

.resource-row {
  display: flex;
  align-items: center;
  padding: 0 15px;
  border-bottom: 1px solid #ebeef5;
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 8px;
  overflow: hidden;
}

.resource-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resource-code {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.timeline-body {
  flex: 1;
  position: relative;
  overflow: auto;
}

.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  pointer-events: none;
}

.grid-column {
  position: absolute;
  top: 0;
  height: 100%;
  border-right: 1px dashed #e4e7ed;
}

.grid-column.weekend {
  background-color: rgba(0, 0, 0, 0.02);
}

.grid-column.today {
  background-color: rgba(64, 158, 255, 0.05);
  border-right: 2px solid #409eff;
}

.task-group {
  position: relative;
}

.group-placeholder {
  height: 36px;
  border-bottom: 1px solid #e4e7ed;
  background-color: rgba(240, 242, 245, 0.3);
}

.task-row {
  position: relative;
  border-bottom: 1px solid #ebeef5;
}

.task-bar {
  position: absolute;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s, transform 0.2s;
  user-select: none;
}

.task-bar:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.task-bar.task-dragging {
  opacity: 0.8;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.task-bar.task-selected {
  outline: 2px solid #409eff;
  outline-offset: 2px;
}

.task-label {
  padding: 5px 8px;
  color: white;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.task-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-qty {
  font-weight: bold;
  margin-left: 8px;
  flex-shrink: 0;
}

.task-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  background-color: rgba(255, 255, 255, 0.5);
}

.task-tooltip-trigger {
  position: absolute;
  inset: 0;
}

.drag-marker {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #f56c6c;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  transform: translateX(-50%);
  pointer-events: none;
}

.drag-marker > span {
  background-color: #f56c6c;
  color: white;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 4px;
  margin-top: 4px;
  white-space: nowrap;
}
</style>
