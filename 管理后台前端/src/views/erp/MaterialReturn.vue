<template>
  <div class="material-return-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="退补料单号">
          <el-input v-model="searchForm.returnNo" placeholder="请输入退补料单号" clearable />
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
        <el-form-item label="退补料类型">
          <el-select v-model="searchForm.returnType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="余料退回" :value="1" />
            <el-option label="不良品补料" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="新建" :value="1" />
            <el-option label="待审批" :value="2" />
            <el-option label="已审批" :value="3" />
            <el-option label="已完成" :value="4" />
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
          新增退补料单
        </el-button>
      </div>

      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="returnNo" label="退补料单号" width="180" />
        <el-table-column prop="returnTypeName" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getReturnTypeTagType(row.returnType)">
              {{ row.returnTypeName || getReturnTypeName(row.returnType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workOrderNo" label="工单号" width="150" />
        <el-table-column prop="workOrderName" label="工单名称" width="120" />
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
        <el-table-column prop="returnQuantity" label="退补料数量" width="120">
          <template #default="{ row }">
            {{ row.returnQuantity }} {{ row.unit || '' }}
          </template>
        </el-table-column>
        <el-table-column prop="returnReason" label="退补料原因" width="150">
          <template #default="{ row }">
            {{ row.returnReason || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="100">
          <template #default="{ row }">
            {{ row.operatorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">
            {{ row.createTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">详情</el-button>
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1" link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="handleSubmitApproval(row)">提交审批</el-button>
            <el-button v-if="row.status === 2" link type="success" size="small" @click="handleApproval(row, true)">审批通过</el-button>
            <el-button v-if="row.status === 2" link type="warning" size="small" @click="handleApproval(row, false)">审批驳回</el-button>
            <el-button v-if="row.status === 3" link type="primary" size="small" @click="handleComplete(row)">完成</el-button>
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
            <el-form-item label="退补料类型" prop="returnType">
              <el-select v-model="form.returnType" placeholder="请选择类型" style="width: 100%">
                <el-option label="余料退回" :value="1" />
                <el-option label="不良品补料" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单号" prop="workOrderNo">
              <el-input v-model="form.workOrderNo" placeholder="请输入工单号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工单名称">
              <el-input v-model="form.workOrderName" placeholder="请输入工单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="领料单号">
              <el-input v-model="form.pickNo" placeholder="请输入领料单号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产品名称">
              <el-input v-model="form.productName" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物料编码" prop="materialCode">
              <el-input v-model="form.materialCode" placeholder="请输入物料编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="物料名称" prop="materialName">
              <el-input v-model="form.materialName" placeholder="请输入物料名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号">
              <el-input v-model="form.specification" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退补料数量" prop="returnQuantity">
              <el-input-number v-model="form.returnQuantity" :precision="0" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="退补料原因">
              <el-input v-model="form.returnReason" placeholder="请输入退补料原因" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人">
              <el-input v-model="form.operatorName" placeholder="请输入操作人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="退补料日期">
              <el-date-picker 
                v-model="form.returnDate" 
                type="date" 
                placeholder="选择日期" 
                value-format="YYYY-MM-DD"
                style="width: 100%" 
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称">
              <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
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

    <el-dialog v-model="detailVisible" title="退补料单详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="退补料单号">{{ currentReturn.returnNo }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="getReturnTypeTagType(currentReturn.returnType)">
            {{ getReturnTypeName(currentReturn.returnType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工单号">{{ currentReturn.workOrderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工单名称">{{ currentReturn.workOrderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="领料单号">{{ currentReturn.pickNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ currentReturn.productName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物料编码">{{ currentReturn.materialCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物料名称">{{ currentReturn.materialName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ currentReturn.specification || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单位">{{ currentReturn.unit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退补料数量">{{ currentReturn.returnQuantity }} {{ currentReturn.unit || '' }}</el-descriptions-item>
        <el-descriptions-item label="退补料原因">{{ currentReturn.returnReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentReturn.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ currentReturn.warehouseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退补料日期">{{ currentReturn.returnDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReturn.status)">
            {{ getStatusName(currentReturn.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentReturn.approvalUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ currentReturn.approvalTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ currentReturn.approvalOpinion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentReturn.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentReturn.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="approvalVisible" title="退补料单审批" width="500px">
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
  getMaterialReturnList, 
  createMaterialReturn, 
  updateMaterialReturn, 
  deleteMaterialReturn,
  submitApproval,
  approvalMaterialReturn,
  completeMaterialReturn
} from '@/api/materialReturn'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const approvalVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const currentReturn = ref<any>({})

const searchForm = reactive({
  returnNo: '',
  workOrderNo: '',
  workOrderName: '',
  materialName: '',
  returnType: null as number | null,
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
  returnType: 1,
  workOrderNo: '',
  workOrderName: '',
  pickNo: '',
  productName: '',
  materialCode: '',
  materialName: '',
  specification: '',
  unit: '件',
  returnQuantity: 1,
  returnReason: '',
  operatorName: '',
  returnDate: '',
  warehouseName: '',
  remark: ''
})

const approvalForm = reactive({
  approvalOpinion: ''
})

const rules: FormRules = {
  returnType: [{ required: true, message: '请选择退补料类型', trigger: 'change' }],
  workOrderNo: [{ required: true, message: '请输入工单号', trigger: 'blur' }],
  materialCode: [{ required: true, message: '请输入物料编码', trigger: 'blur' }],
  materialName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
  returnQuantity: [{ required: true, message: '请输入退补料数量', trigger: 'blur' }]
}

const getReturnTypeTagType = (returnType: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'warning'
  }
  return types[returnType] || 'info'
}

const getReturnTypeName = (returnType: number) => {
  const names: Record<number, string> = {
    1: '余料退回',
    2: '不良品补料'
  }
  return names[returnType] || '未知'
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'warning',
    3: 'primary',
    4: 'success'
  }
  return types[status] || 'info'
}

const getStatusName = (status: number) => {
  const names: Record<number, string> = {
    1: '新建',
    2: '待审批',
    3: '已审批',
    4: '已完成'
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
    const res = await getMaterialReturnList(params)
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
  searchForm.returnNo = ''
  searchForm.workOrderNo = ''
  searchForm.workOrderName = ''
  searchForm.materialName = ''
  searchForm.returnType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增退补料单'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑退补料单'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = (row: any) => {
  currentReturn.value = row
  detailVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该退补料单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMaterialReturn(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmitApproval = (row: any) => {
  ElMessageBox.confirm('确定要提交该退补料单审批吗？', '提示', {
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
  currentReturn.value = row
  approvalForm.approvalOpinion = ''
  approvalVisible.value = true
}

const submitApproval = async (approved: boolean) => {
  try {
    await approvalMaterialReturn(currentReturn.value.id, approved, approvalForm.approvalOpinion)
    ElMessage.success(approved ? '审批通过' : '审批驳回')
    approvalVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  }
}

const handleComplete = (row: any) => {
  ElMessageBox.confirm('确定要完成该退补料单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await completeMaterialReturn(row.id)
      ElMessage.success('完成成功')
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
          await updateMaterialReturn(form)
          ElMessage.success('更新成功')
        } else {
          await createMaterialReturn(form)
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
    returnType: 1,
    workOrderNo: '',
    workOrderName: '',
    pickNo: '',
    productName: '',
    materialCode: '',
    materialName: '',
    specification: '',
    unit: '件',
    returnQuantity: 1,
    returnReason: '',
    operatorName: '',
    returnDate: '',
    warehouseName: '',
    remark: ''
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.material-return-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
