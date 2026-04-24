<template>
  <div class="work-group-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="班组编码">
          <el-input v-model="searchForm.groupCode" placeholder="请输入班组编码" clearable />
        </el-form-item>
        <el-form-item label="班组名称">
          <el-input v-model="searchForm.groupName" placeholder="请输入班组名称" clearable />
        </el-form-item>
        <el-form-item label="班组类型">
          <el-select v-model="searchForm.groupType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="生产班" value="1" />
            <el-option label="维修班" value="2" />
            <el-option label="质检班" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
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
          新增班组
        </el-button>
        <el-button 
          type="success" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchStatus(1)"
        >
          批量启用
        </el-button>
        <el-button 
          type="warning" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchStatus(0)"
        >
          批量停用
        </el-button>
        <el-button 
          type="danger" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <el-table 
        :data="tableData" 
        border 
        stripe 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="groupCode" label="班组编码" width="120" />
        <el-table-column prop="groupName" label="班组名称" width="150" />
        <el-table-column prop="groupType" label="班组类型" width="100">
          <template #default="{ row }">
            {{ getGroupTypeName(row.groupType) }}
          </template>
        </el-table-column>
        <el-table-column prop="supervisor" label="班组长" width="100" />
        <el-table-column prop="supervisorPhone" label="联系电话" width="120" />
        <el-table-column prop="workshop" label="车间" width="100" />
        <el-table-column prop="workcenter" label="工作中心" width="100" />
        <el-table-column prop="memberCount" label="成员数量" width="90" />
        <el-table-column prop="skillLevel" label="技能等级" width="90">
          <template #default="{ row }">
            {{ getSkillLevelName(row.skillLevel) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status !== 1" 
              link type="success" 
              size="small" 
              @click="handleStatus(row, 1)"
            >
              启用
            </el-button>
            <el-button 
              v-if="row.status !== 0" 
              link type="warning" 
              size="small" 
              @click="handleStatus(row, 0)"
            >
              停用
            </el-button>
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
            <el-form-item label="班组编码" prop="groupCode">
              <el-input v-model="form.groupCode" :disabled="isEdit" placeholder="请输入班组编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班组名称" prop="groupName">
              <el-input v-model="form.groupName" placeholder="请输入班组名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班组类型">
              <el-select v-model="form.groupType" placeholder="请选择班组类型" style="width: 100%">
                <el-option label="生产班" value="1" />
                <el-option label="维修班" value="2" />
                <el-option label="质检班" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="技能等级">
              <el-select v-model="form.skillLevel" placeholder="请选择技能等级" style="width: 100%">
                <el-option label="初级" value="1" />
                <el-option label="中级" value="2" />
                <el-option label="高级" value="3" />
                <el-option label="专家级" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班组长">
              <el-input v-model="form.supervisor" placeholder="请输入班组长" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.supervisorPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="车间">
              <el-input v-model="form.workshop" placeholder="请输入车间" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作中心">
              <el-input v-model="form.workcenter" placeholder="请输入工作中心" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="成员数量">
              <el-input-number v-model="form.memberCount" :min="0" placeholder="请输入成员数量" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="启用" :value="1" />
                <el-option label="停用" :value="0" />
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
import { 
  getWorkGroupList, 
  createWorkGroup, 
  updateWorkGroup, 
  deleteWorkGroup,
  updateWorkGroupStatus,
  updateWorkGroupStatusBatch,
  deleteWorkGroupBatch
} from '@/api/workGroup'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const selectedIds = ref<number[]>([])

const searchForm = reactive({
  groupCode: '',
  groupName: '',
  groupType: '',
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
  groupCode: '',
  groupName: '',
  groupType: '1',
  supervisor: '',
  supervisorPhone: '',
  workshop: '',
  workcenter: '',
  memberCount: 5,
  skillLevel: '2',
  remark: '',
  status: 1
})

const rules: FormRules = {
  groupCode: [{ required: true, message: '请输入班组编码', trigger: 'blur' }],
  groupName: [{ required: true, message: '请输入班组名称', trigger: 'blur' }]
}

const getGroupTypeName = (type: string) => {
  const types: Record<string, string> = {
    '1': '生产班',
    '2': '维修班',
    '3': '质检班'
  }
  return types[type] || '其他'
}

const getSkillLevelName = (level: string) => {
  const levels: Record<string, string> = {
    '1': '初级',
    '2': '中级',
    '3': '高级',
    '4': '专家级'
  }
  return levels[level] || '未知'
}

const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getWorkGroupList(params)
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
  searchForm.groupCode = ''
  searchForm.groupName = ''
  searchForm.groupType = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增班组'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑班组'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleStatus = (row: any, status: number) => {
  ElMessageBox.confirm(`确定要将班组标记为"${status === 1 ? '启用' : '停用'}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateWorkGroupStatus(row.id, status)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该班组吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteWorkGroup(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchStatus = (status: number) => {
  ElMessageBox.confirm(`确定要将选中的 ${selectedIds.value.length} 个班组标记为"${status === 1 ? '启用' : '停用'}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateWorkGroupStatusBatch(selectedIds.value, status)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个班组吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteWorkGroupBatch(selectedIds.value)
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
          await updateWorkGroup(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createWorkGroup(form)
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
    groupCode: '',
    groupName: '',
    groupType: '1',
    supervisor: '',
    supervisorPhone: '',
    workshop: '',
    workcenter: '',
    memberCount: 5,
    skillLevel: '2',
    remark: '',
    status: 1
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.work-group-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
