<template>
  <div class="over-pick-approval-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="审批单号">
          <el-input v-model="searchForm.approvalNo" placeholder="请输入审批单号" clearable />
        </el-form-item>
        <el-form-item label="工单号">
          <el-input v-model="searchForm.workOrderNo" placeholder="请输入工单号" clearable />
        </el-form-item>
        <el-form-item label="工单名称">
          <el-input v-model="searchForm.workOrderName" placeholder="请输入工单名称" clearable />
        </el-form-item>
        <el-form-item label="物料名称">
          <el-input v-model="searchForm.materialName" placeholder="请输入物料名称" clearable />
        </el-form-item>
        <el-form-item label="申请人">
          <el-input v-model="searchForm.applicantName" placeholder="请输入申请人" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" :value="1" />
            <el-option label="待审批" :value="2" />
            <el-option label="已通过" :value="3" />
            <el-option label="已驳回" :value="4" />
            <el-option label="已撤销" :value="5" />
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
          新增超领申请
        </el-button>
      </div>

      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="approvalNo" label="审批单号" width="180" />
        <el-table-column prop="workOrderNo" label="工单号" width="150" />
        <el-table-column prop="workOrderName" label="工单名称" width="120" />
        <el-table-column prop="pickNo" label="领料单号" width="150">
          <template #default="{ row }">
            {{ row.pickNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="materialCode" label="物料编码" width="120">
          <template #default="{ row }">
            {{ row.materialCode || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="materialName" label="物料名称" width="120">
          <template #default="{ row }">
            {{ row.materialName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="planQuantity" label="计划数量" width="100">
          <template #default="{ row }">
            {{ row.planQuantity }} {{ row.unit || '' }}
          </template>
        </el-table-column>
        <el-table-column prop="overPickQuantity" label="超领数量" width="100">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.overPickQuantity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalQuantity" label="总领料数量" width="100">
          <template #default="{ row }">
            {{ row.totalQuantity || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="overPickReason" label="超领原因" width="150">
          <template #default="{ row }">
            {{ row.overPickReason || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="100">
          <template #default="{ row }">
            {{ row.applicantName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicationTime" label="申请时间" width="170">
          <template #default="{ row }">
            {{ row.applicationTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleSubmitApproval(row)">提交审批</el-button>
            <el-button v-if="row.status === 2" link type="success" size="small" @click="handleApproval(row, true)">审批通过</el-button>
            <el-button v-if="row.status === 2" link type="warning" size="small" @click="handleApproval(row, false)">审批驳回</el-button>
            <el-button v-if="row.status === 1 || row.status === 2" link type="info" size="small" @click="handleCancel(row)">撤销</el-button>
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
            <el-form-item label="领料单号">
              <el-input v-model="form.pickNo" placeholder="请输入领料单号" />
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
            <el-form-item label="物料编码" prop="materialCode">
              <el-input v-model="form.materialCode" placeholder="请输入物料编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料名称" prop="materialName">
              <el-input v-model="form.materialName" placeholder="请输入物料名称" />
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
            <el-form-item label="超领数量" prop="overPickQuantity">
              <el-input-number v-model="form.overPickQuantity" :precision="0" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人">
              <el-input v-model="form.applicantName" placeholder="请输入申请人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="超领原因">
          <el-input v-model="form.overPickReason" type="textarea" :rows="3" placeholder="请输入超领原因" />
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

    <el-dialog v-model="detailVisible" title="超领审批单详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="审批单号">{{ currentApproval.approvalNo }}</el-descriptions-item>
        <el-descriptions-item label="工单号">{{ currentApproval.workOrderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工单名称">{{ currentApproval.workOrderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="领料单号">{{ currentApproval.pickNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentApproval.productName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物料编码">{{ currentApproval.materialCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物料名称">{{ currentApproval.materialName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ currentApproval.specification || '-' }}</el-descriptions-item>
        <el-descriptions-item label="计划数量">{{ currentApproval.planQuantity }} {{ currentApproval.unit || '' }}</el-descriptions-item>
        <el-descriptions-item label="超领数量">
          <el-tag type="danger">{{ currentApproval.overPickQuantity }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总领料数量">{{ currentApproval.totalQuantity || 0 }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentApproval.applicantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentApproval.applicationTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentApproval.status)">
            {{ getStatusName(currentApproval.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentApproval.approvalUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ currentApproval.approvalTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="超领原因" :span="2">{{ currentApproval.overPickReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ currentApproval.approvalOpinion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentApproval.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentApproval.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="approvalVisible" title="超领审批" width="500px">
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getOverPickApprovalList, 
  createOverPickApproval, 
  updateOverPickApproval, 
  deleteOverPickApproval,
  submitApproval,
  approvalOverPick,
  cancelOverPick
} from '@/api/overPickApproval'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approvalVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentApproval = ref<any>({})

const searchForm = reactive({
  approvalNo: '',
  workOrderNo: '',
  workOrderName: '',
  materialName: '',
  applicantName: '',
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
  pickNo: '',
  productName: '',
  materialCode: '',
  materialName: '',
  specification: '',
  unit: '件',
  planQuantity: 0,
  overPickQuantity: 1,
  applicantName: '',
  overPickReason: '',
  remark: ''
})

const approvalForm = reactive({
  approvalOpinion: ''
})

const rules: FormRules = {
  workOrderNo: [{ required: true, message: '请输入工单号', trigger: 'blur' }],
  materialCode: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
  materialName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
  planQuantity: [{ required: true, message: '请输入计划数量', trigger: 'blur' }],
  overPickQuantity: [{ required: true, message: '请输入超领数量', trigger: 'blur' }]
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger',
    5: 'info'
  }
  return types[status] || 'info'
}

const getStatusName = (status: number) => {
  const names: Record<number, string> = {
    1: '草稿',
    2: '待审批',
    3: '已通过',
    4: '已驳回',
    5: '已撤销'
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
    const res = await getOverPickApprovalList(params)
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
  searchForm.approvalNo = ''
  searchForm.workOrderNo = ''
  searchForm.workOrderName = ''
  searchForm.materialName = ''
  searchForm.applicantName = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增超领申请'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑超领申请'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = (row: any) => {
  currentApproval.value = row
  detailVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该超领申请吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteOverPickApproval(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmitApproval = (row: any) => {
  ElMessageBox.confirm('确定要提交该超领申请审批吗？', '提示', {
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

const handleApproval = (row: any, approved: boolean) => {
  currentApproval.value = row
  approvalForm.approvalOpinion = ''
  approvalVisible.value = true
}

const submitApproval = async (approved: boolean) => {
  try {
    await approvalOverPick({
      id: currentApproval.value.id,
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

const handleCancel = (row: any) => {
  ElMessageBox.confirm('确定要撤销该超领申请吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelOverPick(row.id)
      ElMessage.success('撤销成功')
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
          await updateOverPickApproval(form)
          ElMessage.success('更新成功')
        } else {
          await createOverPickApproval(form)
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
    workOrderNo: '',
    workOrderName: '',
    pickNo: '',
    productName: '',
    materialCode: '',
    materialName: '',
    specification: '',
    unit: '件',
    planQuantity: 0,
    overPickQuantity: 1,
    applicantName: '',
    overPickReason: '',
    remark: ''
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.over-pick-approval-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
