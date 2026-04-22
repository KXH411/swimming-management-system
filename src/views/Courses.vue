<template>
  <div class="courses-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>游泳课程</h1>
      <p>选择适合您的游泳课程，提升游泳技能，享受健康生活</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-row :gutter="20" align="middle">
        <el-col :span="8">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索课程名称或描述..."
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
              size="large"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select
              v-model="filterType"
              placeholder="课程类型"
              clearable
              @change="handleFilter"
              size="large"
          >
            <el-option
                v-for="type in courseTypes"
                :key="type"
                :label="type"
                :value="type"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select
              v-model="sortBy"
              placeholder="排序方式"
              @change="handleSort"
              size="large"
          >
            <el-option label="最新发布" value="latest" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="收藏最多" value="popular" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button
              type="primary"
              @click="handleSearch"
              size="large"
              :loading="courseStore.loading"
          >
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="4" class="text-right">
          <span class="course-count">共 {{ filteredCourses.length }} 个课程</span>
        </el-col>
      </el-row>
    </div>

    <!-- 课程列表 -->
    <div class="courses-list">
      <el-row :gutter="20">
        <el-col
            :xs="24" :sm="12" :md="8"
            v-for="course in paginatedCourses"
            :key="course.id"
        >
          <course-card
              :course="course"
              :is-favorited="favoriteStore.isFavorited(course.id)"
              @view-detail="viewCourseDetail"
              @reserve="openReservationDialog"
              @favorite="toggleFavorite"
          />
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="filteredCourses.length === 0 && !courseStore.loading" class="empty-state">
        <el-empty description="暂无相关课程">
          <el-button type="primary" @click="resetFilters">重置筛选条件</el-button>
        </el-empty>
      </div>

      <!-- 加载状态 -->
      <div v-if="courseStore.loading" class="loading-state">
        <el-skeleton :rows="6" animated />
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="filteredCourses.length > pageSize">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[6, 12, 18, 24]"
          :total="filteredCourses.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新建预约对话框 -->
    <new-reservation-dialog
        v-model="showReservationDialog"
        :selected-course="selectedCourse"
        @success="handleReservationSuccess"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCourseStore } from '@/store/course'
import { useUserStore } from '@/store/user'
import { useFavoriteStore } from '@/store/favorite'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import CourseCard from '@/components/CourseCard.vue'
import NewReservationDialog from '@/components/NewReservationDialog.vue'

const router = useRouter()
const courseStore = useCourseStore()
const userStore = useUserStore()
const favoriteStore = useFavoriteStore()

// 响应式数据
const searchKeyword = ref('')
const filterType = ref('')
const sortBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(6)

// 预约对话框
const showReservationDialog = ref(false)
const selectedCourse = ref(null)

// 计算属性
const courseTypes = computed(() => courseStore.courseTypes)
const filteredCourses = computed(() => courseStore.filteredCourses)

const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCourses.value.slice(start, end)
})

// 方法
const handleSearch = () => {
  currentPage.value = 1
  if (searchKeyword.value.trim()) {
    courseStore.searchCourses(searchKeyword.value.trim())
  } else {
    courseStore.fetchCourses()
  }
}

const handleFilter = () => {
  currentPage.value = 1
}

const handleSort = () => {
  currentPage.value = 1
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
}

const viewCourseDetail = (courseId) => {
  router.push(`/course-detail/${courseId}`)
}

// 🔥 打开预约对话框
const openReservationDialog = (course) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  selectedCourse.value = course
  showReservationDialog.value = true
}

// 预约成功回调
const handleReservationSuccess = () => {
  console.log('预约成功，刷新数据')
}

// 切换收藏状态
const toggleFavorite = async (course) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再收藏')
    router.push('/login')
    return
  }

  try {
    const result = await favoriteStore.toggleFavorite(course.id, course)
    
    if (result.success) {
      const message = result.isFavorited ? '收藏成功' : '已取消收藏'
      ElMessage.success(message)
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterType.value = ''
  sortBy.value = 'latest'
  currentPage.value = 1
  courseStore.fetchCourses()
}

// 监听筛选条件变化
watch([searchKeyword, filterType, sortBy], () => {
  currentPage.value = 1
})

// 生命周期
onMounted(async () => {
  courseStore.fetchCourses()
  
  // 如果已登录，加载收藏列表
  if (userStore.isLoggedIn) {
    await favoriteStore.fetchMyFavorites()
  }
  
  console.log('🏊 课程页面加载完成')
})
</script>

<style scoped>
.courses-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 20px 0;
}

.page-header h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 32px;
}

.page-header p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.search-filter {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.text-right {
  text-align: right;
}

.course-count {
  color: #666;
  font-size: 14px;
}

.courses-list {
  margin-bottom: 40px;
  min-height: 400px;
}

.empty-state {
  padding: 60px 0;
}

.loading-state {
  padding: 20px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .courses-page {
    padding: 10px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .search-filter {
    padding: 15px;
  }
}
</style>