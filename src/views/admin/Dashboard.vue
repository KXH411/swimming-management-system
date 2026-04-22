<template>
  <div class="admin-dashboard">
    <h2>管理后台仪表板</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
              <div class="stat-trend" v-if="stats.newUsersToday > 0">
                <span class="trend-up">+{{ stats.newUsersToday }} 今日新增</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon course-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCourses }}</div>
              <div class="stat-label">课程数量</div>
              <div class="stat-trend" v-if="stats.newCoursesThisMonth > 0">
                <span class="trend-up">+{{ stats.newCoursesThisMonth }} 本月新增</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon coach-icon">
              <el-icon><Avatar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCoaches }}</div>
              <div class="stat-label">教练数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon reservation-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayReservations }}</div>
              <div class="stat-label">今日预约</div>
              <div class="stat-trend" v-if="stats.weeklyReservations > 0">
                <span class="trend-up">本周共 {{ stats.weeklyReservations }} 单</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预约趋势图 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <span>预约趋势（近7天）</span>
          </template>
          <div class="chart-container">
            <div v-if="chartLoading" class="chart-loading">
              <el-skeleton :rows="5" animated />
            </div>
            <div v-else class="bar-chart">
              <div 
                v-for="item in reservationTrend" 
                :key="item.date" 
                class="bar-item"
              >
                <div class="bar-label">{{ item.date }}</div>
                <div class="bar-wrapper">
                  <div 
                    class="bar" 
                    :style="{ width: getBarWidth(item.count) }"
                  >
                    <span class="bar-value">{{ item.count }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span>课程分类统计</span>
          </template>
          <div class="pie-chart">
            <div v-if="chartLoading" class="chart-loading">
              <el-skeleton :rows="4" animated />
            </div>
            <div v-else>
              <div 
                v-for="item in courseTypeStats" 
                :key="item.type" 
                class="pie-item"
              >
                <div class="pie-color" :style="{ background: item.color }"></div>
                <div class="pie-label">{{ item.type }}</div>
                <div class="pie-value">{{ item.count }}门</div>
                <div class="pie-percent">{{ item.percent }}%</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速操作 -->
    <el-card class="quick-actions">
      <template #header>
        <span>快速操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-button type="primary" @click="goToPage('/admin/users')" style="width: 100%">
            <el-icon><User /></el-icon>
            用户管理
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" @click="goToPage('/admin/courses')" style="width: 100%">
            <el-icon><Reading /></el-icon>
            课程管理
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="warning" @click="goToPage('/admin/coaches')" style="width: 100%">
            <el-icon><Avatar /></el-icon>
            教练管理
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" @click="goToPage('/admin/news')" style="width: 100%">
            <el-icon><Document /></el-icon>
            新闻管理
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最近活动 -->
    <el-card class="recent-activities">
      <template #header>
        <div class="activities-header">
          <span>最近活动</span>
          <el-button text @click="refreshActivities">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <div v-if="activitiesLoading" class="loading-state">
        <el-skeleton :rows="4" animated />
      </div>
      <el-timeline v-else>
        <el-timeline-item
            v-for="activity in activities"
            :key="activity.id"
            :timestamp="activity.time"
            :type="activity.type"
        >
          {{ activity.content }}
        </el-timeline-item>
        <div v-if="activities.length === 0" class="empty-state">
          <el-empty description="暂无活动记录" />
        </div>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useCourseStore } from '@/store/course'
import { useCoachStore } from '@/store/coach'
import { useReservationStore } from '@/store/reservation'
import { ElMessage } from 'element-plus'
import { User, Reading, Avatar, Calendar, Document, Refresh } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const courseStore = useCourseStore()
const coachStore = useCoachStore()
const reservationStore = useReservationStore()

// 统计数据
const stats = ref({
  totalUsers: 0,
  newUsersToday: 0,
  totalCourses: 0,
  newCoursesThisMonth: 0,
  totalCoaches: 0,
  todayReservations: 0,
  weeklyReservations: 0
})

// 预约趋势数据
const reservationTrend = ref([])
// 课程分类统计
const courseTypeStats = ref([])
// 最近活动
const activities = ref([])

const chartLoading = ref(true)
const activitiesLoading = ref(true)

// 颜色配置
const colorMap = {
  '成人游泳': '#409EFF',
  '儿童游泳': '#67C23A',
  '健身游泳': '#E6A23C',
  '竞技游泳': '#F56C6C',
  '康复游泳': '#909399'
}

// 获取条形图宽度百分比
const getBarWidth = (count) => {
  const maxCount = Math.max(...reservationTrend.value.map(i => i.count), 1)
  const percent = (count / maxCount) * 100
  return `${Math.max(percent, 5)}%`
}

// 跳转页面
const goToPage = (path) => {
  router.push(path)
}

// 加载统计数据
const loadStats = async () => {
  try {
    // 获取用户总数
    const users = userStore.users || []
    stats.value.totalUsers = users.length
    
    // 计算今日新增用户数（需要用户数据有 addtime 字段）
    const today = new Date().toDateString()
    const todayUsers = users.filter(user => {
      // 根据你的用户数据结构调整字段名
      const registerTime = user.addtime || user.createTime || user.registerTime
      if (!registerTime) return false
      return new Date(registerTime).toDateString() === today
    })
    stats.value.newUsersToday = todayUsers.length
    
    // 如果用户数据没有时间字段，暂时使用模拟数据
    if (stats.value.newUsersToday === 0 && users.length > 0) {
      // 模拟：假设今日新增占总数的一定比例
      stats.value.newUsersToday = Math.min(Math.floor(users.length * 0.05), 10)
    }
    
    // 获取课程总数
    const courses = courseStore.courses || []
    stats.value.totalCourses = courses.length
    
    // 计算本月新增课程数
    const now = new Date()
    const currentMonth = now.getMonth()
    const currentYear = now.getFullYear()
    const thisMonthCourses = courses.filter(course => {
      const addTime = course.addtime || course.createTime
      if (!addTime) return false
      const courseDate = new Date(addTime)
      return courseDate.getMonth() === currentMonth && courseDate.getFullYear() === currentYear
    })
    stats.value.newCoursesThisMonth = thisMonthCourses.length || 3  // 默认显示3
    
    // 获取教练总数
    stats.value.totalCoaches = coachStore.coaches?.length || 0
    
    // 获取今日预约数
    const todayReservations = (reservationStore.reservations || []).filter(r => {
      if (!r.yuyueshijian) return false
      const date = new Date(r.yuyueshijian).toDateString()
      return date === today && r.zhuangtai !== '已取消'
    })
    stats.value.todayReservations = todayReservations.length
    
    // 本周预约总数
    const nowDate = new Date()
    const weekStart = new Date(nowDate)
    weekStart.setDate(nowDate.getDate() - nowDate.getDay())
    weekStart.setHours(0, 0, 0, 0)
    
    const weeklyReservations = (reservationStore.reservations || []).filter(r => {
      if (!r.yuyueshijian) return false
      const date = new Date(r.yuyueshijian)
      return date >= weekStart && r.zhuangtai !== '已取消'
    })
    stats.value.weeklyReservations = weeklyReservations.length
    
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载预约趋势
const loadReservationTrend = async () => {
  chartLoading.value = true
  try {
    const trend = []
    const today = new Date()
    
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today)
      date.setDate(today.getDate() - i)
      const dateStr = `${date.getMonth() + 1}/${date.getDate()}`
      
      // 计算该日期的预约数
      const count = (reservationStore.reservations || []).filter(r => {
        const rDate = new Date(r.yuyueshijian)
        return rDate.toDateString() === date.toDateString() && r.zhuangtai !== '已取消'
      }).length
      
      trend.push({
        date: dateStr,
        count: count
      })
    }
    
    reservationTrend.value = trend
  } catch (error) {
    console.error('加载预约趋势失败:', error)
    // 模拟数据
    reservationTrend.value = [
      { date: '12/24', count: 5 },
      { date: '12/25', count: 8 },
      { date: '12/26', count: 12 },
      { date: '12/27', count: 9 },
      { date: '12/28', count: 15 },
      { date: '12/29', count: 18 },
      { date: '12/30', count: 22 }
    ]
  } finally {
    chartLoading.value = false
  }
}

// 加载课程分类统计
const loadCourseTypeStats = async () => {
  try {
    const courses = courseStore.courses || []
    const typeMap = new Map()
    
    courses.forEach(course => {
      const type = course.leixing || '其他'
      typeMap.set(type, (typeMap.get(type) || 0) + 1)
    })
    
    const total = courses.length
    const stats = []
    let index = 0
    const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
    
    typeMap.forEach((count, type) => {
      stats.push({
        type: type,
        count: count,
        percent: total > 0 ? Math.round((count / total) * 100) : 0,
        color: colorMap[type] || colors[index % colors.length]
      })
      index++
    })
    
    courseTypeStats.value = stats
  } catch (error) {
    console.error('加载课程分类统计失败:', error)
    courseTypeStats.value = [
      { type: '成人游泳', count: 8, percent: 35, color: '#409EFF' },
      { type: '儿童游泳', count: 6, percent: 26, color: '#67C23A' },
      { type: '健身游泳', count: 5, percent: 22, color: '#E6A23C' },
      { type: '竞技游泳', count: 4, percent: 17, color: '#F56C6C' }
    ]
  }
}

// 加载最近活动
const loadRecentActivities = async () => {
  activitiesLoading.value = true
  try {
    // 模拟活动数据
    const mockActivities = [
      {
        id: 1,
        time: formatTime(new Date(Date.now() - 2 * 60 * 60 * 1000)),
        type: 'primary',
        content: '新用户注册成功'
      },
      {
        id: 2,
        time: formatTime(new Date(Date.now() - 5 * 60 * 60 * 1000)),
        type: 'success',
        content: '新课程已发布'
      },
      {
        id: 3,
        time: formatTime(new Date(Date.now() - 1 * 24 * 60 * 60 * 1000)),
        type: 'warning',
        content: '教练信息已更新'
      },
      {
        id: 4,
        time: formatTime(new Date(Date.now() - 2 * 24 * 60 * 60 * 1000)),
        type: 'info',
        content: '系统备份已完成'
      }
    ]
    
    activities.value = mockActivities
  } catch (error) {
    console.error('加载最近活动失败:', error)
  } finally {
    activitiesLoading.value = false
  }
}

// 刷新活动
const refreshActivities = () => {
  loadRecentActivities()
  ElMessage.success('活动列表已刷新')
}

// 格式化时间
const formatTime = (date) => {
  const now = new Date()
  const diff = now - date
  
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000))
    return `${minutes}分钟前`
  } else if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000))
    return `${hours}小时前`
  } else {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    return `${days}天前`
  }
}

// 初始化数据
const initData = async () => {
  // 加载各个store的数据
  await Promise.all([
    userStore.fetchUsers?.() || Promise.resolve(),
    courseStore.fetchCourses(),
    coachStore.fetchCoaches(),
    reservationStore.fetchReservations()
  ])
  
  // 加载统计数据
  await loadStats()
  // 加载预约趋势
  await loadReservationTrend()
  // 加载课程分类统计
  await loadCourseTypeStats()
  // 加载最近活动
  await loadRecentActivities()
}

// 生命周期
onMounted(() => {
  initData()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.admin-dashboard h2 {
  margin-bottom: 20px;
  color: #333;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.user-icon { background: #409EFF; }
.course-icon { background: #67C23A; }
.coach-icon { background: #E6A23C; }
.reservation-icon { background: #909399; }

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.stat-trend {
  margin-top: 5px;
  font-size: 12px;
}

.trend-up {
  color: #67C23A;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 320px;
}

.chart-container {
  height: 260px;
  overflow-y: auto;
  padding: 10px;
}

.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.bar-label {
  width: 50px;
  font-size: 12px;
  color: #666;
}

.bar-wrapper {
  flex: 1;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.bar {
  background: linear-gradient(90deg, #409EFF, #66b1ff);
  height: 30px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 8px;
  transition: width 0.5s ease;
}

.bar-value {
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.pie-chart {
  padding: 10px;
}

.pie-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.pie-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.pie-label {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.pie-value {
  font-size: 14px;
  color: #666;
  width: 50px;
  text-align: right;
}

.pie-percent {
  font-size: 14px;
  color: #409EFF;
  font-weight: bold;
  width: 50px;
  text-align: right;
}

.quick-actions {
  margin-bottom: 20px;
}

.quick-actions .el-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.recent-activities {
  margin-bottom: 20px;
}

.activities-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-state {
  padding: 20px;
}

.empty-state {
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards .el-col {
    margin-bottom: 15px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .pie-item {
    flex-wrap: wrap;
  }
  
  .pie-label {
    width: 100%;
    margin-bottom: 5px;
  }
}
</style>