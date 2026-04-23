<template>
  <div class="inventory-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="产品编码">
          <el-input v-model="searchForm.productCode" placeholder="请输入产品编码" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="仓库编码">
          <el-input v-model="searchForm.warehouseCode" placeholder="请输入仓库编码" clearable />
        </el-form-item>
        <el-form-item label="仓库名称">
          <el-input v-model="searchForm.warehouseName" placeholder="请输入仓库名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="正常" :value="1" />
            <el-option label="冻结" :value="2" />
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
          新增库存
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="productCode" label="产品编码" width="120" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="warehouseCode" label="仓库编码" width="100" />
        <el-table-column prop="warehouseName" label="仓库名称" width="120" />
        <el-table-column prop="locationCode" label="库位" width="100" />
        <el-table-column prop="quantity" label="库存数量" width="100" />
        <el-table-column prop="lockedQuantity" label="锁定数量" width="100" />
        <el-table-column prop="availableQuantity" label="可用数量" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.availableQuantity < 0 ? '#f56c6c' : '#409eff' }">
              {{ row.availableQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="batchNo" label="批次号" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '冻结' }}
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
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
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
            <el-form-item label="规格型号">
              <el-input v-model="form.specification" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位">
              <el-input v-model="form.unit" placeholder="请输入计量单位" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库编码">
              <el-input v-model="form.warehouseCode" placeholder="请输入仓库编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称">
              <el-input v-model="form.warehouseName" placeholder="请输入仓库名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库位编码">
              <el-input v-model="form.locationCode" placeholder="请输入库位编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次号">
              <el-input v-model="form.batchNo" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存数量">
              <el-input-number v-model="form.quantity" :precision="2" :min="0" placeholder="请输入库存数量" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="锁定数量">
              <el-input-number v-model="form.lockedQuantity" :precision="2" :min="0" placeholder="请输入锁定数量" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="安全库存">
              <el-input-number v-model="form.safetyStock" :precision="2" :min="0" placeholder="请输入安全库存" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="2">冻结</el-radio>
              </el-radio-group>
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
import { getInventoryList, createInventory, updateInventory, deleteInventory } from '@/api/inventory'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const searchForm = reactive({
  productCode: '',
  productName: '',
  warehouseCode: '',
  warehouseName: '',
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
  productId: null as number | null,
  productCode: '',
  productName: '',
  specification: '',
  unit: '',
  warehouseCode: '',
  warehouseName: '',
  locationCode: '',
  quantity: 0,
  lockedQuantity: 0,
  safetyStock: 0,
  batchNo: '',
  remark: '',
  status: 1
})

const rules: FormRules = {
  productCode: [{ required: true, message: '请输入产品编码', trigger: 'blur' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getInventoryList(params)
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
  searchForm.productCode = ''
  searchForm.productName = ''
  searchForm.warehouseCode = ''
  searchForm.warehouseName = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增库存'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑库存'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该库存吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteInventory(row.id)
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
          await updateInventory(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createInventory(form)
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
    productId: null,
    productCode: '',
    productName: '',
    specification: '',
    unit: '',
    warehouseCode: '',
    warehouseName: '',
    locationCode: '',
    quantity: 0,
    lockedQuantity: 0,
    safetyStock: 0,
    batchNo: '',
    remark: '',
    status: 1
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.inventory-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
