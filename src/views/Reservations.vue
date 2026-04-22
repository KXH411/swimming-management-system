<template>
  <div class="reservations-page">
    <div class="page-header">
      <h1>我的预约</h1>
      <p>查看和管理您的游泳课程预约</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter" v-if="reservations.length > 0 || reservationStore.searchKeyword">
      <el-row :gutter="20" align="middle">
        <el-col :span="8">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索课程、教练或预约编号..."
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="6">
          <el-select
              v-model="filterStatus"
              placeholder="预约状态"
              clearable
              @change="handleStatusFilter"
          >
            <el-option
                v-for="option in statusOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button @click="clearFilters" :disabled="!hasActiveFilters">
            清除筛选
          </el-button>
        </el-col>
        <el-col :span="6" class="text-right">
          <span class="reservation-count">共 {{ reservationStats.total }} 个预约</span>
        </el-col>
      </el-row>
    </div>

    <!-- 预约统计 -->
    <div class="reservation-stats" v-if="reservations.length > 0">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-statistic title="总预约数" :value="reservationStats.total" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="待确认" :value="reservationStats.pending" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="已确认" :value="reservationStats.confirmed" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="进行中" :value="reservationStats.inProgress" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="已完成" :value="reservationStats.completed" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="已取消" :value="reservationStats.cancelled" />
        </el-col>
      </el-row>
    </div>

    <!-- 今日预约提醒 -->
    <div class="today-reminder" v-if="todayReservations.length > 0">
      <el-alert
          title="今日预约提醒"
          type="info"
          :closable="false"
          show-icon
      >
        <p>您今天有 {{ todayReservations.length }} 个预约：</p>
        <div class="today-list">
          <span v-for="reservation in todayReservations" :key="reservation.id" class="today-item">
            {{ reservation.xiangmumingcheng }} ({{ formatTime(reservation.yuyueshijian) }})
          </span>
        </div>
      </el-alert>
    </div>

    <!-- 预约列表 -->
    <div class="reservations-list">
      <el-card
          v-for="reservation in paginatedReservations"
          :key="reservation.id"
          class="reservation-card"
          :class="getReservationCardClass(reservation)"
      >
        <template #header>
          <div class="reservation-header">
            <div class="header-left">
              <span class="course-name">{{ reservation.xiangmumingcheng }}</span>
              <span class="reservation-number">({{ reservation.yuyuebianhao || `预约#${reservation.id}` }})</span>
              <el-tag
                  v-if="isToday(reservation.yuyueshijian) && reservation.zhuangtai === '已确认'"
                  type="warning"
                  size="small"
              >
                今日
              </el-tag>
              <el-tag
                  v-if="isUpcoming(reservation.yuyueshijian) && reservation.zhuangtai === '已确认'"
                  type="success"
                  size="small"
              >
                即将开始
              </el-tag>
            </div>
            <el-tag :type="getStatusType(reservation.zhuangtai)">
              {{ reservation.zhuangtai }}
            </el-tag>
          </div>
        </template>

        <div class="reservation-content">
          <div class="reservation-info">
            <p><strong>课程类型:</strong> {{ reservation.leixing }}</p>
            <p><strong>教练:</strong> {{ reservation.jiaolianmingcheng }}</p>
           <p><strong>预约时间:</strong> 
  <el-tag size="small" type="success">{{ formatTime(reservation.yuyueshijian) }}</el-tag>
</p>
            <p><strong>创建时间:</strong> {{ formatDateTime(reservation.addtime) }}</p>
            <p v-if="reservation.beizhu"><strong>备注:</strong> {{ reservation.beizhu }}</p>

            <!-- 预约时间状态提示 -->
            <div v-if="showTimeStatus(reservation)" class="time-status">
              <el-alert
                  :title="getTimeStatusText(reservation)"
                  :type="getTimeStatusType(reservation)"
                  :closable="false"
                  show-icon
                  size="small"
              />
            </div>
          </div>

          <div class="reservation-actions">
            <!-- 待确认和已确认的预约可以取消 -->
            <el-button
                v-if="['待确认', '已确认', '进行中'].includes(reservation.zhuangtai)"
                type="danger"
                size="small"
                @click="handleCancel(reservation)"
                :loading="cancellingId === reservation.id"
            >
              取消预约
            </el-button>

            <!-- 已取消的预约可以重新预约 -->
            <el-button
                v-else-if="reservation.zhuangtai === '已取消'"
                type="primary"
                size="small"
                @click="handleRebook(reservation)"
            >
              重新预约
            </el-button>

            <!-- 删除已取消或已完成的预约 -->
            <el-button
                v-if="['已取消', '已完成'].includes(reservation.zhuangtai)"
                type="danger"
                size="small"
                plain
                @click="handleDelete(reservation)"
                :loading="deletingId === reservation.id"
            >
              删除记录
            </el-button>


          </div>
        </div>
      </el-card>

      <!-- 空状态 -->
      <div v-if="reservations.length === 0 && !reservationStore.loading" class="empty-state">
        <el-empty :description="emptyDescription">
          <el-button type="primary" @click="$router.push('/courses')">
            去预约课程
          </el-button>
          <el-button @click="showQuickDemo" style="margin-left: 12px;">
            快速体验
          </el-button>
        </el-empty>
      </div>

      <!-- 加载状态 -->
      <div v-if="reservationStore.loading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="reservations.length > 0">
      <el-pagination
          v-model:current-page="reservationStore.currentPage"
          v-model:page-size="reservationStore.pageSize"
          :total="reservations.length"
          :page-sizes="[8, 16, 24, 32]"
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
import { useReservationStore } from '@/store/reservation'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const reservationStore = useReservationStore()

// 响应式数据
const cancellingId = ref(null)
const deletingId = ref(null)
const isDevelopment = ref(false)

// 计算属性（添加默认值防止 undefined）
const reservations = computed(() => reservationStore.filteredReservations || [])
const paginatedReservations = computed(() => reservationStore.paginatedReservations || [])
const reservationStats = computed(() => {
  const stats = reservationStore.reservationStats
  return stats || {
    total: 0,
    pending: 0,
    confirmed: 0,
    inProgress: 0,
    completed: 0,
    cancelled: 0
  }
})
const statusOptions = computed(() => reservationStore.statusOptions || [])
const todayReservations = computed(() => reservationStore.todayReservations || [])
const upcomingReservations = computed(() => reservationStore.upcomingReservations || [])
// 搜索和筛选相关
const searchKeyword = ref('')
const filterStatus = ref('')

const hasActiveFilters = computed(() => {
  return searchKeyword.value || filterStatus.value
})

const emptyDescription = computed(() => {
  if (reservationStore.searchKeyword) {
    return `没有找到包含"${reservationStore.searchKeyword}"的预约`
  } else if (reservationStore.filterStatus) {
    return `没有${reservationStore.statusOptions.find(opt => opt.value === reservationStore.filterStatus)?.label}的预约`
  }
  return '暂无预约记录'
})

// 加载预约数据
const loadReservations = async () => {
  try {
    console.log('🔄 开始加载预约数据...')
    await reservationStore.fetchReservations()
    console.log('✅ 预约数据加载成功，数量:', reservations.value.length)
  } catch (error) {
    console.error('❌ 加载预约记录失败:', error)
    ElMessage.error('加载预约记录失败: ' + error.message)
  }
}

// 搜索和筛选处理
const handleSearch = () => {
  reservationStore.setSearchKeyword(searchKeyword.value)
}

const handleStatusFilter = () => {
  reservationStore.setFilterStatus(filterStatus.value)
}

const clearFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  reservationStore.clearFilters()
}

// 取消预约
const handleCancel = async (reservation) => {
  try {
    await ElMessageBox.confirm(
        `确定要取消预约吗？\n\n课程: ${reservation.xiangmumingcheng}\n预约时间: ${formatDateTime(reservation.yuyueshijian)}`,
        '取消预约确认',
        {
          confirmButtonText: '确定取消',
          cancelButtonText: '再想想',
          type: 'warning',
          confirmButtonClass: 'el-button--danger'
        }
    )

    cancellingId.value = reservation.id
    console.log(`🚨 开始取消预约，预约ID:`, reservation.id)

    const result = await reservationStore.deleteReservation(reservation.id)

    if (result.success) {
      if (result.localOnly) {
        ElMessage.warning('预约已取消（演示模式）')
      } else {
        ElMessage.success('预约已取消')
      }
      console.log(`✅ 取消预约成功`)
      await reservationStore.fetchReservations()
    } else {
      throw new Error(result.message || '取消预约失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('❌ 取消预约失败:', error)
      ElMessage.error(error.message || '操作失败')
    } else {
      console.log('用户取消了操作')
    }
  } finally {
    cancellingId.value = null
  }
}

// 删除预约记录
const handleDelete = async (reservation) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除这条预约记录吗？此操作不可恢复。\n\n课程: ${reservation.xiangmumingcheng}`,
        '删除记录确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'error',
          confirmButtonClass: 'el-button--danger'
        }
    )

    deletingId.value = reservation.id
    const result = await reservationStore.deleteReservation(reservation.id)

    if (result.success) {
      ElMessage.success(result.message || '删除成功')
      await reservationStore.fetchReservations()
    } else {
      throw new Error(result.message || '删除记录失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('❌ 删除预约记录失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  } finally {
    deletingId.value = null
  }
}

// 重新预约功能
const handleRebook = async (reservation) => {
  try {
    await ElMessageBox.confirm(
        `确定要重新预约 "${reservation.xiangmumingcheng}" 吗？`,
        '重新预约',
        {
          confirmButtonText: '确定预约',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    const result = await reservationStore.rebookReservation(reservation)

    if (result.success) {
      ElMessage.success(result.message || '重新预约成功')
      await reservationStore.fetchReservations()
    } else {
      throw new Error(result.message || '重新预约失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重新预约失败:', error)
      ElMessage.error(error.message || '重新预约失败')
    }
  }
}

// 查看详情
const viewDetails = (reservation) => {
  console.log('查看预约详情:', reservation)
  ElMessage.info(`查看预约详情: ${reservation.xiangmumingcheng}`)
}

// 分页处理
const handleSizeChange = (newSize) => {
  reservationStore.setPagination(1, newSize)
}

const handleCurrentChange = (newPage) => {
  reservationStore.setPagination(newPage, reservationStore.pageSize)
}

// 快速体验功能
const showQuickDemo = async () => {
  try {
    const demoReservation = {
      kechengid: 999,
      xiangmumingcheng: '体验课程 - 自由泳入门',
      leixing: '体验课程',
      jiaolianmingcheng: '体验教练',
      jiaolianid: 999,
      yuyueshijian: new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString(),
      beizhu: '这是快速体验创建的预约'
    }

    const result = await reservationStore.addReservation(demoReservation)
    if (result.success) {
      ElMessage.success('体验预约创建成功！')
      await reservationStore.fetchReservations()
    }
  } catch (error) {
    console.error('体验预约失败:', error)
    ElMessage.error('体验预约失败')
  }
}

// 状态标签类型
const getStatusType = (status) => {
  const statusMap = {
    '待确认': 'warning',
    '已确认': 'success',
    '进行中': 'primary',
    '已完成': 'info',
    '已取消': 'danger'
  }
  return statusMap[status] || 'info'
}

// 卡片样式类
const getReservationCardClass = (reservation) => {
  const classes = []
  if (reservation.zhuangtai === '已取消') {
    classes.push('cancelled-card')
  }
  if (isToday(reservation.yuyueshijian) && reservation.zhuangtai === '已确认') {
    classes.push('today-card')
  }
  return classes
}

// 时间判断函数 - 使用北京时间
const isToday = (dateString) => {
  if (!dateString) return false
  const date = new Date(dateString)
  // 转换为北京时间
  const beijingDate = new Date(date.getTime() + 8 * 60 * 60 * 1000)
  const today = new Date()
  const beijingToday = new Date(today.getTime() + 8 * 60 * 60 * 1000)
  return beijingDate.toDateString() === beijingToday.toDateString()
}

const isUpcoming = (dateString) => {
  if (!dateString) return false
  const reservationDate = new Date(dateString)
  const now = new Date()
  // 转换为北京时间
  const beijingReservation = new Date(reservationDate.getTime() + 8 * 60 * 60 * 1000)
  const beijingNow = new Date(now.getTime() + 8 * 60 * 60 * 1000)
  const oneHourLater = new Date(beijingNow.getTime() + 60 * 60 * 1000)
  return beijingReservation > beijingNow && beijingReservation <= oneHourLater
}

const showTimeStatus = (reservation) => {
  return reservation.zhuangtai === '已确认' &&
      (isToday(reservation.yuyueshijian) || isUpcoming(reservation.yuyueshijian))
}

const getTimeStatusText = (reservation) => {
  if (isUpcoming(reservation.yuyueshijian)) {
    const minutes = Math.round((new Date(reservation.yuyueshijian) - new Date()) / (60 * 1000))
    return `预约即将开始，还有约${minutes}分钟`
  } else if (isToday(reservation.yuyueshijian)) {
    return '今日预约，请准时参加'
  }
  return ''
}

const getTimeStatusType = (reservation) => {
  if (isUpcoming(reservation.yuyueshijian)) {
    return 'warning'
  }
  return 'info'
}

// 🔥 日期时间格式化（完整显示）- 使用北京时间
const formatDateTime = (dateString) => {
  if (!dateString) return '未设置'
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      timeZone: 'Asia/Shanghai'  // 🔥 指定北京时间
    })
  } catch (error) {
    console.error('日期格式化错误:', error)
    return dateString
  }
}

// 🔥 格式化时间（只显示时分）- 使用北京时间
const formatTime = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
      timeZone: 'Asia/Shanghai'  // 🔥 指定北京时间
    })
  } catch (error) {
    return dateString
  }
}

// 监听搜索关键词同步
watch(searchKeyword, (newVal) => {
  if (newVal !== reservationStore.searchKeyword) {
    reservationStore.setSearchKeyword(newVal)
  }
})

watch(() => reservationStore.searchKeyword, (newVal) => {
  if (newVal !== searchKeyword.value) {
    searchKeyword.value = newVal
  }
})

// 监听状态过滤同步
watch(filterStatus, (newVal) => {
  if (newVal !== reservationStore.filterStatus) {
    reservationStore.setFilterStatus(newVal)
  }
})

watch(() => reservationStore.filterStatus, (newVal) => {
  if (newVal !== filterStatus.value) {
    filterStatus.value = newVal
  }
})

// 生命周期
onMounted(() => {
  console.log('🏊 预约页面加载完成，开始加载数据...')

  isDevelopment.value = window.location.hostname === 'localhost' ||
      window.location.hostname === '127.0.0.1'

  console.log('环境判断 - 开发模式:', isDevelopment.value)

  searchKeyword.value = reservationStore.searchKeyword
  filterStatus.value = reservationStore.filterStatus

  loadReservations()
})
</script>

<style scoped>
.reservations-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
}

.search-filter {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.text-right {
  text-align: right;
}

.reservation-count {
  color: #666;
  font-size: 14px;
  line-height: 32px;
}

.reservation-stats {
  margin-bottom: 30px;
}

.today-reminder {
  margin-bottom: 20px;
}

.today-list {
  margin-top: 8px;
}

.today-item {
  display: inline-block;
  background: #e6f7ff;
  padding: 4px 8px;
  margin: 2px 4px 2px 0;
  border-radius: 4px;
  font-size: 12px;
}

.reservations-list {
  margin-bottom: 40px;
}

.reservation-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.reservation-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.reservation-card.cancelled-card {
  opacity: 0.7;
  background: #fafafa;
}

.reservation-card.today-card {
  border-left: 4px solid #e6a23c;
}

.reservation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.course-name {
  font-size: 16px;
  font-weight: bold;
}

.reservation-number {
  font-size: 12px;
  color: #999;
}

.reservation-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.reservation-info {
  flex: 1;
}

.reservation-info p {
  margin: 5px 0;
  color: #666;
}

.time-status {
  margin-top: 12px;
}

.reservation-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 120px;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.loading-state {
  padding: 20px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .reservation-content {
    flex-direction: column;
    gap: 15px;
  }

  .reservation-actions {
    flex-direction: row;
    width: 100%;
    justify-content: flex-start;
  }

  .reservation-actions .el-button {
    flex: 1;
  }

  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>