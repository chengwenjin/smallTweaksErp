<template>
  <div class="employee-schedule-management">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="排班编号">
          <el-input v-model="searchForm.scheduleNo" placeholder="请输入排班编号" clearable />
        </el-form-item>
        <el-form-item label="班组名称">
          <el-input v-model="searchForm.groupName" placeholder="请输入班组名称" clearable />
        </el-form-item>
        <el-form-item label="排班日期">
          <el-date-picker
            v-model="searchForm.scheduleDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="班次类型">
          <el-select v-model="searchForm.shiftType" placeholder="请选择" clearable style="width: 100px">
            <el-option label="白班" :value="1" />
            <el-option label="夜班" :value="2" />
            <el-option label="中班" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="待确认" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="已执行" :value="3" />
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
          新增排班
        </el-button>
        <el-button 
          type="success" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchStatus(2)"
        >
          批量确认
        </el-button>
        <el-button 
          type="warning" 
          :disabled="selectedIds.length === 0"
          @click="handleBatchStatus(4)"
        >
          批量取消
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
        <el-table-column prop="scheduleNo" label="排班编号" width="130" />
        <el-table-column prop="groupName" label="班组" width="120" />
        <el-table-column prop="scheduleDate" label="排班日期" width="110" />
        <el-table-column prop="shiftType" label="班次" width="80">
          <template #default="{ row }">
            {{ getShiftTypeName(row.shiftType) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="90" />
        <el-table-column prop="endTime" label="结束时间" width="90" />
        <el-table-column prop="workHours" label="工时(h)" width="80" />
        <el-table-column prop="employeeNames" label="排班人员" width="150" show-overflow-tooltip />
        <el-table-column prop="responsiblePerson" label="负责人" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 1" 
              link type="success" 
              size="small" 
              @click="handleStatus(row, 2)"
            >
              确认
            </el-button>
            <el-button 
              v-if="row.status === 2" 
              link type="success" 
              size="small" 
              @click="handleStatus(row, 3)"
            >
              执行
            </el-button>
            <el-button 
              v-if="row.status !== 4" 
              link type="warning" 
              size="small" 
              @click="handleStatus(row, 4)"
            >
              取消
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
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排班编号" prop="scheduleNo">
              <el-input v-model="form.scheduleNo" :disabled="isEdit" placeholder="自动生成或手动输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班组" prop="groupId">
              <el-select v-model="form.groupId" placeholder="请选择班组" style="width: 100%" @change="handleGroupChange">
                <el-option 
                  v-for="item in groupList" 
                  :key="item.id" 
                  :label="item.groupName" 
                  :value="item.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排班日期" prop="scheduleDate">
              <el-date-picker
                v-model="form.scheduleDate"
                type="date"
                placeholder="选择排班日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班次类型" prop="shiftType">
              <el-select v-model="form.shiftType" placeholder="请选择班次" style="width: 100%" @change="handleShiftTypeChange">
                <el-option label="白班" :value="1" />
                <el-option label="夜班" :value="2" />
                <el-option label="中班" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="form.startTime"
                placeholder="选择开始时间"
                value-format="HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                v-model="form.endTime"
                placeholder="选择结束时间"
                value-format="HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="工时(h)">
              <el-input-number v-model="form.workHours" :min="0" :max="24" :precision="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排班人员">
              <el-input v-model="form.employeeNames" placeholder="请输入排班人员，多个用逗号分隔" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-input v-model="form.responsiblePerson" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option label="待确认" :value="1" />
                <el-option label="已确认" :value="2" />
                <el-option label="已执行" :value="3" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { 
  getEmployeeScheduleList, 
  createEmployeeSchedule, 
  updateEmployeeSchedule, 
  deleteEmployeeSchedule,
  updateEmployeeScheduleStatus,
  updateEmployeeScheduleStatusBatch,
  deleteEmployeeScheduleBatch
} from '@/api/employeeSchedule'
import { getWorkGroupList } from '@/api/workGroup'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const selectedIds = ref<number[]>([])
const groupList = ref<any[]>([])

const searchForm = reactive({
  scheduleNo: '',
  groupName: '',
  scheduleDate: '',
  shiftType: null as number | null,
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
  scheduleNo: '',
  groupId: null as number | null,
  groupCode: '',
  groupName: '',
  scheduleDate: '',
  shiftType: 1,
  startTime: '08:00:00',
  endTime: '17:00:00',
  workHours: 8,
  employeeNames: '',
  responsiblePerson: '',
  remark: '',
  status: 1
})

const rules: FormRules = {
  scheduleNo: [{ required: true, message: '请输入排班编号', trigger: 'blur' }],
  groupId: [{ required: true, message: '请选择班组', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择排班日期', trigger: 'change' }],
  shiftType: [{ required: true, message: '请选择班次类型', trigger: 'change' }]
}

const shiftTypeMap = computed(() => ({
  1: { name: '白班', start: '08:00:00', end: '17:00:00', hours: 8 },
  2: { name: '夜班', start: '20:00:00', end: '06:00:00', hours: 10 },
  3: { name: '中班', start: '14:00:00', end: '23:00:00', hours: 9 }
}))

const getShiftTypeName = (type: number) => {
  return shiftTypeMap.value[type]?.name || '未知'
}

const getStatusName = (status: number) => {
  const statuses: Record<number, string> = {
    1: '待确认',
    2: '已确认',
    3: '已执行',
    4: '已取消'
  }
  return statuses[status] || '未知'
}

const getStatusTagType = (status: number) => {
  const types: Record<number, string> = {
    1: '',
    2: 'primary',
    3: 'success',
    4: 'info'
  }
  return types[status] || ''
}

const handleShiftTypeChange = (val: number) => {
  if (val && shiftTypeMap.value[val]) {
    const shift = shiftTypeMap.value[val]
    form.startTime = shift.start
    form.endTime = shift.end
    form.workHours = shift.hours
  }
}

const handleGroupChange = (val: number) => {
  const group = groupList.value.find(g => g.id === val)
  if (group) {
    form.groupCode = group.groupCode
    form.groupName = group.groupName
  }
}

const handleSelectionChange = (selection: any[]) => {
  selectedIds.value = selection.map(item => item.id)
}

const fetchGroupList = async () => {
  try {
    const res = await getWorkGroupList({ pageNum: 1, pageSize: 1000 })
    groupList.value = res.data.records || []
  } catch (error) {
    console.error(error)
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
    const res = await getEmployeeScheduleList(params)
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
  searchForm.scheduleNo = ''
  searchForm.groupName = ''
  searchForm.scheduleDate = ''
  searchForm.shiftType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增排班'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑排班'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleStatus = (row: any, status: number) => {
  const statusText = getStatusName(status)
  ElMessageBox.confirm(`确定要将排班标记为"${statusText}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateEmployeeScheduleStatus(row.id, status)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该排班吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteEmployeeSchedule(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchStatus = (status: number) => {
  const statusText = getStatusName(status)
  ElMessageBox.confirm(`确定要将选中的 ${selectedIds.value.length} 个排班标记为"${statusText}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateEmployeeScheduleStatusBatch(selectedIds.value, status)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个排班吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteEmployeeScheduleBatch(selectedIds.value)
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
          await updateEmployeeSchedule(form.id!, form)
          ElMessage.success('更新成功')
        } else {
          await createEmployeeSchedule(form)
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
    scheduleNo: '',
    groupId: null,
    groupCode: '',
    groupName: '',
    scheduleDate: '',
    shiftType: 1,
    startTime: '08:00:00',
    endTime: '17:00:00',
    workHours: 8,
    employeeNames: '',
    responsiblePerson: '',
    remark: '',
    status: 1
  })
}

onMounted(() => {
  fetchGroupList()
  fetchData()
})
</script>

<style scoped>
.employee-schedule-management {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
