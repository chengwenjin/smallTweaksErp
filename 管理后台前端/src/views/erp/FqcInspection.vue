<template>
  <div class="fqc-inspection-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="检验单号">
          <el-input v-model="searchForm.inspectionNo" placeholder="请输入检验单号" clearable />
        </el-form-item>
        <el-form-item label="工单号">
          <el-input v-model="searchForm.workOrderNo" placeholder="请输入工单号" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="合格证号">
          <el-input v-model="searchForm.certificateNo" placeholder="请输入合格证号" clearable />
        </el-form-item>
        <el-form-item label="检验结果">
          <el-select v-model="searchForm.inspectionResult" placeholder="请选择" clearable style="width: 120px">
            <el-option label="合格" value="QUALIFIED" />
            <el-option label="不合格" value="UNQUALIFIED" />
          </el-select>
        </el-form-item>
        <el-form-item label="允许入库">
          <el-select v-model="searchForm.allowWarehousing" placeholder="请选择" clearable style="width: 120px">
            <el-option label="允许" :value="1" />
            <el-option label="禁止" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" :value="1" />
            <el-option label="待检验" :value="2" />
            <el-option label="检验完成" :value="3" />
            <el-option label="待处理" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="已取消" :value="6" />
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
          新增检验单
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="inspectionNo" label="检验单号" width="150" />
        <el-table-column prop="workOrderNo" label="工单号" width="140" />
        <el-table-column prop="productName" label="产品名称" width="120" show-overflow-tooltip />
        <el-table-column prop="specification" label="规格型号" width="100" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column prop="batchNo" label="批次号" width="100" />
        <el-table-column prop="finishedQuantity" label="完工数量" width="100" />
        <el-table-column prop="inspectionQuantity" label="检验数量" width="100" />
        <el-table-column prop="inspectionTypeName" label="检验类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getInspectionTypeTagType(row.inspectionType)">
              {{ row.inspectionTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="qualifiedRate" label="合格率(%)" width="100" />
        <el-table-column prop="inspectionResultName" label="检验结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.inspectionResult === 'QUALIFIED' ? 'success' : row.inspectionResult === 'UNQUALIFIED' ? 'danger' : 'info'">
              {{ row.inspectionResultName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="certificateNo" label="合格证号" width="120" />
        <el-table-column prop="allowWarehousingName" label="允许入库" width="80">
          <template #default="{ row }">
            <el-tag :type="row.allowWarehousing === 1 ? 'success' : 'danger'">
              {{ row.allowWarehousingName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="disposalTypeName" label="处理方式" width="80" show-overflow-tooltip />
        <el-table-column prop="inspectorName" label="检验员" width="80" />
        <el-table-column prop="inspectionDate" label="检验日期" width="160" />
        <el-table-column prop="statusName" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" size="small" v-if="row.status === 1" @click="handleSubmit(row)">提交检验</el-button>
            <el-button link type="primary" size="small" v-if="row.status === 2" @click="handleComplete(row)">完成检验</el-button>
            <el-button link type="warning" size="small" v-if="row.status === 4" @click="handleDisposal(row)">不合格品处理</el-button>
            <el-button link type="danger" size="small" v-if="row.status === 1" @click="handleDelete(row)">删除</el-button>
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
      width="900px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检验单号" prop="inspectionNo">
              <el-input v-model="form.inspectionNo" :disabled="isEdit" placeholder="自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工单号">
              <el-input v-model="form.workOrderNo" placeholder="请输入工单号" />
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
            <el-form-item label="规格型号">
              <el-input v-model="form.specification" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="单位">
              <el-input v-model="form.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="批次号">
              <el-input v-model="form.batchNo" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="检验类型">
              <el-select v-model="form.inspectionType" placeholder="请选择检验类型" style="width: 100%">
                <el-option label="全检" value="FULL" />
                <el-option label="抽检" value="SAMPLE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="完工数量">
              <el-input-number v-model="form.finishedQuantity" :min="0" :precision="4" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检验数量">
              <el-input-number v-model="form.inspectionQuantity" :min="0" :precision="4" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检验标准">
              <el-input v-model="form.inspectionStandard" placeholder="请输入检验标准" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检验员">
              <el-input v-model="form.inspectorName" placeholder="请输入检验员姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="completeDialogVisible"
      title="完成检验"
      width="800px"
    >
      <el-form :model="completeForm" :rules="completeRules" ref="completeFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="合格数量" prop="qualifiedCount">
              <el-input-number v-model="completeForm.qualifiedCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="不合格数量" prop="unqualifiedCount">
              <el-input-number v-model="completeForm.unqualifiedCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="返工数量" prop="reworkCount">
              <el-input-number v-model="completeForm.reworkCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="报废数量" prop="scrappedCount">
              <el-input-number v-model="completeForm.scrappedCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检验结果" prop="inspectionResult">
              <el-select v-model="completeForm.inspectionResult" placeholder="请选择检验结果" style="width: 100%">
                <el-option label="合格" value="QUALIFIED" />
                <el-option label="不合格" value="UNQUALIFIED" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="检验备注">
          <el-input v-model="completeForm.remark" type="textarea" :rows="2" placeholder="请输入检验备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="disposalDialogVisible"
      title="不合格品处理"
      width="700px"
    >
      <el-form :model="disposalForm" :rules="disposalRules" ref="disposalFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="检验结果" prop="inspectionResult">
              <el-select v-model="disposalForm.inspectionResult" placeholder="请选择检验结果" style="width: 100%">
                <el-option label="合格" value="QUALIFIED" />
                <el-option label="不合格" value="UNQUALIFIED" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理方式" prop="disposalType">
              <el-select v-model="disposalForm.disposalType" placeholder="请选择处理方式" style="width: 100%">
                <el-option label="返工" value="REWORK" />
                <el-option label="特采" value="SPECIAL_ACCEPT" />
                <el-option label="报废" value="SCRAP" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="处理说明" prop="disposalRemark">
          <el-input v-model="disposalForm.disposalRemark" type="textarea" :rows="3" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="disposalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDisposalSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getFqcInspectionList,
  createFqcInspection,
  updateFqcInspection,
  deleteFqcInspection,
  submitFqcInspection,
  completeFqcInspection,
  disposalFqcInspection
} from '@/api/fqcInspection'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const completeDialogVisible = ref(false)
const disposalDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const completeFormRef = ref<FormInstance>()
const disposalFormRef = ref<FormInstance>()

const searchForm = reactive({
  inspectionNo: '',
  workOrderNo: '',
  productName: '',
  certificateNo: '',
  inspectionResult: '',
  allowWarehousing: null as number | null,
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
  inspectionNo: '',
  workOrderNo: '',
  productName: '',
  specification: '',
  unit: '',
  batchNo: '',
  finishedQuantity: 0,
  inspectionQuantity: 0,
  inspectionType: 'FULL',
  inspectionStandard: '',
  inspectorName: '',
  remark: ''
})

const completeForm = reactive({
  id: null as number | null,
  qualifiedCount: 0,
  unqualifiedCount: 0,
  reworkCount: 0,
  scrappedCount: 0,
  inspectionResult: 'QUALIFIED',
  remark: ''
})

const disposalForm = reactive({
  id: null as number | null,
  inspectionResult: 'UNQUALIFIED',
  disposalType: '',
  disposalRemark: ''
})

const rules: FormRules = {
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }]
}

const completeRules: FormRules = {
  qualifiedCount: [{ required: true, message: '请输入合格数量', trigger: 'blur' }],
  unqualifiedCount: [{ required: true, message: '请输入不合格数量', trigger: 'blur' }],
  inspectionResult: [{ required: true, message: '请选择检验结果', trigger: 'change' }]
}

const disposalRules: FormRules = {
  inspectionResult: [{ required: true, message: '请选择检验结果', trigger: 'change' }],
  disposalType: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
  disposalRemark: [{ required: true, message: '请输入处理说明', trigger: 'blur' }]
}

const getInspectionTypeTagType = (type: string) => {
  switch (type) {
    case 'FULL': return 'primary'
    case 'SAMPLE': return 'warning'
    default: return ''
  }
}

const getStatusTagType = (status: number) => {
  switch (status) {
    case 1: return 'info'
    case 2: return 'warning'
    case 3: return 'primary'
    case 4: return 'danger'
    case 5: return 'success'
    case 6: return 'info'
    default: return ''
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getFqcInspectionList(params)
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
  searchForm.inspectionNo = ''
  searchForm.workOrderNo = ''
  searchForm.productName = ''
  searchForm.certificateNo = ''
  searchForm.inspectionResult = ''
  searchForm.allowWarehousing = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增检验单'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  if (row.status !== 1) {
    ElMessage.warning('只能编辑草稿状态的检验单')
    return
  }
  dialogTitle.value = '编辑检验单'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该检验单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFqcInspection(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = (row: any) => {
  ElMessageBox.confirm('确定要提交检验吗？提交后将进入待检验状态。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await submitFqcInspection(row.id)
      ElMessage.success('提交成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleComplete = (row: any) => {
  completeForm.id = row.id
  completeForm.qualifiedCount = 0
  completeForm.unqualifiedCount = 0
  completeForm.reworkCount = 0
  completeForm.scrappedCount = 0
  completeForm.inspectionResult = 'QUALIFIED'
  completeForm.remark = ''
  completeDialogVisible.value = true
}

const handleCompleteSubmit = async () => {
  if (!completeFormRef.value) return
  
  await completeFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await completeFqcInspection(completeForm.id!, {
          qualifiedCount: completeForm.qualifiedCount,
          unqualifiedCount: completeForm.unqualifiedCount,
          reworkCount: completeForm.reworkCount,
          scrappedCount: completeForm.scrappedCount,
          inspectionResult: completeForm.inspectionResult,
          remark: completeForm.remark
        })
        ElMessage.success('检验完成')
        completeDialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDisposal = (row: any) => {
  disposalForm.id = row.id
  disposalForm.inspectionResult = row.inspectionResult || 'UNQUALIFIED'
  disposalForm.disposalType = ''
  disposalForm.disposalRemark = ''
  disposalDialogVisible.value = true
}

const handleDisposalSubmit = async () => {
  if (!disposalFormRef.value) return
  
  await disposalFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await disposalFqcInspection(disposalForm.id!, {
          inspectionResult: disposalForm.inspectionResult,
          disposalType: disposalForm.disposalType,
          disposalRemark: disposalForm.disposalRemark
        })
        ElMessage.success('处理完成')
        disposalDialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleSubmitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateFqcInspection(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createFqcInspection(form)
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
    inspectionNo: '',
    workOrderNo: '',
    productName: '',
    specification: '',
    unit: '',
    batchNo: '',
    finishedQuantity: 0,
    inspectionQuantity: 0,
    inspectionType: 'FULL',
    inspectionStandard: '',
    inspectorName: '',
    remark: ''
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.fqc-inspection-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
