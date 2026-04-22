<template>
  <div class="bom-management">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="父件类型">
          <el-select v-model="searchForm.parentType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="产品" :value="1" />
            <el-option label="物料" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="子件类型">
          <el-select v-model="searchForm.childType" placeholder="请选择" clearable style="width: 120px">
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
          新增BOM
        </el-button>
        <el-button @click="handleViewTree">
          <el-icon><Tree /></el-icon>
          查看BOM树
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="parentTypeName" label="父件类型" width="100" />
        <el-table-column label="父件信息" min-width="200">
          <template #default="{ row }">
            <div>{{ row.parentCode }} - {{ row.parentName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="childTypeName" label="子件类型" width="100" />
        <el-table-column label="子件信息" min-width="200">
          <template #default="{ row }">
            <div>{{ row.childCode }} - {{ row.childName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="用量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="父件类型" prop="parentType">
              <el-select v-model="form.parentType" placeholder="请选择父件类型" style="width: 100%">
                <el-option label="产品" :value="1" />
                <el-option label="物料" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="父件" prop="parentId">
              <el-select
                v-model="form.parentId"
                placeholder="请选择父件"
                style="width: 100%"
                filterable
                remote
                :remote-method="(query) => handleParentRemote(query, form.parentType)"
                :loading="parentLoading"
              >
                <el-option
                  v-for="item in parentOptions"
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
            <el-form-item label="子件类型" prop="childType">
              <el-select v-model="form.childType" placeholder="请选择子件类型" style="width: 100%">
                <el-option label="产品" :value="1" />
                <el-option label="物料" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="子件" prop="childId">
              <el-select
                v-model="form.childId"
                placeholder="请选择子件"
                style="width: 100%"
                filterable
                remote
                :remote-method="(query) => handleChildRemote(query, form.childType)"
                :loading="childLoading"
              >
                <el-option
                  v-for="item in childOptions"
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
            <el-form-item label="用量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0.001" :step="0.001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="请输入单位" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
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

    <!-- BOM树对话框 -->
    <el-dialog
      v-model="treeDialogVisible"
      title="BOM层级结构"
      width="800px"
      @close="handleTreeDialogClose"
    >
      <el-form :inline="true" :model="treeForm" class="tree-search-form" style="margin-bottom: 20px">
        <el-form-item label="根节点类型">
          <el-select v-model="treeForm.parentType" placeholder="请选择" style="width: 120px">
            <el-option label="产品" :value="1" />
            <el-option label="物料" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="根节点">
          <el-select
            v-model="treeForm.parentId"
            placeholder="请选择根节点"
            style="width: 300px"
            filterable
            remote
            :remote-method="(query) => handleTreeRemote(query, treeForm.parentType)"
            :loading="treeLoading"
          >
            <el-option
              v-for="item in treeOptions"
              :key="item.id"
              :label="item.code + ' - ' + item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLoadTree">加载BOM树</el-button>
        </el-form-item>
      </el-form>
      <el-tree
        v-if="bomTree"
        :data="[bomTree]"
        :props="treeProps"
        node-key="id"
        default-expand-all
        style="max-height: 500px; overflow-y: auto"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span>{{ data.code }} - {{ data.name }}</span>
            <span v-if="data.quantity" class="node-quantity">
              ({{ data.quantity }} {{ data.unit }})
            </span>
          </span>
        </template>
      </el-tree>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getBomList, createBom, updateBom, deleteBom, getBomTree, getProductList, getMaterialList } from '@/api/bom'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const parentLoading = ref(false)
const childLoading = ref(false)
const parentOptions = ref<any[]>([])
const childOptions = ref<any[]>([])

const treeDialogVisible = ref(false)
const treeLoading = ref(false)
const treeOptions = ref<any[]>([])
const bomTree = ref<any>(null)

const searchForm = reactive({
  parentType: null as number | null,
  childType: null as number | null,
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
  parentId: null as number | null,
  parentType: 1,
  childId: null as number | null,
  childType: 1,
  quantity: 1,
  unit: '',
  remark: '',
  status: 1
})

const treeForm = reactive({
  parentId: null as number | null,
  parentType: 1
})

const rules: FormRules = {
  parentType: [{ required: true, message: '请选择父件类型', trigger: 'change' }],
  parentId: [{ required: true, message: '请选择父件', trigger: 'change' }],
  childType: [{ required: true, message: '请选择子件类型', trigger: 'change' }],
  childId: [{ required: true, message: '请选择子件', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入用量', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }]
}

const treeProps = {
  children: 'children',
  label: 'name'
}

// 监听父件类型变化
watch(() => form.parentType, () => {
  form.parentId = null
  parentOptions.value = []
})

// 监听子件类型变化
watch(() => form.childType, () => {
  form.childId = null
  childOptions.value = []
})

// 父件远程搜索
const handleParentRemote = async (query: string, type: number) => {
  if (!type) return
  parentLoading.value = true
  try {
    let res
    if (type === 1) {
      res = await getProductList()
    } else {
      res = await getMaterialList()
    }
    parentOptions.value = res.data.records.map((item: any) => ({
      id: item.id,
      code: item.productCode || item.materialCode,
      name: item.productName || item.materialName
    }))
  } catch (error) {
    console.error(error)
  } finally {
    parentLoading.value = false
  }
}

// 子件远程搜索
const handleChildRemote = async (query: string, type: number) => {
  if (!type) return
  childLoading.value = true
  try {
    let res
    if (type === 1) {
      res = await getProductList()
    } else {
      res = await getMaterialList()
    }
    childOptions.value = res.data.records.map((item: any) => ({
      id: item.id,
      code: item.productCode || item.materialCode,
      name: item.productName || item.materialName
    }))
  } catch (error) {
    console.error(error)
  } finally {
    childLoading.value = false
  }
}

// BOM树根节点远程搜索
const handleTreeRemote = async (query: string, type: number) => {
  if (!type) return
  treeLoading.value = true
  try {
    let res
    if (type === 1) {
      res = await getProductList()
    } else {
      res = await getMaterialList()
    }
    treeOptions.value = res.data.records.map((item: any) => ({
      id: item.id,
      code: item.productCode || item.materialCode,
      name: item.productName || item.materialName
    }))
  } catch (error) {
    console.error(error)
  } finally {
    treeLoading.value = false
  }
}

// 加载BOM树
const handleLoadTree = async () => {
  if (!treeForm.parentId || !treeForm.parentType) {
    ElMessage.warning('请选择根节点')
    return
  }
  loading.value = true
  try {
    const res = await getBomTree(treeForm.parentId, treeForm.parentType)
    bomTree.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
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
    const res = await getBomList(params)
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
  searchForm.parentType = null
  searchForm.childType = null
  searchForm.status = null
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增BOM'
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑BOM'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该BOM吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBom(row.id)
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
          await updateBom(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createBom(form)
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
    parentId: null,
    parentType: 1,
    childId: null,
    childType: 1,
    quantity: 1,
    unit: '',
    remark: '',
    status: 1
  })
  parentOptions.value = []
  childOptions.value = []
}

// 关闭BOM树对话框
const handleTreeDialogClose = () => {
  bomTree.value = null
  treeOptions.value = []
}

// 查看BOM树
const handleViewTree = () => {
  treeDialogVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.bom-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.tree-search-form {
  margin-bottom: 20px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-quantity {
  font-size: 12px;
  color: #606266;
}
</style>
