<template>
  <div class="demand-source-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="来源编号">
          <el-input v-model="searchForm.sourceNo" placeholder="请输入来源编号" clearable />
        </el-form-item>
        <el-form-item label="来源类型">
          <el-select v-model="searchForm.sourceType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="销售订单" :value="1" />
            <el-option label="预测单" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="searchForm.productCode" placeholder="请输入产品编码" clearable />
        </el-form-item>
        <el-form-item label="产品名称">
          <el-input v-model="searchForm.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="待处理" :value="1" />
            <el-option label="已处理" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleSyncSales">
          <el-icon><Download /></el-icon>
          从销售订单同步
        </el-button>
        <el-button type="primary" @click="handleSyncForecast">
          <el-icon><Download /></el-icon>
          从预测单同步
        </el-button>
        <el-button type="success" @click="handleSyncAll">
          <el-icon><Refresh /></el-icon>
          同步所有需求
        </el-button>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="sourceNo" label="来源编号" width="160" />
        <el-table-column prop="sourceTypeName" label="来源类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.sourceType === 1 ? 'primary' : 'success'">
              {{ row.sourceTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="productCode" label="产品编码" width="120" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="specification" label="规格型号" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="demandQuantity" label="需求数量" width="100" />
        <el-table-column prop="demandDate" label="需求日期" width="120" />
        <el-table-column prop="allocatedQuantity" label="已分配数量" width="100" />
        <el-table-column prop="statusName" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
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
import { getDemandSourceList, syncFromSalesOrders, syncFromForecastOrders, syncAllDemandSources, deleteDemandSource } from '@/api/demandSource'

const loading = ref(false)
const tableData = ref<any[]>([])

const searchForm = reactive({
  sourceNo: '',
  sourceType: null as number | null,
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
    2: 'success',
    3: 'danger'
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
    const res = await getDemandSourceList(params)
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
  searchForm.sourceNo = ''
  searchForm.sourceType = null
  searchForm.productCode = ''
  searchForm.productName = ''
  searchForm.status = null
  handleSearch()
}

const handleSyncSales = async () => {
  ElMessageBox.confirm('确定要从销售订单同步需求来源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await syncFromSalesOrders()
      ElMessage.success('同步成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSyncForecast = async () => {
  ElMessageBox.confirm('确定要从预测单同步需求来源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await syncFromForecastOrders()
      ElMessage.success('同步成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSyncAll = async () => {
  ElMessageBox.confirm('确定要同步所有需求来源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await syncAllDemandSources()
      ElMessage.success('同步成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该需求来源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDemandSource(row.id)
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
.demand-source-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
