<template>
  <div class="alternative-material-management">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="主料类型">
          <el-select v-model="searchForm.mainMaterialType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="产品" :value="1" />
            <el-option label="物料" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="替代料类型">
          <el-select v-model="searchForm.alternativeMaterialType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="产品" :value="1" />
            <el-option label="物料" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增替代料
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="主料信息" min-width="250">
          <template #default="{ row }">
            <div>{{ row.mainMaterialTypeName }}: {{ row.mainMaterialCode }} - {{ row.mainMaterialName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="替代料信息" min-width="250">
          <template #default="{ row }">
            <div>{{ row.alternativeMaterialTypeName }}: {{ row.alternativeMaterialCode }} - {{ row.alternativeMaterialName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="alternativeRatio" label="替代比例" width="100" />
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="getPriorityTagType(row.priority)">
              {{ row.priority }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicableScene" label="适用场景" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
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

      <!-- 分页 -->
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主料类型" prop="mainMaterialType">
              <el-select v-model="form.mainMaterialType" placeholder="请选择主料类型" style="width: 100%">
                <el-option label="产品" :value="1" />
                <el-option label="物料" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主料" prop="mainMaterialId">
              <el-select
                v-model="form.mainMaterialId"
                placeholder="请选择主料"
                style="width: 100%"
                filterable
                remote
                :remote-method="(query) => handleMainMaterialRemote(query, form.mainMaterialType)"
                :loading="mainMaterialLoading"
                :disabled="isEdit"
              >
                <el-option
                  v-for="item in mainMaterialOptions"
                  :key="item.id"
                  :label="item.code + ' - ' + item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="替代料类型" prop="alternativeMaterialType">
              <el-select v-model="form.alternativeMaterialType" placeholder="请选择替代料类型" style="width: 100%">
                <el-option label="产品" :value="1" />
                <el-option label="物料" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="替代料" prop="alternativeMaterialId">
              <el-select
                v-model="form.alternativeMaterialId"
                placeholder="请选择替代料"
                style="width: 100%"
                filterable
                remote
                :remote-method="(query) => handleAlternativeMaterialRemote(query, form.alternativeMaterialType)"
                :loading="alternativeMaterialLoading"
                :disabled="isEdit"
              >
                <el-option
                  v-for="item in alternativeMaterialOptions"
                  :key="item.id"
                  :label="item.code + ' - ' + item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="替代比例" prop="alternativeRatio">
              <el-input-number v-model="form.alternativeRatio" :min="0.001" :step="0.001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option label="1 (最高)" :value="1" />
                <el-option label="2" :value="2" />
                <el-option label="3" :value="3" />
                <el-option label="4" :value="4" />
                <el-option label="5 (最低)" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="适用场景" prop="applicableScene">
          <el-input v-model="form.applicableScene" type="textarea" :rows="2" placeholder="请输入适用场景" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getAlternativeMaterialList, createAlternativeMaterial, updateAlternativeMaterial, deleteAlternativeMaterial } from '@/api/bomVersion'
import { getProductList } from '@/api/product'
import { getMaterialList } from '@/api/material'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const mainMaterialLoading = ref(false)
const alternativeMaterialLoading = ref(false)
const mainMaterialOptions = ref<any[]>([])
const alternativeMaterialOptions = ref<any[]>([])

const searchForm = reactive({
  mainMaterialType: null as number | null,
  alternativeMaterialType: null as number | null,
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
  mainMaterialId: null as number | null,
  mainMaterialType: 1,
  alternativeMaterialId: null as number | null,
  alternativeMaterialType: 1,
  alternativeRatio: 1,
  priority: 1,
  applicableScene: '',
  remark: '',
  status: 1
})

const rules: FormRules = {
  mainMaterialType: [{ required: true, message: '请选择主料类型', trigger: 'change' }],
  mainMaterialId: [{ required: true, message: '请选择主料', trigger: 'change' }],
  alternativeMaterialType: [{ required: true, message: '请选择替代料类型', trigger: 'change' }],
  alternativeMaterialId: [{ required: true, message: '请选择替代料', trigger: 'change' }],
  alternativeRatio: [{ required: true, message: '请输入替代比例', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
}

// 监听主料类型变化
watch(() => form.mainMaterialType, () => {
  form.mainMaterialId = null
  mainMaterialOptions.value = []
})

// 监听替代料类型变化
watch(() => form.alternativeMaterialType, () => {
  form.alternativeMaterialId = null
  alternativeMaterialOptions.value = []
})

// 主料远程搜索
const handleMainMaterialRemote = async (query: string, type: number) => {
  if (!type) return
  mainMaterialLoading.value = true
  try {
    let res
    if (type === 1) {
      res = await getProductList()
    } else {
      res = await getMaterialList()
    }
    mainMaterialOptions.value = res.data.records.map((item: any) => ({
      id: item.id,
      code: item.productCode || item.materialCode,
      name: item.productName || item.materialName
    }))
  } catch (error) {
    console.error(error)
  } finally {
    mainMaterialLoading.value = false
  }
}

// 替代料远程搜索
const handleAlternativeMaterialRemote = async (query: string, type: number) => {
  if (!type) return
  alternativeMaterialLoading.value = true
  try {
    let res
    if (type === 1) {
      res = await getProductList()
    } else {
      res = await getMaterialList()
    }
    alternativeMaterialOptions.value = res.data.records.map((item: any) => ({
      id: item.id,
      code: item.productCode || item.materialCode,
      name: item.productName || item.materialName
    }))
  } catch (error) {
    console.error(error)
  } finally {
    alternativeMaterialLoading.value = false
  }
}

// 获取优先级标签类型
const getPriorityTagType = (priority: number) => {
  switch (priority) {
    case 1: return 'danger'
    case 2: return 'warning'
    case 3: return 'info'
    case 4: return 'primary'
    case 5: return 'success'
    default: return ''
  }
}

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getAlternativeMaterialList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.mainMaterialType = null
  searchForm.alternativeMaterialType = null
  searchForm.status = null
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增替代料'
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑替代料'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该替代料吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAlternativeMaterial(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

// 提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateAlternativeMaterial(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createAlternativeMaterial(form)
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

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    mainMaterialId: null,
    mainMaterialType: 1,
    alternativeMaterialId: null,
    alternativeMaterialType: 1,
    alternativeRatio: 1,
    priority: 1,
    applicableScene: '',
    remark: '',
    status: 1
  })
  mainMaterialOptions.value = []
  alternativeMaterialOptions.value = []
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.alternative-material-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
