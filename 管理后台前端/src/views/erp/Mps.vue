<template>
  <div class="mps-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="计划编号">
          <el-input v-model="searchForm.mpsNo" placeholder="请输入计划编号" clearable />
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="searchForm.productCode" placeholder="请输入产品编码" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="生产中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleGenerateFromNetRequirement">
          <el-icon><MagicStick /></el-icon>
          从净需求生成（产能平衡）
        </el-button>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增计划
        </el-button>
        <el-button 
          type="warning" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchConfirm"
        >
          批量确认
        </el-button>
        <el-button 
          type="danger" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
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
        <el-table-column prop="mpsNo" label="计划编号" width="180" />
        <el-table-column prop="planName" label="计划名称" width="150" />
        <el-table-column prop="productCode" label="产品编码" width="120" />
        <el-table-column prop="productName" label="产品名称" width="120" />
        <el-table-column prop="netRequirement" label="净需求" width="100" />
        <el-table-column prop="plannedQuantity" label="计划数量" width="100" />
        <el-table-column prop="equipmentName" label="设备" width="100" />
        <el-table-column prop="groupName" label="班组" width="100" />
        <el-table-column prop="planStartDate" label="开始日期" width="110" />
        <el-table-column prop="planEndDate" label="结束日期" width="110" />
        <el-table-column prop="capacityUtilization" label="产能利用率" width="100">
          <template #default="{ row }">
            <span :class="getCapacityClass(row.capacityUtilization)">
              {{ row.capacityUtilization }}%
            </span>
          </template>
        </el-table-column>
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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 1" 
              link type="success" 
              size="small" 
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button 
              v-if="row.status === 1 || row.status === 2" 
              link type="warning" 
              size="small" 
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
            <el-form-item label="计划编号">
              <el-input v-model="form.mpsNo" :disabled="isEdit" placeholder="自动生成或手动输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划名称">
              <el-input v-model="form.planName" placeholder="请输入计划名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品编码">
              <el-input v-model="form.productCode" placeholder="请输入产品编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品名称">
              <el-input v-model="form.productName" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="净需求">
              <el-input-number v-model="form.netRequirement" :precision="2" :min="0" placeholder="请输入净需求" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划数量">
              <el-input-number v-model="form.plannedQuantity" :precision="2" :min="0" placeholder="请输入计划数量" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.planStartDate" type="date" placeholder="请选择开始日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.planEndDate" type="date" placeholder="请选择结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备">
              <el-input v-model="form.equipmentName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班组">
              <el-input v-model="form.groupName" placeholder="请输入班组名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优先级">
              <el-select v-model="form.priority" style="width: 100%">
                <el-option label="高" :value="1" />
                <el-option label="中" :value="2" />
                <el-option label="低" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="草稿" :value="1" />
                <el-option label="已确认" :value="2" />
                <el-option label="生产中" :value="3" />
                <el-option label="已完成" :value="4" />
                <el-option label="已取消" :value="5" />
              </el-select>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getMpsList, 
  createMps, 
  updateMps, 
  deleteMps,
  confirmMps,
  cancelMps,
  generateMpsFromNetRequirement,
  updateMpsStatusBatch,
  deleteMpsBatch
} from '@/api/mps'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const selectedIds = ref<number[]>([])

const searchForm = reactive({
  mpsNo: '',
  productCode: '',
  productName: '',
  status: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  mpsNo: '',
  planName: '',
  productId: null as number | null,
  productCode: '',
  productName: '',
  netRequirement: 0,
  plannedQuantity: 0,
  planStartDate: '',
  planEndDate: '',
  equipmentId: null as number | null,
  equipmentCode: '',
  equipmentName: '',
  groupId: null as number | null,
  groupCode: '',
  groupName: '',
  priority: 2,
  remark: '',
  status: 1
})

const rules: FormRules = {}

const getCapacityClass = (utilization: number) => {
  if (utilization >= 90) return 'text-red-600 font-bold'
  if (utilization >= 70) return 'text-orange-600'
  return 'text-green-600'
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

const getStatusName = (status: number) => {
  const names: Record<number, string> = {
    1: '草稿',
    2: '已确认',
    3: '生产中',
    4: '已完成',
    5: '已取消'
  }
  return names[status] || '未知'
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
    const res = await getMpsList(params)
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
  searchForm.mpsNo = ''
  searchForm.productCode = ''
  searchForm.productName = ''
  searchForm.status = null
  handleSearch()
}

const handleGenerateFromNetRequirement = () => {
  ElMessageBox.confirm('确定要从净需求生成主生产计划吗？系统将根据设备产能和人员排班进行产能平衡计算。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      await generateMpsFromNetRequirement()
      ElMessage.success('主生产计划生成成功')
      fetchData()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

const handleAdd = () => {
  dialogTitle.value = '新增主生产计划'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑主生产计划'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleConfirm = (row: any) => {
  ElMessageBox.confirm('确定要确认该主生产计划吗？确认后将进入生产流程。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await confirmMps(row.id)
      ElMessage.success('确认成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleCancel = (row: any) => {
  ElMessageBox.confirm('确定要取消该主生产计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelMps(row.id)
      ElMessage.success('取消成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该主生产计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMps(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchConfirm = () => {
  ElMessageBox.confirm(`确定要确认选中的 ${selectedIds.value.length} 个主生产计划吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateMpsStatusBatch(selectedIds.value, 2)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个主生产计划吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMpsBatch(selectedIds.value)
      ElMessage.success('删除成功')
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
          await updateMps(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createMps(form)
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
    mpsNo: '',
    planName: '',
    productId: null,
    productCode: '',
    productName: '',
    netRequirement: 0,
    plannedQuantity: 0,
    planStartDate: '',
    planEndDate: '',
    equipmentId: null,
    equipmentCode: '',
    equipmentName: '',
    groupId: null,
    groupCode: '',
    groupName: '',
    priority: 2,
    remark: '',
    status: 1
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.mps-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.text-red-600 {
  color: #dc2626;
}

.text-orange-600 {
  color: #ea580c;
}

.text-green-600 {
  color: #16a34a;
}

.font-bold {
  font-weight: bold;
}
</style>
