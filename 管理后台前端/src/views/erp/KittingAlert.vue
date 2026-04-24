<template>
  <div class="kitting-alert-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="预警编号">
          <el-input v-model="searchForm.alertNo" placeholder="请输入预警编号" clearable />
        </el-form-item>
        <el-form-item label="物料编码">
          <el-input v-model="searchForm.materialCode" placeholder="请输入物料编码" clearable />
        </el-form-item>
        <el-form-item label="物料名称">
          <el-input v-model="searchForm.materialName" placeholder="请输入物料名称" clearable />
        </el-form-item>
        <el-form-item label="预警等级">
          <el-select v-model="searchForm.alertLevel" placeholder="请选择" clearable style="width: 100px">
            <el-option label="紧急" :value="1" />
            <el-option label="高" :value="2" />
            <el-option label="中" :value="3" />
            <el-option label="低" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="待处理" :value="1" />
            <el-option label="已跟催" :value="2" />
            <el-option label="已解决" :value="3" />
            <el-option label="已忽略" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleCalculate">
          <el-icon><MagicStick /></el-icon>
          计算齐套预警
        </el-button>
        <el-button 
          type="success" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchSyncToScrm"
        >
          批量同步SCM
        </el-button>
        <el-button 
          type="warning" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchStatus(2)"
        >
          批量标记已跟催
        </el-button>
        <el-button 
          type="info" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchStatus(3)"
        >
          批量标记已解决
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
        <el-table-column prop="alertNo" label="预警编号" width="180" />
        <el-table-column prop="mpsNo" label="MPS编号" width="150" />
        <el-table-column prop="productCode" label="产品编码" width="100" />
        <el-table-column prop="productName" label="产品名称" width="120" />
        <el-table-column prop="materialCode" label="物料编码" width="120" />
        <el-table-column prop="materialName" label="物料名称" width="120" />
        <el-table-column prop="requiredQuantity" label="需求数量" width="90" />
        <el-table-column prop="stockQuantity" label="库存数量" width="90" />
        <el-table-column prop="allocatedQuantity" label="已分配" width="80" />
        <el-table-column prop="shortageQuantity" label="缺口数量" width="90">
          <template #default="{ row }">
            <span class="text-red-600 font-bold">{{ row.shortageQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="kittingRate" label="齐套率" width="100">
          <template #default="{ row }">
            <el-progress 
              :percentage="row.kittingRate" 
              :color="getKittingRateColor(row.kittingRate)"
              :stroke-width="18"
            />
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="预警等级" width="90">
          <template #default="{ row }">
            <el-tag :type="getAlertLevelType(row.alertLevel)" size="small">
              {{ getAlertLevelName(row.alertLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="requiredDate" label="需求日期" width="110" />
        <el-table-column prop="expectedArrivalDate" label="预计到货" width="110" />
        <el-table-column prop="syncedToScrm" label="SCM同步" width="80">
          <template #default="{ row }">
            <el-tag :type="row.syncedToScrm === 1 ? 'success' : 'info'" size="small">
              {{ row.syncedToScrm === 1 ? '已同步' : '未同步' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.syncedToScrm !== 1" 
              link type="success" 
              size="small" 
              @click="handleSyncToScrm(row)"
            >
              同步SCM
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              link type="warning" 
              size="small" 
              @click="handleStatus(row, 2)"
            >
              标记已跟催
            </el-button>
            <el-button 
              v-if="row.status === 2" 
              link type="info" 
              size="small" 
              @click="handleStatus(row, 3)"
            >
              标记已解决
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="预警编号">
          <el-input v-model="form.alertNo" disabled />
        </el-form-item>
        <el-form-item label="物料编码">
          <el-input v-model="form.materialCode" disabled />
        </el-form-item>
        <el-form-item label="物料名称">
          <el-input v-model="form.materialName" disabled />
        </el-form-item>
        <el-form-item label="需求数量">
          <el-input v-model="form.requiredQuantity" disabled />
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input v-model="form.stockQuantity" disabled />
        </el-form-item>
        <el-form-item label="缺口数量">
          <el-input v-model="form.shortageQuantity" disabled />
        </el-form-item>
        <el-form-item label="预计到货日期">
          <el-date-picker v-model="form.expectedArrivalDate" type="date" placeholder="请选择预计到货日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="form.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="采购订单号">
          <el-input v-model="form.purchaseOrderNo" placeholder="请输入采购订单号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="待处理" :value="1" />
            <el-option label="已跟催" :value="2" />
            <el-option label="已解决" :value="3" />
            <el-option label="已忽略" :value="4" />
          </el-select>
        </el-form-item>
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
  getKittingAlertList, 
  updateKittingAlert, 
  deleteKittingAlert,
  updateKittingAlertStatus,
  calculateKittingAlerts,
  syncToScrm,
  syncToScrmBatch,
  updateKittingAlertStatusBatch,
  deleteKittingAlertBatch
} from '@/api/kittingAlert'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const selectedIds = ref<number[]>([])

const searchForm = reactive({
  alertNo: '',
  materialCode: '',
  materialName: '',
  alertLevel: null as number | null,
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
  alertNo: '',
  materialCode: '',
  materialName: '',
  requiredQuantity: 0,
  stockQuantity: 0,
  shortageQuantity: 0,
  expectedArrivalDate: '',
  supplierName: '',
  purchaseOrderNo: '',
  remark: '',
  status: 1
})

const rules: FormRules = {}

const getKittingRateColor = (rate: number) => {
  if (rate >= 80) return '#67c23a'
  if (rate >= 50) return '#e6a23c'
  return '#f56c6c'
}

const getAlertLevelType = (level: number) => {
  const types: Record<number, string> = {
    1: 'danger',
    2: 'warning',
    3: 'primary',
    4: 'info'
  }
  return types[level] || 'info'
}

const getAlertLevelName = (level: number) => {
  const names: Record<number, string> = {
    1: '紧急',
    2: '高',
    3: '中',
    4: '低'
  }
  return names[level] || '未知'
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'info'
  }
  return types[status] || 'info'
}

const getStatusName = (status: number) => {
  const names: Record<number, string> = {
    1: '待处理',
    2: '已跟催',
    3: '已解决',
    4: '已忽略'
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
    const res = await getKittingAlertList(params)
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
  searchForm.alertNo = ''
  searchForm.materialCode = ''
  searchForm.materialName = ''
  searchForm.alertLevel = null
  searchForm.status = null
  handleSearch()
}

const handleCalculate = () => {
  ElMessageBox.confirm('确定要重新计算齐套预警吗？系统将根据BOM结构、库存情况和MPS计划自动计算物料齐套率并生成预警。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      loading.value = true
      await calculateKittingAlerts()
      ElMessage.success('齐套预警计算完成')
      fetchData()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑齐套预警'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSyncToScrm = (row: any) => {
  ElMessageBox.confirm('确定要将该预警同步到SCM模块进行跟催吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await syncToScrm(row.id)
      ElMessage.success('同步成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleStatus = (row: any, status: number) => {
  ElMessageBox.confirm(`确定要将该预警标记为"${getStatusName(status)}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateKittingAlertStatus(row.id, status)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该齐套预警吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteKittingAlert(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchSyncToScrm = () => {
  ElMessageBox.confirm(`确定要将选中的 ${selectedIds.value.length} 个预警同步到SCM模块吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await syncToScrmBatch(selectedIds.value)
      ElMessage.success('同步成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchStatus = (status: number) => {
  ElMessageBox.confirm(`确定要将选中的 ${selectedIds.value.length} 个预警标记为"${getStatusName(status)}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateKittingAlertStatusBatch(selectedIds.value, status)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个齐套预警吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteKittingAlertBatch(selectedIds.value)
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
        await updateKittingAlert(form.id!, form)
        ElMessage.success('更新成功')
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
    alertNo: '',
    materialCode: '',
    materialName: '',
    requiredQuantity: 0,
    stockQuantity: 0,
    shortageQuantity: 0,
    expectedArrivalDate: '',
    supplierName: '',
    purchaseOrderNo: '',
    remark: '',
    status: 1
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.kitting-alert-management {
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

.font-bold {
  font-weight: bold;
}
</style>
