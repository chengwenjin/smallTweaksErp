<template>
  <div class="work-order-dashboard">
    <el-card>
      <h2>工单进度跟踪看板</h2>
      
      <el-row :gutter="20" style="margin-top: 30px;">
        <el-col :span="4">
          <div class="stat-card total">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.totalCount }}</div>
              <div class="stat-label">工单总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card draft">
            <div class="stat-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.draftCount }}</div>
              <div class="stat-label">草稿</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card pending">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.pendingApprovalCount }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card approved">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.approvedCount }}</div>
              <div class="stat-label">已审批</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card production">
            <div class="stat-icon">
              <el-icon><Operation /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.inProductionCount }}</div>
              <div class="stat-label">生产中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-card completed">
            <div class="stat-icon">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.completedCount }}</div>
              <div class="stat-label">已完工</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 30px;">
        <el-col :span="8">
          <div class="stat-card avg-rate">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.avgCompletionRate?.toFixed(2) || 0 }}%</div>
              <div class="stat-label">平均完成率</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card picking">
            <div class="stat-icon">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.pickingCount }}</div>
              <div class="stat-label">领料中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-card reporting">
            <div class="stat-icon">
              <el-icon><DocumentAdd /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboard.reportingCount }}</div>
              <div class="stat-label">报工中</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>工单状态分布</span>
            </div>
          </template>
          <el-table :data="dashboard.statusStats || []" border stripe>
            <el-table-column prop="statusName" label="状态" width="120" />
            <el-table-column prop="count" label="数量" width="100">
              <template #default="{ row }">
                <el-tag type="primary">{{ row.count }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="percentage" label="占比" width="200">
              <template #default="{ row }">
                <el-progress 
                  :percentage="row.percentage?.toFixed(2) || 0" 
                  :stroke-width="15" 
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>工单优先级分布</span>
            </div>
          </template>
          <el-table :data="dashboard.priorityStats || []" border stripe>
            <el-table-column prop="priorityName" label="优先级" width="120">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)">
                  {{ row.priorityName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量" width="100">
              <template #default="{ row }">
                <el-tag type="primary">{{ row.count }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="percentage" label="占比" width="200">
              <template #default="{ row }">
                <el-progress 
                  :percentage="row.percentage?.toFixed(2) || 0" 
                  :stroke-width="15" 
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>紧急工单（按交期排序）</span>
              <el-button type="primary" link @click="refreshDashboard">刷新</el-button>
            </div>
          </template>
          <el-table :data="dashboard.urgentWorkOrders || []" border stripe v-loading="loading">
            <el-table-column prop="workOrderNo" label="工单编号" width="180" />
            <el-table-column prop="workOrderName" label="工单名称" width="150" />
            <el-table-column prop="productName" label="产品名称" width="120" />
            <el-table-column prop="statusName" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.statusName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="completionRate" label="完成率" width="150">
              <template #default="{ row }">
                <el-progress 
                  :percentage="row.completionRate || 0" 
                  :stroke-width="10" 
                />
              </template>
            </el-table-column>
            <el-table-column prop="deliveryDate" label="交期" width="120" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近工单</span>
            </div>
          </template>
          <el-table :data="dashboard.recentWorkOrders || []" border stripe v-loading="loading">
            <el-table-column prop="workOrderNo" label="工单编号" width="180" />
            <el-table-column prop="workOrderName" label="工单名称" width="150" />
            <el-table-column prop="productName" label="产品名称" width="120" />
            <el-table-column prop="statusName" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.statusName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="completionRate" label="完成率" width="150">
              <template #default="{ row }">
                <el-progress 
                  :percentage="row.completionRate || 0" 
                  :stroke-width="10" 
                />
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getWorkOrderDashboard } from '@/api/workOrder'

const loading = ref(false)

const dashboard = reactive<any>({
  totalCount: 0,
  draftCount: 0,
  pendingApprovalCount: 0,
  approvedCount: 0,
  issuedCount: 0,
  pickingCount: 0,
  inProductionCount: 0,
  reportingCount: 0,
  pendingStorageCount: 0,
  completedCount: 0,
  cancelledCount: 0,
  avgCompletionRate: 0,
  statusStats: [],
  priorityStats: [],
  urgentWorkOrders: [],
  recentWorkOrders: []
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'primary',
    4: 'success',
    5: 'warning',
    6: 'danger',
    7: 'warning',
    8: 'info',
    9: 'success',
    10: 'info'
  }
  return types[status] || 'info'
}

const getPriorityType = (priority: number) => {
  const types: Record<number, string> = {
    1: 'danger',
    2: 'warning',
    3: 'info'
  }
  return types[priority] || 'info'
}

const formatTime = (time: string) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getWorkOrderDashboard()
    Object.assign(dashboard, res.data)
  } catch (error) {
    console.error(error)
    ElMessage.error('获取看板数据失败')
  } finally {
    loading.value = false
  }
}

const refreshDashboard = () => {
  fetchData()
  ElMessage.success('刷新成功')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.work-order-dashboard {
  padding: 20px;
}

h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  color: #fff;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.draft {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card.pending {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.approved {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-card.production {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-card.completed {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

.stat-card.avg-rate {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #333;
}

.stat-card.picking {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  color: #333;
}

.stat-card.reporting {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: #333;
}

.stat-icon {
  font-size: 40px;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 5px;
}
</style>
