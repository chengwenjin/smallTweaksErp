<template>
  <div class="forecast-order-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="预测单编号">
          <el-input v-model="searchForm.forecastNo" placeholder="请输入预测单编号" clearable />
        </el-form-item>
        <el-form-item label="预测单名称">
          <el-input v-model="searchForm.forecastName" placeholder="请输入预测单名称" clearable />
        </el-form-item>
        <el-form-item label="预测类型">
          <el-select v-model="searchForm.forecastType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="月度预测" :value="1" />
            <el-option label="季度预测" :value="2" />
            <el-option label="年度预测" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="草稿" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
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
          新增预测单
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="forecastNo" label="预测单编号" width="160" />
        <el-table-column prop="forecastName" label="预测单名称" width="180" />
        <el-table-column prop="forecastType" label="预测类型" width="100">
          <template #default="{ row }">
            <el-tag type="primary">
              {{ getForecastTypeName(row.forecastType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="totalQuantity" label="预测数量" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预测单编号">
              <el-input v-model="form.forecastNo" :disabled="isEdit" placeholder="自动生成或手动输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预测单名称" prop="forecastName">
              <el-input v-model="form.forecastName" placeholder="请输入预测单名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预测类型">
              <el-select v-model="form.forecastType" style="width: 100%">
                <el-option label="月度预测" :value="1" />
                <el-option label="季度预测" :value="2" />
                <el-option label="年度预测" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预测数量">
              <el-input-number v-model="form.totalQuantity" :precision="2" :min="0" placeholder="请输入预测数量" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.startDate" type="date" placeholder="请选择开始日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.endDate" type="date" placeholder="请选择结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="草稿" :value="1" />
                <el-option label="已确认" :value="2" />
                <el-option label="已完成" :value="3" />
                <el-option label="已取消" :value="4" />
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
import { getForecastOrderList, createForecastOrder, updateForecastOrder, deleteForecastOrder } from '@/api/forecastOrder'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const searchForm = reactive({
  forecastNo: '',
  forecastName: '',
  forecastType: null as number | null,
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
  forecastNo: '',
  forecastName: '',
  forecastType: 1,
  startDate: '',
  endDate: '',
  totalQuantity: 0,
  remark: '',
  status: 1
})

const rules: FormRules = {
  forecastName: [{ required: true, message: '请输入预测单名称', trigger: 'blur' }]
}

const getForecastTypeName = (type: number) => {
  const names: Record<number, string> = {
    1: '月度预测',
    2: '季度预测',
    3: '年度预测'
  }
  return names[type] || '未知类型'
}

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return types[status] || 'info'
}

const getStatusName = (status: number) => {
  const names: Record<number, string> = {
    1: '草稿',
    2: '已确认',
    3: '已完成',
    4: '已取消'
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
    const res = await getForecastOrderList(params)
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
  searchForm.forecastNo = ''
  searchForm.forecastName = ''
  searchForm.forecastType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增预测单'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑预测单'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该预测单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteForecastOrder(row.id)
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
          await updateForecastOrder(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createForecastOrder(form)
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
    forecastNo: '',
    forecastName: '',
    forecastType: 1,
    startDate: '',
    endDate: '',
    totalQuantity: 0,
    remark: '',
    status: 1
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.forecast-order-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
