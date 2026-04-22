<template>
  <div class="coach-management">
    <div class="page-header">
      <h2>教练管理</h2>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-actions">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索教练姓名、简介或专业..."
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterGender" placeholder="性别" clearable @change="handleFilter">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterSpecialty" placeholder="专业领域" clearable @change="handleFilter">
            <el-option label="成人游泳" value="成人游泳" />
            <el-option label="儿童游泳" value="儿童游泳" />
            <el-option label="健身游泳" value="健身游泳" />
            <el-option label="竞技游泳" value="竞技游泳" />
            <el-option label="康复游泳" value="康复游泳" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
        <el-col :span="6" class="text-right">
          <el-button type="success" @click="handleAddCoach">
            <el-icon><Plus /></el-icon>
            添加教练
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 教练表格 -->
    <el-card class="table-card">
      <el-table
          :data="paginatedCoaches"
          v-loading="loading"
          style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="jiaolianxingming" label="姓名" width="100" />
        <el-table-column prop="xingbie" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.xingbie === '男' ? 'primary' : 'danger'">
              {{ row.xingbie }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lianxidianhua" label="联系电话" width="130" />
        <el-table-column prop="zhuanye" label="专业领域" width="120">
          <template #default="{ row }">
            <el-tag type="success">{{ row.zhuanye }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jianjie" label="简介" min-width="200" show-overflow-tooltip />
        <el-table-column prop="addtime" label="添加时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.addtime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditCoach(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteCoach(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="filteredCoaches.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑教练对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑教练' : '添加教练'"
        width="600px"
    >
      <el-form
          :model="coachForm"
          :rules="coachRules"
          ref="coachFormRef"
          label-width="80px"
      >
        <el-form-item label="姓名" prop="jiaolianxingming">
          <el-input v-model="coachForm.jiaolianxingming" placeholder="请输入教练姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="xingbie">
          <el-radio-group v-model="coachForm.xingbie">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="lianxidianhua">
          <el-input v-model="coachForm.lianxidianhua" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="专业" prop="zhuanye">
          <el-select v-model="coachForm.zhuanye" placeholder="请选择专业领域" style="width: 100%">
            <el-option label="成人游泳" value="成人游泳" />
            <el-option label="儿童游泳" value="儿童游泳" />
            <el-option label="健身游泳" value="健身游泳" />
            <el-option label="竞技游泳" value="竞技游泳" />
            <el-option label="康复游泳" value="康复游泳" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介" prop="jianjie">
          <el-input
              v-model="coachForm.jianjie"
              type="textarea"
              :rows="4"
              placeholder="请输入教练简介"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCoachForm" :loading="submitting">
            {{ isEdit ? '更新' : '添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

// 响应式数据
const searchKeyword = ref('')
const filterGender = ref('')
const filterSpecialty = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const coachFormRef = ref()
const coachForm = ref({
  jiaolianxingming: '',
  xingbie: '男',
  lianxidianhua: '',
  zhuanye: '',
  jianjie: ''
})

// 教练数据 - 从后端获取真实数据
const coaches = ref([])

// 验证规则
const coachRules = {
  jiaolianxingming: [
    { required: true, message: '请输入教练姓名', trigger: 'blur' }
  ],
  xingbie: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  lianxidianhua: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  zhuanye: [
    { required: true, message: '请选择专业领域', trigger: 'change' }
  ],
  jianjie: [
    { required: true, message: '请输入教练简介', trigger: 'blur' }
  ]
}

// 计算属性
const filteredCoaches = computed(() => {
  let filtered = [...coaches.value]

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(coach =>
        coach.jiaolianxingming.toLowerCase().includes(keyword) ||
        coach.jianjie.toLowerCase().includes(keyword) ||
        coach.zhuanye.toLowerCase().includes(keyword)
    )
  }

  if (filterGender.value) {
    filtered = filtered.filter(coach => coach.xingbie === filterGender.value)
  }

  if (filterSpecialty.value) {
    filtered = filtered.filter(coach => coach.zhuanye === filterSpecialty.value)
  }

  return filtered
})

const paginatedCoaches = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCoaches.value.slice(start, end)
})

// 方法
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterGender.value = ''
  filterSpecialty.value = ''
  currentPage.value = 1
}

// 从后端API获取教练数据
const fetchCoaches = async () => {
  loading.value = true
  try {
    console.log('🔄 从后端获取教练数据...')
    const response = await adminApi.getCoaches()
    console.log('✅ 后端返回教练数据:', response)

    if (Array.isArray(response)) {
      coaches.value = response
      ElMessage.success(`成功加载 ${coaches.value.length} 位教练`)
    } else {
      ElMessage.error('获取教练数据失败: 数据格式不正确')
    }
  } catch (error) {
    console.error('❌ 获取教练列表失败:', error)
    ElMessage.error(`获取教练数据失败: ${error.response?.data?.message || error.message}`)
  } finally {
    loading.value = false
  }
}

const handleAddCoach = () => {
  isEdit.value = false
  coachForm.value = {
    jiaolianxingming: '',
    xingbie: '男',
    lianxidianhua: '',
    zhuanye: '',
    jianjie: ''
  }
  dialogVisible.value = true
}

const handleEditCoach = (coach) => {
  isEdit.value = true
  coachForm.value = { ...coach }
  dialogVisible.value = true
}

const handleDeleteCoach = async (coach) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除教练 "${coach.jiaolianxingming}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    try {
      // 调用后端API删除教练
      const response = await adminApi.deleteCoach(coach.id)
      console.log('删除教练响应:', response)

      if (response && response.success) {
        // 从本地数据中移除
        const index = coaches.value.findIndex(c => c.id === coach.id)
        if (index > -1) {
          coaches.value.splice(index, 1)
        }
        ElMessage.success('教练删除成功')
      } else {
        ElMessage.error('删除教练失败: ' + (response?.message || '未知错误'))
      }
    } catch (error) {
      console.error('删除教练失败:', error)
      ElMessage.error(`删除教练失败: ${error.response?.data?.message || error.message}`)
    }
  } catch {
    // 用户取消删除
  }
}

const submitCoachForm = async () => {
  try {
    await coachFormRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      // 更新教练 - 调用后端更新接口
      try {
        const response = await adminApi.updateCoach(coachForm.value)
        console.log('更新教练响应:', response)

        if (response && response.success) {
          // 更新本地数据
          const index = coaches.value.findIndex(c => c.id === coachForm.value.id)
          if (index > -1) {
            coaches.value[index] = { ...coachForm.value }
          }
          ElMessage.success('教练更新成功')
          dialogVisible.value = false
        } else {
          ElMessage.error('更新教练失败: ' + (response?.message || '未知错误'))
        }
      } catch (error) {
        console.error('更新教练失败:', error)
        ElMessage.error(`更新教练失败: ${error.response?.data?.message || error.message}`)
      }
    } else {
      // 添加教练 - 调用后端添加接口
      try {
        const response = await adminApi.addCoach(coachForm.value)
        console.log('添加教练响应:', response)

        if (response && response.success) {
          // 添加新教练到本地数据
          coaches.value.unshift(response.data || coachForm.value)
          ElMessage.success('教练添加成功')
          dialogVisible.value = false
        } else {
          ElMessage.error('添加教练失败: ' + (response?.message || '未知错误'))
        }
      } catch (error) {
        console.error('添加教练失败:', error)
        ElMessage.error(`添加教练失败: ${error.response?.data?.message || error.message}`)
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
}

// 生命周期
onMounted(() => {
  console.log('教练管理页面加载完成')
  fetchCoaches()
})
</script>

<style scoped>
.coach-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  margin: 0;
}

.search-actions {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.text-right {
  text-align: right;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>