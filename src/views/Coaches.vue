<template>
  <div class="coaches-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>专业教练团队</h1>
      <p>认识我们的专业游泳教练团队，选择适合您的教练，开启游泳学习之旅</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索教练姓名或专业..."
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
              v-model="filterGender"
              placeholder="性别"
              clearable
              @change="handleGenderFilter"
              size="large"
          >
            <el-option
                v-for="gender in genderOptions"
                :key="gender"
                :label="gender"
                :value="gender"
            />
          </el-select>
        </el-col>

        <el-col :span="4">
          <el-select
              v-model="filterSpecialty"
              placeholder="专业领域"
              clearable
              @change="handleSpecialtyFilter"
              size="large"
          >
            <el-option
                v-for="specialty in specialtyOptions"
                :key="specialty"
                :label="specialty"
                :value="specialty"
            />
          </el-select>
        </el-col>

        <el-col :span="4">
          <el-button
              type="primary"
              @click="handleSearch"
              size="large"
              :loading="coachStore.loading"
          >
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>

        <el-col :span="6" class="text-right">
          <span class="coach-count">共 {{ filteredCoaches.length }} 位教练</span>
        </el-col>
      </el-row>
    </div>

    <!-- 教练统计 -->
    <div class="coach-stats" v-if="filteredCoaches.length > 0">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-statistic title="总教练数" :value="filteredCoaches.length" />
        </el-col>
        <el-col :span="6">
          <el-statistic
              title="男教练"
              :value="maleCoachesCount"
              suffix="位"
          />
        </el-col>
        <el-col :span="6">
          <el-statistic
              title="女教练"
              :value="femaleCoachesCount"
              suffix="位"
          />
        </el-col>
        <el-col :span="6">
          <el-statistic
              title="专业领域"
              :value="specialtyOptions.length"
              suffix="个"
          />
        </el-col>
      </el-row>
    </div>

    <!-- 教练列表 -->
    <div class="coaches-list">
      <el-row :gutter="24">
        <el-col
            :xs="24" :sm="12" :md="8" :lg="6"
            v-for="coach in paginatedCoaches"
            :key="coach.id"
        >
          <coach-card
              :coach="coach"
              @contact="handleContact"
          />
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <div v-if="filteredCoaches.length === 0 && !coachStore.loading" class="empty-state">
        <el-empty description="暂无相关教练">
          <el-button type="primary" @click="resetFilters">重置筛选条件</el-button>
        </el-empty>
      </div>

      <!-- 加载状态 -->
      <div v-if="coachStore.loading" class="loading-state">
        <el-skeleton :rows="6" animated />
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="filteredCoaches.length > pageSize">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[8, 16, 24, 32]"
          :total="filteredCoaches.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCoachStore } from '@/store/coach'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import CoachCard from '@/components/CoachCard.vue'

const router = useRouter()
const coachStore = useCoachStore()

// 响应式数据
const searchKeyword = ref('')
const filterGender = ref('')
const filterSpecialty = ref('')
const currentPage = ref(1)
const pageSize = ref(8)

// 计算属性
const genderOptions = computed(() => coachStore.genderOptions)
const specialtyOptions = computed(() => coachStore.specialtyOptions)
const filteredCoaches = computed(() => coachStore.filteredCoaches)

const paginatedCoaches = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredCoaches.value.slice(start, end)
})

const maleCoachesCount = computed(() =>
    filteredCoaches.value.filter(coach => coach.xingbie === '男').length
)

const femaleCoachesCount = computed(() =>
    filteredCoaches.value.filter(coach => coach.xingbie === '女').length
)

// 方法
const handleSearch = () => {
  currentPage.value = 1
  if (searchKeyword.value.trim()) {
    coachStore.searchCoaches(searchKeyword.value.trim())
  } else {
    coachStore.fetchCoaches()
  }
}

const handleGenderFilter = () => {
  currentPage.value = 1
  if (filterGender.value) {
    coachStore.filterByGender(filterGender.value)
  } else {
    coachStore.fetchCoaches()
  }
}

const handleSpecialtyFilter = () => {
  currentPage.value = 1
  // 专业筛选在计算属性中处理
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
}

// 联系教练
const handleContact = (coach) => {
  ElMessage.success(`联系教练: ${coach.jiaolianxingming}`)
  if (coach.lianxidianhua) {
    ElMessage.info(`联系电话: ${coach.lianxidianhua}`)
  }
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterGender.value = ''
  filterSpecialty.value = ''
  currentPage.value = 1
  coachStore.fetchCoaches()
}

// 监听筛选条件变化
watch([searchKeyword, filterGender, filterSpecialty], () => {
  currentPage.value = 1
})

// 生命周期
onMounted(() => {
  coachStore.fetchCoaches()
})
</script>

<style scoped>
.coaches-page {
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
  line-height: 1.6;
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

.coach-count {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.coach-stats {
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coaches-list {
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
  .coaches-page {
    padding: 10px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .search-filter {
    padding: 15px;
  }

  .coach-stats {
    padding: 15px;
  }

  .coach-stats .el-col {
    margin-bottom: 15px;
  }
}
</style>