<template>
  <div class="net-requirement-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="计划编号">
          <el-input v-model="searchForm.planNo" placeholder="请输入计划编号" clearable />
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="searchForm.productCode" placeholder="请输入产品编码" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="待确认" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="已执行" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleCalculate">
          <el-icon><Calculator /></el-icon>
          执行净需求计算
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="planNo" label="计划编号" width="160" />
        <el-table-column prop="productCode" label="产品编码" width="120" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="requirementDate" label="需求日期" width="120" />
        <el-table-column prop="grossDemand" label="毛需求" width="100" />
        <el-table-column prop="stockQuantity" label="库存数量" width="100" />
        <el-table-column prop="lockedQuantity" label="锁定数量" width="100" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="availableQuantity" label="可用库存" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.availableQuantity < 0 ? '#f56c6c' : '#409eff' }">
              {{ row.availableQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="netRequirement" label="净需求" width="100">
          <template #default="{ row }">
            <el-tag :type="row.netRequirement > 0 ? 'danger' : 'success'">
              {{ row.netRequirement }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="plannedOrder" label="计划订单" width="100" />
        <el-table-column prop="statusName" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleConfirm(row)" :disabled="row.status !== 1">确认</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNetRequirementList, calculateNetRequirements, confirmNetRequirement, deleteNetRequirement } from '@/api/netRequirement'

const loading = ref(false)
const tableData = ref<any[]>([])

const searchForm = reactive({
  planNo: '',
  productCode: '',
  productName: '',
  status: null as number | null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
    1: 'warning',
    2: 'primary',
    3: 'success'
  }
  return types[status] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getNetRequirementList(params)
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
  searchForm.planNo = ''
  searchForm.productCode = ''
  searchForm.productName = ''
  searchForm.status = null
  handleSearch()
}

const handleCalculate = async () => {
  ElMessageBox.confirm('确定要执行净需求计算吗？\n系统将根据需求来源和库存数据自动计算净需求。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    loading.value = true
    try {
      await calculateNetRequirements()
      ElMessage.success('净需求计算完成')
      fetchData()
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

const handleConfirm = (row: any) => {
  ElMessageBox.confirm('确定要确认该净需求计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await confirmNetRequirement(row.id)
      ElMessage.success('确认成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该净需求计划吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNetRequirement(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.net-requirement-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
