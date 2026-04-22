<template>
  <div class="course-management">
    <div class="page-header">
      <h2>课程管理</h2>
      <p>管理系统所有游泳课程 - 真实数据</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="search-actions">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索课程名称或描述..."
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
          <el-select v-model="filterType" placeholder="课程类型" clearable @change="handleFilter">
            <el-option
                v-for="type in courseTypes"
                :key="type"
                :label="type"
                :value="type"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="sortBy" placeholder="排序方式" @change="handleSort">
            <el-option label="最新发布" value="latest" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="收藏最多" value="popular" />
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
          <el-button type="success" @click="handleAddCourse">
            <el-icon><Plus /></el-icon>
            添加课程
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 课程表格 -->
    <el-card class="table-card">
      <el-table
          :data="paginatedCourses"
          v-loading="loading"
          style="width: 100%"
          :default-sort="{ prop: 'addtime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" sortable />
        <el-table-column prop="xiangmumingcheng" label="课程名称" width="180" />
        <el-table-column prop="leixing" label="课程类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.leixing }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jiaolianmingcheng" label="教练" width="100" />
        <el-table-column prop="shichang" label="时长(分钟)" width="100">
          <template #default="{ row }">
            {{ row.shichang }}分钟
          </template>
        </el-table-column>
        <el-table-column prop="jiage" label="价格" width="100" sortable>
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.jiage }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="storeupnum" label="收藏数" width="80" sortable>
          <template #default="{ row }">
            <el-tag type="info">{{ row.storeupnum }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="添加时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatDate(row.addtime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditCourse(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteCourse(row)">
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
            :total="filteredCourses.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑课程' : '添加课程'"
        width="600px"
    >
      <el-form
          :model="courseForm"
          :rules="courseRules"
          ref="courseFormRef"
          label-width="100px"
      >
        <el-form-item label="课程名称" prop="xiangmumingcheng">
          <el-input v-model="courseForm.xiangmumingcheng" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程类型" prop="leixing">
          <el-select v-model="courseForm.leixing" placeholder="请选择课程类型" style="width: 100%">
            <el-option label="成人游泳" value="成人游泳" />
            <el-option label="儿童游泳" value="儿童游泳" />
            <el-option label="健身游泳" value="健身游泳" />
            <el-option label="竞技游泳" value="竞技游泳" />
            <el-option label="康复游泳" value="康复游泳" />
          </el-select>
        </el-form-item>
        <el-form-item label="教练姓名" prop="jiaolianmingcheng">
          <el-input v-model="courseForm.jiaolianmingcheng" placeholder="请输入教练姓名" />
        </el-form-item>
        <el-form-item label="课程时长" prop="shichang">
          <el-input-number
              v-model="courseForm.shichang"
              :min="30"
              :max="180"
              :step="15"
              placeholder="课程时长（分钟）"
              style="width: 100%"
          />
          <template #append>分钟</template>
        </el-form-item>
        <el-form-item label="课程价格" prop="jiage">
          <el-input-number
              v-model="courseForm.jiage"
              :min="0"
              :max="10000"
              :step="50"
              placeholder="课程价格"
              style="width: 100%"
          />
          <template #prefix>¥</template>
        </el-form-item>
        <el-form-item label="课程简介" prop="kechengjianjie">
          <el-input
              v-model="courseForm.kechengjianjie"
              type="textarea"
              :rows="4"
              placeholder="请输入课程简介"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCourseForm" :loading="submitting">
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
const filterType = ref('')
const sortBy = ref('latest')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const courseFormRef = ref()
const courseForm = ref({
  xiangmumingcheng: '',
  leixing: '',
  jiaolianmingcheng: '',
  shichang: 60,
  jiage: 200,
  kechengjianjie: ''
})

// 课程数据 - 从后端获取真实数据
const courses = ref([])

// 验证规则
const courseRules = {
  xiangmumingcheng: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  leixing: [
    { required: true, message: '请选择课程类型', trigger: 'change' }
  ],
  jiaolianmingcheng: [
    { required: true, message: '请输入教练姓名', trigger: 'blur' }
  ],
  shichang: [
    { required: true, message: '请输入课程时长', trigger: 'blur' }
  ],
  jiage: [
    { required: true, message: '请输入课程价格', trigger: 'blur' }
  ],
  kechengjianjie: [
    { required: true, message: '请输入课程简介', trigger: 'blur' }
  ]
}

// 计算属性
const courseTypes = computed(() => {
  const types = [...new Set(courses.value.map(course => course.leixing))]
  return types.filter(type => type).sort()
})

const filteredCourses = computed(() => {
  let filtered = [...courses.value]

  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(course =>
        course.xiangmumingcheng.toLowerCase().includes(keyword) ||
        course.kechengjianjie?.toLowerCase().includes(keyword) ||
        course.jiaolianmingcheng?.toLowerCase().includes(keyword)
    )
  }

  // 类型过滤
  if (filterType.value) {
    filtered = filtered.filter(course => course.leixing === filterType.value)
  }

  // 排序
  if (sortBy.value === 'price_asc') {
    filtered.sort((a, b) => (a.jiage || 0) - (b.jiage || 0))
  } else if (sortBy.value === 'price_desc') {
    filtered.sort((a, b) => (b.jiage || 0) - (a.jiage || 0))
  } else if (sortBy.value === 'popular') {
    filtered.sort((a, b) => (b.storeupnum || 0) - (a.storeupnum || 0))
  } else if (sortBy.value === 'latest') {
    filtered.sort((a, b) => new Date(b.addtime) - new Date(a.addtime))
  }

  return filtered
})

const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCourses.value.slice(start, end)
})

// 方法
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const handleSort = () => {
  currentPage.value = 1
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = ''
  sortBy.value = 'latest'
  currentPage.value = 1
}

// 从后端API获取课程数据
const fetchCourses = async () => {
  loading.value = true
  try {
    console.log('🔄 从后端获取课程数据...')
    const response = await adminApi.getCourses()
    console.log('✅ 后端返回课程数据:', response)

    if (response && response.success && Array.isArray(response.data)) {
      courses.value = response.data
      ElMessage.success(`成功加载 ${courses.value.length} 个课程`)
    } else {
      ElMessage.error('获取课程数据失败: 数据格式不正确')
    }
  } catch (error) {
    console.error('❌ 获取课程列表失败:', error)
    ElMessage.error(`获取课程数据失败: ${error.response?.data?.message || error.message}`)
  } finally {
    loading.value = false
  }
}

const handleAddCourse = () => {
  isEdit.value = false
  courseForm.value = {
    xiangmumingcheng: '',
    leixing: '',
    jiaolianmingcheng: '',
    shichang: 60,
    jiage: 200,
    kechengjianjie: ''
  }
  dialogVisible.value = true
}

const handleEditCourse = (course) => {
  isEdit.value = true
  courseForm.value = { ...course }
  dialogVisible.value = true
}

const handleDeleteCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除课程 "${course.xiangmumingcheng}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    try {
      // 调用后端API删除课程
      const response = await adminApi.deleteCourse(course.id)
      console.log('删除课程响应:', response)

      if (response && response.success) {
        // 从本地数据中移除
        const index = courses.value.findIndex(c => c.id === course.id)
        if (index > -1) {
          courses.value.splice(index, 1)
        }
        ElMessage.success('课程删除成功')
      } else {
        ElMessage.error('删除课程失败: ' + (response?.message || '未知错误'))
      }
    } catch (error) {
      console.error('删除课程失败:', error)
      ElMessage.error(`删除课程失败: ${error.response?.data?.message || error.message}`)
    }
  } catch {
    // 用户取消删除
  }
}

const submitCourseForm = async () => {
  try {
    await courseFormRef.value.validate()
    submitting.value = true

    if (isEdit.value) {
      // 更新课程 - 调用后端更新接口
      try {
        const response = await adminApi.updateCourse(courseForm.value)
        console.log('更新课程响应:', response)

        if (response && response.success) {
          // 更新本地数据
          const index = courses.value.findIndex(c => c.id === courseForm.value.id)
          if (index > -1) {
            courses.value[index] = { ...courseForm.value }
          }
          ElMessage.success('课程更新成功')
          dialogVisible.value = false
        } else {
          ElMessage.error('更新课程失败: ' + (response?.message || '未知错误'))
        }
      } catch (error) {
        console.error('更新课程失败:', error)
        ElMessage.error(`更新课程失败: ${error.response?.data?.message || error.message}`)
      }
    } else {
      // 添加课程 - 调用后端添加接口
      try {
        const response = await adminApi.addCourse(courseForm.value)
        console.log('添加课程响应:', response)

        if (response && response.success) {
          // 添加新课程到本地数据
          courses.value.unshift(response.data || courseForm.value)
          ElMessage.success('课程添加成功')
          dialogVisible.value = false
        } else {
          ElMessage.error('添加课程失败: ' + (response?.message || '未知错误'))
        }
      } catch (error) {
        console.error('添加课程失败:', error)
        ElMessage.error(`添加课程失败: ${error.response?.data?.message || error.message}`)
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
  console.log('课程管理页面加载完成')
  fetchCourses()
})
</script>

<style scoped>
.course-management {
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