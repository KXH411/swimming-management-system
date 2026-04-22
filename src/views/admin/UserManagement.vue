<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-actions">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名、账号或电话..."
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
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
        <el-col :span="10" class="text-right">
          <el-button type="success" @click="handleAddUser">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
          <el-button type="warning" @click="exportUsers">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 用户表格 -->
    <el-card class="table-card">
      <el-table
          :data="paginatedUsers"
          v-loading="loading"
          style="width: 100%"
          :default-sort="{ prop: 'addtime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="yonghuzhanghao" label="账号" width="120" />
        <el-table-column prop="yonghuxingming" label="姓名" width="100" />
        <el-table-column prop="xingbie" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.xingbie === '男' ? 'primary' : 'danger'">
              {{ row.xingbie }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lianxidianhua" label="联系电话" width="130" />
        <el-table-column prop="addtime" label="注册时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDate(row.addtime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditUser(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteUser(row)">
              删除
            </el-button>
            <el-button size="small" type="warning" @click="handleResetPassword(row)">
              重置密码
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
            :total="filteredUsers.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑用户' : '添加用户'"
        width="500px"
    >
      <el-form
          :model="userForm"
          :rules="userRules"
          ref="userFormRef"
          label-width="80px"
      >
        <el-form-item label="账号" prop="yonghuzhanghao">
          <el-input
              v-model="userForm.yonghuzhanghao"
              placeholder="请输入账号"
              :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="密码" prop="mima" v-if="!isEdit">
          <el-input
              v-model="userForm.mima"
              type="password"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>
        <el-form-item label="姓名" prop="yonghuxingming">
          <el-input
              v-model="userForm.yonghuxingming"
              placeholder="请输入真实姓名"
          />
        </el-form-item>
        <el-form-item label="性别" prop="xingbie">
          <el-radio-group v-model="userForm.xingbie">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="lianxidianhua">
          <el-input
              v-model="userForm.lianxidianhua"
              placeholder="请输入联系电话"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserForm" :loading="submitting">
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
import { Search, Plus, Download } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

// 响应式数据
const searchKeyword = ref('')
const filterGender = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const userFormRef = ref()
const userForm = ref({
  yonghuzhanghao: '',
  mima: '',
  yonghuxingming: '',
  xingbie: '男',
  lianxidianhua: ''
})

// 用户数据 - 从后端获取真实数据
const users = ref([])

// 验证规则
const userRules = {
  yonghuzhanghao: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  mima: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  yonghuxingming: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  xingbie: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  lianxidianhua: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 计算属性
const filteredUsers = computed(() => {
  let filtered = [...users.value]

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(user =>
        user.yonghuzhanghao.toLowerCase().includes(keyword) ||
        user.yonghuxingming.toLowerCase().includes(keyword) ||
        user.lianxidianhua.includes(keyword)
    )
  }

  if (filterGender.value) {
    filtered = filtered.filter(user => user.xingbie === filterGender.value)
  }

  return filtered
})

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUsers.value.slice(start, end)
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
  currentPage.value = 1
}

// 从后端API获取用户数据
const fetchUsers = async () => {
  loading.value = true
  try {
    console.log('🔄 从后端获取用户数据...')
    const response = await adminApi.getUsers()
    console.log('✅ 后端返回数据:', response)

    // ⭐️ 重要：你的后端直接返回 List<User>，不是 {success, data} 格式
    if (Array.isArray(response)) {
      users.value = response
      ElMessage.success(`成功加载 ${users.value.length} 个用户`)
    } else if (response && Array.isArray(response.data)) {
      // 如果是 {success, data} 格式
      users.value = response.data
      ElMessage.success(`成功加载 ${users.value.length} 个用户`)
    } else {
      ElMessage.error('获取用户数据失败: 数据格式不正确')
    }
  } catch (error) {
    console.error('❌ 获取用户列表失败:', error)
    ElMessage.error(`获取用户数据失败: ${error.response?.data?.message || error.message}`)
  } finally {
    loading.value = false
  }
}

const handleAddUser = () => {
  isEdit.value = false
  userForm.value = {
    yonghuzhanghao: '',
    mima: '',
    yonghuxingming: '',
    xingbie: '男',
    lianxidianhua: ''
  }
  dialogVisible.value = true
}

const handleEditUser = (user) => {
  isEdit.value = true
  userForm.value = { ...user }
  dialogVisible.value = true
}

const handleDeleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除用户 "${user.yonghuxingming}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    // 🔥 使用正确的删除接口
    const response = await adminApi.deleteUser(user.id)

    if (response && response.success) {
      // 从本地数据中移除
      const index = users.value.findIndex(u => u.id === user.id)
      if (index > -1) {
        users.value.splice(index, 1)
      }
      ElMessage.success('用户删除成功')
      // 刷新列表确保数据同步
      await fetchUsers()
    } else {
      ElMessage.error('删除用户失败: ' + (response?.message || '未知错误'))
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleResetPassword = async (user) => {
  try {
    await ElMessageBox.confirm(
        `确定要重置用户 "${user.yonghuxingming}" 的密码吗？`,
        '重置密码确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    try {
      // 调用后端API重置密码
      const response = await adminApi.resetUserPassword(user.id)
      if (response && response.success) {
        ElMessage.success(`用户 "${user.yonghuxingming}" 的密码已重置`)
      } else {
        ElMessage.error('重置密码失败: ' + (response.message || '未知错误'))
      }
    } catch (error) {
      console.error('重置密码失败:', error)
      ElMessage.error(`重置密码失败: ${error.response?.data?.message || error.message}`)
    }
  } catch {
    // 用户取消
  }
}

const submitUserForm = async () => {
  try {
    await userFormRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      // 更新用户 - 调用后端更新接口
      try {
        const response = await adminApi.updateUser(userForm.value)
        console.log('更新用户响应:', response)

        if (response && response.success) {
          // 更新本地数据
          const index = users.value.findIndex(u => u.id === userForm.value.id)
          if (index > -1) {
            users.value[index] = { ...userForm.value }
          }
          ElMessage.success('用户更新成功')
          dialogVisible.value = false
        } else {
          ElMessage.error('更新用户失败: ' + (response?.message || '未知错误'))
        }
      } catch (error) {
        console.error('更新用户失败:', error)
        ElMessage.error(`更新用户失败: ${error.response?.data?.message || error.message}`)
      }
    } else {
      // 添加用户 - 使用注册接口
      try {
        const response = await adminApi.addUser(userForm.value)
        console.log('添加用户响应:', response)

        if (response && response.success) {
          // 添加新用户到本地数据
          users.value.unshift(response.data || userForm.value)
          ElMessage.success('用户添加成功')
          dialogVisible.value = false
        } else {
          ElMessage.error('添加用户失败: ' + (response?.message || '未知错误'))
        }
      } catch (error) {
        console.error('添加用户失败:', error)
        ElMessage.error(`添加用户失败: ${error.response?.data?.message || error.message}`)
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

// 导出用户数据
const exportUsers = () => {
  if (filteredUsers.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }

  // 准备导出数据
  const exportData = filteredUsers.value.map(user => ({
    'ID': user.id,
    '账号': user.yonghuzhanghao,
    '姓名': user.yonghuxingming,
    '性别': user.xingbie,
    '联系电话': user.lianxidianhua || '',
    '注册时间': formatDate(user.addtime),
    '邮箱': user.email || '',
    '地址': user.address || '',
    '个人简介': user.jianjie || ''
  }))

  // 转换CSV格式
  const csvContent = convertToCSV(exportData)
  
  // 创建下载
  const blob = new Blob(["\uFEFF" + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.href = url
  const fileName = `用户列表_${new Date().toLocaleDateString('zh-CN').replace(/\//g, '-')}.csv`
  link.setAttribute('download', fileName)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  
  ElMessage.success(`成功导出 ${exportData.length} 条用户数据`)
}

// 将数据转换为CSV格式
const convertToCSV = (data) => {
  if (data.length === 0) return ''
  
  // 获取所有列名
  const headers = Object.keys(data[0])
  
  // 处理每个单元格的值（处理逗号和换行符）
  const escapeCell = (cell) => {
    if (cell === null || cell === undefined) return ''
    const str = String(cell)
    // 如果包含逗号、换行符或双引号，需要用双引号包裹并转义内部双引号
    if (str.includes(',') || str.includes('\n') || str.includes('"')) {
      return `"${str.replace(/"/g, '""')}"`
    }
    return str
  }
  
  // 构建CSV行
  const rows = [
    headers.join(','),  // 表头行
    ...data.map(row => headers.map(header => escapeCell(row[header])).join(','))
  ]
  
  return rows.join('\n')
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
  console.log('用户管理页面加载完成')
  fetchUsers()
})
</script>

<style scoped>
.user-management {
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