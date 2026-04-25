<template>
  <div class="work-order-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="工单编号">
          <el-input v-model="searchForm.workOrderNo" placeholder="请输入工单编号" clearable />
        </el-form-item>
        <el-form-item label="工单名称">
          <el-input v-model="searchForm.workOrderName" placeholder="请输入工单名称" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" :value="1" />
            <el-option label="待审批" :value="2" />
            <el-option label="已审批" :value="3" />
            <el-option label="已下发" :value="4" />
            <el-option label="领料中" :value="5" />
            <el-option label="生产中" :value="6" />
            <el-option label="报工中" :value="7" />
            <el-option label="待入库" :value="8" />
            <el-option label="已完工" :value="9" />
            <el-option label="已取消" :value="10" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="searchForm.priority" placeholder="请选择" clearable style="width: 100px">
            <el-option label="高" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="低" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增工单
        </el-button>
        <el-button 
          type="warning" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchSubmitApproval"
        >
          批量提交审批
        </el-button>
      </div>

      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="workOrderNo" label="工单编号" width="180" />
        <el-table-column prop="workOrderName" label="工单名称" width="150" />
        <el-table-column prop="workOrderType" label="工单类型" width="100" />
        <el-table-column prop="productName" label="产品名称" width="120" />
        <el-table-column prop="planQuantity" label="计划数量" width="100">
          <template #default="{ row }">
            {{ row.planQuantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="completedQuantity" label="已完成" width="100">
          <template #default="{ row }">
            {{ row.completedQuantity || 0 }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="completionRate" label="完成率" width="100">
          <template #default="{ row }">
            <el-progress :percentage="row.completionRate || 0" :stroke-width="10" :show-text="true" />
          </template>
        </el-table-column>
        <el-table-column prop="equipmentName" label="设备" width="100" />
        <el-table-column prop="groupName" label="班组" width="100" />
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">
              {{ getPriorityName(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="planStartDate" label="计划开始" width="110" />
        <el-table-column prop="deliveryDate" label="交期" width="110" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleSubmitApproval(row)">提交审批</el-button>
            <el-button v-if="row.status === 2" link type="success" size="small" @click="handleApproval(row, true)">审批通过</el-button>
            <el-button v-if="row.status === 2" link type="warning" size="small" @click="handleApproval(row, false)">审批驳回</el-button>
            <el-button v-if="row.status === 3" link type="success" size="small" @click="handleIssue(row)">下发</el-button>
            <el-button v-if="row.status === 4 || row.status === 5" link type="primary" size="small" @click="handlePick(row)">领料</el-button>
            <el-button v-if="row.status === 5" link type="success" size="small" @click="handleStartProduction(row)">开始生产</el-button>
            <el-button v-if="row.status === 6 || row.status === 7" link type="primary" size="small" @click="handleReport(row)">报工</el-button>
            <el-button v-if="row.status === 7" link type="warning" size="small" @click="handlePendingStorage(row)">待入库</el-button>
            <el-button v-if="row.status === 8" link type="success" size="small" @click="handleComplete(row)">完工入库</el-button>
            <el-button v-if="row.status !== 9 && row.status !== 10" link type="danger" size="small" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单名称" prop="workOrderName">
              <el-input v-model="form.workOrderName" placeholder="请输入工单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单类型">
              <el-select v-model="form.workOrderType" placeholder="请选择工单类型" style="width: 100%">
                <el-option label="正常工单" value="正常工单" />
                <el-option label="返工工单" value="返工工单" />
                <el-option label="试制工单" value="试制工单" />
                <el-option label="维修工单" value="维修工单" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品名称" prop="productName">
              <el-input v-model="form.productName" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品编码">
              <el-input v-model="form.productCode" placeholder="请输入产品编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input v-model="form.specification" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划数量" prop="planQuantity">
              <el-input-number v-model="form.planQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级">
              <el-select v-model="form.priority" style="width: 100%">
                <el-option label="高" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="低" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划开始日期">
              <el-date-picker 
                v-model="form.planStartDate" 
                type="date" 
                placeholder="选择日期" 
                value-format="YYYY-MM-DD"
                style="width: 100%" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划结束日期">
              <el-date-picker 
                v-model="form.planEndDate" 
                type="date" 
                placeholder="选择日期" 
                value-format="YYYY-MM-DD"
                style="width: 100%" 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="交期">
              <el-date-picker 
                v-model="form.deliveryDate" 
                type="date" 
                placeholder="选择交期" 
                value-format="YYYY-MM-DD"
                style="width: 100%" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备">
              <el-input v-model="form.equipmentName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班组">
              <el-input v-model="form.groupName" placeholder="请输入班组名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单来源">
              <el-input v-model="form.orderSource" placeholder="请输入订单来源" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="工单详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">{{ currentWorkOrder.workOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="工单名称">{{ currentWorkOrder.workOrderName }}</el-descriptions-item>
        <el-descriptions-item label="工单类型">{{ currentWorkOrder.workOrderType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentWorkOrder.status)">
            {{ getStatusName(currentWorkOrder.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentWorkOrder.productName }}</el-descriptions-item>
        <el-descriptions-item label="产品编码">{{ currentWorkOrder.productCode }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ currentWorkOrder.planQuantity }} {{ currentWorkOrder.unit }}</el-descriptions-item>
        <el-descriptions-item label="已完成">{{ currentWorkOrder.completedQuantity || 0 }} {{ currentWorkOrder.unit }}</el-descriptions-item>
        <el-descriptions-item label="完成率">
          <el-progress :percentage="currentWorkOrder.completionRate || 0" :stroke-width="10" />
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(currentWorkOrder.priority)">
            {{ getPriorityName(currentWorkOrder.priority) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="设备">{{ currentWorkOrder.equipmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="班组">{{ currentWorkOrder.groupName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计划开始">{{ currentWorkOrder.planStartDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计划结束">{{ currentWorkOrder.planEndDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交期">{{ currentWorkOrder.deliveryDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentWorkOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>流程日志</el-divider>
      <el-timeline v-if="workOrderLogs.length > 0">
        <el-timeline-item
          v-for="log in workOrderLogs"
          :key="log.id"
          :timestamp="log.operationTime"
          placement="top"
        >
          <el-card>
            <h4>{{ log.operationName }}</h4>
            <p>
              <span v-if="log.fromStatusName">从「{{ log.fromStatusName }}」</span>
              到「{{ log.toStatusName }}」
            </p>
            <p v-if="log.operationQuantity">操作数量：{{ log.operationQuantity }}</p>
            <p v-if="log.operationRemark">备注：{{ log.operationRemark }}</p>
            <p>操作人：{{ log.operatorName || log.operator }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无流程日志" />
    </el-dialog>

    <el-dialog v-model="approvalVisible" title="工单审批" width="500px">
      <el-form :model="approvalForm" label-width="100px">
        <el-form-item label="审批意见">
          <el-input 
            v-model="approvalForm.approvalOpinion" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入审批意见" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalVisible = false">取消</el-button>
        <el-button type="danger" @click="submitApproval(false)">驳回</el-button>
        <el-button type="primary" @click="submitApproval(true)">通过</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reportVisible" title="工单报工" width="500px">
      <el-form :model="reportForm" label-width="100px">
        <el-form-item label="报工数量">
          <el-input-number v-model="reportForm.reportQuantity" :precision="0" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报废数量">
          <el-input-number v-model="reportForm.scrappedQuantity" :precision="0" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="reportForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReport">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="completeVisible" title="完工入库" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="入库数量">
          <el-input-number v-model="completeForm.completeQuantity" :precision="0" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际结束日期">
          <el-date-picker 
            v-model="completeForm.actualEndDate" 
            type="date" 
            placeholder="选择日期" 
            value-format="YYYY-MM-DD"
            style="width: 100%" 
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="completeForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getWorkOrderList, 
  createWorkOrder, 
  updateWorkOrder, 
  deleteWorkOrder,
  submitApproval,
  submitApprovalBatch,
  approvalWorkOrder,
  issueWorkOrder,
  pickWorkOrder,
  startProduction,
  reportWorkOrder,
  pendingStorage,
  completeWorkOrder,
  cancelWorkOrder,
  getWorkOrderLogs
} from '@/api/workOrder'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const selectedIds = ref<number[]>([])
const detailVisible = ref(false)
const approvalVisible = ref(false)
const reportVisible = ref(false)
const completeVisible = ref(false)
const currentWorkOrder = ref<any>({})
const workOrderLogs = ref<any[]>([])

const searchForm = reactive({
  workOrderNo: '',
  workOrderName: '',
  productName: '',
  status: null as number | null,
  priority: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  workOrderName: '',
  workOrderType: '正常工单',
  productCode: '',
  productName: '',
  specification: '',
  unit: '台',
  planQuantity: 1,
  planStartDate: '',
  planEndDate: '',
  deliveryDate: '',
  equipmentName: '',
  groupName: '',
  priority: 2,
  orderSource: '',
  remark: ''
})

const approvalForm = reactive({
  approvalOpinion: ''
})

const reportForm = reactive({
  workOrderId: null as number | null,
  reportQuantity: 0,
  scrappedQuantity: 0,
  remark: ''
})

const completeForm = reactive({
  workOrderId: null as number | null,
  completeQuantity: 0,
  actualEndDate: '',
  remark: ''
})

const rules: FormRules = {
  workOrderName: [{ required: true, message: '请输入工单名称', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  planQuantity: [{ required: true, message: '请输入计划数量', trigger: 'blur' }]
}

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

const getStatusName = (status: number) => {
  const names: Record<number, string> = {
    1: '草稿',
    2: '待审批',
    3: '已审批',
    4: '已下发',
    5: '领料中',
    6: '生产中',
    7: '报工中',
    8: '待入库',
    9: '已完工',
    10: '已取消'
  }
  return names[status] || '未知'
}

const getPriorityType = (priority: number) => {
  const types: Record<number, string> = {
    1: 'danger',
    2: 'warning',
    3: 'info'
  }
  return types[priority] || 'info'
}

const getPriorityName = (priority: number) => {
  const names: Record<number, string> = {
    1: '高',
    2: '中',
    3: '低'
  }
  return names[priority] || '未知'
}

const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getWorkOrderList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.workOrderNo = ''
  searchForm.workOrderName = ''
  searchForm.productName = ''
  searchForm.status = null
  searchForm.priority = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增工单'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑工单'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  currentWorkOrder.value = row
  detailVisible.value = true
  
  try {
    const res = await getWorkOrderLogs(row.id)
    workOrderLogs.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该工单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteWorkOrder(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmitApproval = (row: any) => {
  ElMessageBox.confirm('确定要提交该工单审批吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await submitApproval(row.id)
      ElMessage.success('提交审批成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchSubmitApproval = () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择工单')
    return
  }
  
  ElMessageBox.confirm(`确定要提交选中的 ${selectedIds.value.length} 个工单审批吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await submitApprovalBatch(selectedIds.value)
      ElMessage.success('批量提交审批成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleApproval = (row: any, approved: boolean) => {
  currentWorkOrder.value = row
  approvalForm.approvalOpinion = ''
  approvalVisible.value = true
}

const submitApproval = async (approved: boolean) => {
  try {
    await approvalWorkOrder({
      id: currentWorkOrder.value.id,
      approved: approved,
      approvalOpinion: approvalForm.approvalOpinion
    })
    ElMessage.success(approved ? '审批通过' : '审批驳回')
    approvalVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleIssue = (row: any) => {
  ElMessageBox.confirm('确定要下发该工单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await issueWorkOrder({
        id: row.id
      })
      ElMessage.success('下发成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handlePick = (row: any) => {
  ElMessageBox.confirm('确定要执行领料操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await pickWorkOrder({
        workOrderId: row.id
      })
      ElMessage.success('领料成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleStartProduction = (row: any) => {
  ElMessageBox.confirm('确定要开始生产吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await startProduction(row.id)
      ElMessage.success('开始生产')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleReport = (row: any) => {
  reportForm.workOrderId = row.id
  reportForm.reportQuantity = 0
  reportForm.scrappedQuantity = 0
  reportForm.remark = ''
  reportVisible.value = true
}

const submitReport = async () => {
  try {
    await reportWorkOrder(reportForm)
    ElMessage.success('报工成功')
    reportVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handlePendingStorage = (row: any) => {
  ElMessageBox.confirm('确定要将工单转为待入库状态吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await pendingStorage(row.id)
      ElMessage.success('转待入库成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleComplete = (row: any) => {
  completeForm.workOrderId = row.id
  completeForm.completeQuantity = row.planQuantity || 0
  completeForm.actualEndDate = ''
  completeForm.remark = ''
  completeVisible.value = true
}

const submitComplete = async () => {
  try {
    await completeWorkOrder(completeForm)
    ElMessage.success('完工入库成功')
    completeVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleCancel = (row: any) => {
  ElMessageBox.confirm('确定要取消该工单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelWorkOrder(row.id)
      ElMessage.success('取消成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateWorkOrder(form)
          ElMessage.success('更新成功')
        } else {
          await createWorkOrder(form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    workOrderName: '',
    workOrderType: '正常工单',
    productCode: '',
    productName: '',
    specification: '',
    unit: '台',
    planQuantity: 1,
    planStartDate: '',
    planEndDate: '',
    deliveryDate: '',
    equipmentName: '',
    groupName: '',
    priority: 2,
    orderSource: '',
    remark: ''
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.work-order-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
