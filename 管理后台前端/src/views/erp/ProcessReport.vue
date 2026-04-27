<template>
  <div class="process-report-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="报工单号">
          <el-input v-model="searchForm.reportNo" placeholder="请输入报工单号" clearable />
        </el-form-item>
        <el-form-item label="工单号">
          <el-input v-model="searchForm.workOrderNo" placeholder="请输入工单号" clearable />
        </el-form-item>
        <el-form-item label="工单名称">
          <el-input v-model="searchForm.workOrderName" placeholder="请输入工单名称" clearable />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input v-model="searchForm.operatorName" placeholder="请输入操作人员" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="待报工" :value="1" />
            <el-option label="已报工" :value="2" />
            <el-option label="已审核" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleScanReport">
          <el-icon><Plus /></el-icon>
          扫码报工
        </el-button>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增报工
        </el-button>
      </div>

      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="reportNo" label="报工单号" width="180" />
        <el-table-column prop="workOrderNo" label="工单号" width="150" />
        <el-table-column prop="workOrderName" label="工单名称" width="120" />
        <el-table-column prop="processName" label="工序名称" width="100">
          <template #default="{ row }">
            {{ row.processName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="equipmentName" label="设备" width="100">
          <template #default="{ row }">
            {{ row.equipmentName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人员" width="100" />
        <el-table-column prop="reportQuantity" label="报工数量" width="100">
          <template #default="{ row }">
            {{ row.reportQuantity }} {{ row.unit || '' }}
          </template>
        </el-table-column>
        <el-table-column prop="qualifiedQuantity" label="合格数量" width="100">
          <template #default="{ row }">
            {{ row.qualifiedQuantity || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="scrappedQuantity" label="报废数量" width="100">
          <template #default="{ row }">
            {{ row.scrappedQuantity || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="workHours" label="工时(小时)" width="100">
          <template #default="{ row }">
            {{ row.workHours || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="barcode" label="条码" width="120">
          <template #default="{ row }">
            {{ row.barcode || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报工时间" width="170">
          <template #default="{ row }">
            {{ row.createTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 1 || row.status === 2" link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单号" prop="workOrderNo">
              <el-input v-model="form.workOrderNo" placeholder="请输入工单号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单名称">
              <el-input v-model="form.workOrderName" placeholder="请输入工单名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工序名称">
              <el-input v-model="form.processName" placeholder="请输入工序名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称">
              <el-input v-model="form.equipmentName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="操作人员" prop="operatorName">
              <el-input v-model="form.operatorName" placeholder="请输入操作人员" />
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
            <el-form-item label="报工数量" prop="reportQuantity">
              <el-input-number v-model="form.reportQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合格数量">
              <el-input-number v-model="form.qualifiedQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报废数量">
              <el-input-number v-model="form.scrappedQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="返工数量">
              <el-input-number v-model="form.reworkQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工时(小时)">
              <el-input-number v-model="form.workHours" :precision="2" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="条码">
              <el-input v-model="form.barcode" placeholder="请输入条码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-date-picker 
                v-model="form.startTime" 
                type="datetime" 
                placeholder="选择时间" 
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-date-picker 
                v-model="form.endTime" 
                type="datetime" 
                placeholder="选择时间" 
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%" 
              />
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

    <el-dialog v-model="scanVisible" title="扫码报工" width="600px">
      <el-form :model="scanForm" label-width="100px">
        <el-form-item label="条码">
          <el-input v-model="scanForm.barcode" placeholder="请输入条码或扫描条码" @keyup.enter="handleScanSubmit" />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input v-model="scanForm.operatorName" placeholder="请输入操作人员" />
        </el-form-item>
        <el-form-item label="报工数量">
          <el-input-number v-model="scanForm.reportQuantity" :precision="0" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报废数量">
          <el-input-number v-model="scanForm.scrappedQuantity" :precision="0" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="scanForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scanVisible = false">取消</el-button>
        <el-button type="primary" @click="handleScanSubmit">确认报工</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="报工详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报工单号">{{ currentReport.reportNo }}</el-descriptions-item>
        <el-descriptions-item label="工单号">{{ currentReport.workOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="工单名称">{{ currentReport.workOrderName }}</el-descriptions-item>
        <el-descriptions-item label="工序名称">{{ currentReport.processName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ currentReport.equipmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人员">{{ currentReport.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="报工数量">{{ currentReport.reportQuantity }} {{ currentReport.unit || '' }}</el-descriptions-item>
        <el-descriptions-item label="合格数量">{{ currentReport.qualifiedQuantity || 0 }}</el-descriptions-item>
        <el-descriptions-item label="报废数量">{{ currentReport.scrappedQuantity || 0 }}</el-descriptions-item>
        <el-descriptions-item label="返工数量">{{ currentReport.reworkQuantity || 0 }}</el-descriptions-item>
        <el-descriptions-item label="工时">{{ currentReport.workHours || '-' }} 小时</el-descriptions-item>
        <el-descriptions-item label="条码">{{ currentReport.barcode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentReport.startTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentReport.endTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReport.status)">
            {{ getStatusName(currentReport.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报工时间">{{ currentReport.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentReport.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getProcessReportList, 
  createProcessReport, 
  updateProcessReport, 
  deleteProcessReport,
  scanReport
} from '@/api/processReport'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const scanVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentReport = ref<any>({})

const searchForm = reactive({
  reportNo: '',
  workOrderNo: '',
  workOrderName: '',
  operatorName: '',
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
  workOrderNo: '',
  workOrderName: '',
  processName: '',
  equipmentName: '',
  operatorName: '',
  unit: '件',
  reportQuantity: 1,
  qualifiedQuantity: 1,
  scrappedQuantity: 0,
  reworkQuantity: 0,
  workHours: null as number | null,
  barcode: '',
  startTime: '',
  endTime: '',
  remark: ''
})

const scanForm = reactive({
  barcode: '',
  operatorName: '',
  reportQuantity: 1,
  scrappedQuantity: 0,
  remark: ''
})

const rules: FormRules = {
  workOrderNo: [{ required: true, message: '请输入工单号', trigger: 'blur' }],
  operatorName: [{ required: true, message: '请输入操作人员', trigger: 'blur' }],
  reportQuantity: [{ required: true, message: '请输入报工数量', trigger: 'blur' }]
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'warning',
    2: 'primary',
    3: 'success'
  }
  return types[status] || 'info'
}

const getStatusName = (status: number) => {
  const names: Record<number, string> = {
    1: '待报工',
    2: '已报工',
    3: '已审核'
  }
  return names[status] || '未知'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getProcessReportList(params)
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
  searchForm.reportNo = ''
  searchForm.workOrderNo = ''
  searchForm.workOrderName = ''
  searchForm.operatorName = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增报工'
  isEdit.value = false
  dialogVisible.value = true
}

const handleScanReport = () => {
  scanForm.barcode = ''
  scanForm.operatorName = ''
  scanForm.reportQuantity = 1
  scanForm.scrappedQuantity = 0
  scanForm.remark = ''
  scanVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑报工'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = (row: any) => {
  currentReport.value = row
  detailVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该报工记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteProcessReport(row.id)
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
          await updateProcessReport(form)
          ElMessage.success('更新成功')
        } else {
          await createProcessReport(form)
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

const handleScanSubmit = async () => {
  if (!scanForm.barcode) {
    ElMessage.warning('请输入条码')
    return
  }
  
  try {
    await scanReport(scanForm)
    ElMessage.success('扫码报工成功')
    scanVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    workOrderNo: '',
    workOrderName: '',
    processName: '',
    equipmentName: '',
    operatorName: '',
    unit: '件',
    reportQuantity: 1,
    qualifiedQuantity: 1,
    scrappedQuantity: 0,
    reworkQuantity: 0,
    workHours: null,
    barcode: '',
    startTime: '',
    endTime: '',
    remark: ''
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.process-report-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
