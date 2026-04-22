<template>
  <div class="reservation-management">
    <div class="page-header">
      <h2>预约管理</h2>
      <p>查看和管理所有用户的课程预约</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="4">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总预约</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card pending" shadow="hover">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待确认</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card confirmed" shadow="hover">
          <div class="stat-value">{{ stats.confirmed }}</div>
          <div class="stat-label">已确认</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card progress" shadow="hover">
          <div class="stat-value">{{ stats.inProgress }}</div>
          <div class="stat-label">进行中</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card completed" shadow="hover">
          <div class="stat-value">{{ stats.completed }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card cancelled" shadow="hover">
          <div class="stat-value">{{ stats.cancelled }}</div>
          <div class="stat-label">已取消</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程、教练或用户..."
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
          <el-select
            v-model="filterStatus"
            placeholder="预约状态"
            clearable
            @change="handleFilter"
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
          <!-- 🔥 改为单日选择 -->
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            clearable
            @change="handleDateFilter"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="6" class="text-right">
          <el-button @click="resetFilters">
            <el-icon><RefreshRight /></el-icon>
            重置筛选
          </el-button>
          <el-button type="success" @click="exportData">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 预约列表 -->
    <el-card class="table-card">
      <el-table
        :data="paginatedReservations"
        v-loading="loading"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="yuyuebianhao" label="预约编号" width="140" />
        <el-table-column prop="xiangmumingcheng" label="课程名称" min-width="180" />
        <el-table-column prop="leixing" label="课程类型" width="100" />
        <el-table-column prop="jiaolianmingcheng" label="教练" width="100" />
        <el-table-column prop="yonghuzhanghao" label="用户账号" width="120" />
        <el-table-column prop="yonghuxingming" label="用户姓名" width="100" />
        <el-table-column label="预约时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.yuyueshijian) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.zhuangtai)">
              {{ row.zhuangtai }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.beizhu || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.zhuangtai === '待确认'"
              type="success"
              size="small"
              @click="confirmReservation(row)"
              :loading="confirmingId === row.id"
            >
              确认
            </el-button>
            <el-button
              v-if="row.zhuangtai === '已确认'"
              type="primary"
              size="small"
              @click="startReservation(row)"
              :loading="startingId === row.id"
            >
              开始课程
            </el-button>
            <el-button
              v-if="row.zhuangtai === '进行中'"
              type="info"
              size="small"
              @click="completeReservation(row)"
              :loading="completingId === row.id"
            >
              完成
            </el-button>
            <el-button
              v-if="['待确认', '已确认', '进行中'].includes(row.zhuangtai)"
              type="danger"
              size="small"
              @click="cancelReservation(row)"
              :loading="cancellingId === row.id"
            >
              取消
            </el-button>
            <el-button
              v-if="['已取消', '已完成'].includes(row.zhuangtai)"
              type="danger"
              size="small"
              plain
              @click="deleteReservation(row)"
              :loading="deletingId === row.id"
            >
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
          :total="filteredReservations.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 预约详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="600px">
      <el-descriptions :column="2" border v-if="currentReservation">
        <el-descriptions-item label="预约编号">{{ currentReservation.yuyuebianhao }}</el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag :type="getStatusType(currentReservation.zhuangtai)">
            {{ currentReservation.zhuangtai }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ currentReservation.xiangmumingcheng }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ currentReservation.leixing }}</el-descriptions-item>
        <el-descriptions-item label="教练">{{ currentReservation.jiaolianmingcheng }}</el-descriptions-item>
        <el-descriptions-item label="用户账号">{{ currentReservation.yonghuzhanghao }}</el-descriptions-item>
        <el-descriptions-item label="用户姓名">{{ currentReservation.yonghuxingming }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentReservation.lianxidianhua || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDateTime(currentReservation.yuyueshijian) }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(currentReservation.addtime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentReservation.beizhu || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useReservationStore } from '@/store/reservation'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, RefreshRight, Download } from '@element-plus/icons-vue'

const reservationStore = useReservationStore()

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('')
const selectedDate = ref('')  // 🔥 改为单日选择
const currentPage = ref(1)
const pageSize = ref(20)

// 操作loading状态
const confirmingId = ref(null)
const startingId = ref(null)
const completingId = ref(null)
const cancellingId = ref(null)
const deletingId = ref(null)

const detailDialogVisible = ref(false)
const currentReservation = ref(null)

// 状态选项
const statusOptions = [
  { label: '全部', value: '' },
  { label: '待确认', value: '待确认' },
  { label: '已确认', value: '已确认' },
  { label: '进行中', value: '进行中' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' }
]

// 计算属性
const allReservations = computed(() => reservationStore.reservations || [])

// 统计
const stats = computed(() => {
  const reservations = allReservations.value
  return {
    total: reservations.length,
    pending: reservations.filter(r => r.zhuangtai === '待确认').length,
    confirmed: reservations.filter(r => r.zhuangtai === '已确认').length,
    inProgress: reservations.filter(r => r.zhuangtai === '进行中').length,
    completed: reservations.filter(r => r.zhuangtai === '已完成').length,
    cancelled: reservations.filter(r => r.zhuangtai === '已取消').length
  }
})

// 过滤后的预约
const filteredReservations = computed(() => {
  let list = [...allReservations.value]

  // 状态过滤
  if (filterStatus.value) {
    list = list.filter(r => r.zhuangtai === filterStatus.value)
  }

  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(r =>
      r.xiangmumingcheng?.toLowerCase().includes(keyword) ||
      r.jiaolianmingcheng?.toLowerCase().includes(keyword) ||
      r.yonghuzhanghao?.toLowerCase().includes(keyword) ||
      r.yonghuxingming?.toLowerCase().includes(keyword)
    )
  }

  // 🔥 单日日期过滤
  if (selectedDate.value) {
    const targetDate = new Date(selectedDate.value)
    const targetDateStr = targetDate.toDateString()
    
    list = list.filter(r => {
      const reservationDate = new Date(r.yuyueshijian)
      return reservationDate.toDateString() === targetDateStr
    })
  }

  // 按时间倒序
  return list.sort((a, b) => new Date(b.addtime) - new Date(a.addtime))
})

// 分页后的预约
const paginatedReservations = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredReservations.value.slice(start, end)
})

// 方法
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const handleDateFilter = () => {
  currentPage.value = 1
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

const resetFilters = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  selectedDate.value = ''
  currentPage.value = 1
}

// 确认预约
const confirmReservation = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认接受用户 "${row.yonghuxingming}" 的预约吗？`,
      '确认预约',
      { type: 'warning' }
    )
    
    confirmingId.value = row.id
    const result = await reservationStore.confirmReservation(row.id)
    
    if (result.success) {
      ElMessage.success('预约已确认')
      await reservationStore.fetchAllReservations()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '确认失败')
    }
  } finally {
    confirmingId.value = null
  }
}

// 开始课程
const startReservation = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认开始课程 "${row.xiangmumingcheng}" 吗？`,
      '开始课程',
      { type: 'info' }
    )
    
    startingId.value = row.id
    const result = await reservationStore.updateReservationStatus(row.id, { zhuangtai: '进行中' })
    
    if (result.success) {
      ElMessage.success('课程已开始')
      await reservationStore.fetchAllReservations()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    startingId.value = null
  }
}

// 完成课程
const completeReservation = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认完成课程 "${row.xiangmumingcheng}" 吗？`,
      '完成课程',
      { type: 'info' }
    )
    
    completingId.value = row.id
    const result = await reservationStore.updateReservationStatus(row.id, { zhuangtai: '已完成' })
    
    if (result.success) {
      ElMessage.success('课程已完成')
      await reservationStore.fetchAllReservations()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    completingId.value = null
  }
}

// 取消预约
const cancelReservation = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消用户 "${row.yonghuxingming}" 的预约吗？`,
      '取消预约',
      { type: 'warning' }
    )
    
    cancellingId.value = row.id
    const result = await reservationStore.cancelReservation(row.id)
    
    if (result.success) {
      ElMessage.success('预约已取消')
      await reservationStore.fetchAllReservations()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    cancellingId.value = null
  }
}

// 删除预约
const deleteReservation = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除这条预约记录吗？此操作不可恢复。`,
      '删除记录',
      { type: 'error' }
    )
    
    deletingId.value = row.id
    const result = await reservationStore.deleteReservation(row.id)
    
    if (result.success) {
      ElMessage.success('预约已删除')
      await reservationStore.fetchAllReservations()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    deletingId.value = null
  }
}

// 查看详情
const viewDetail = (row) => {
  currentReservation.value = row
  detailDialogVisible.value = true
}

// 导出数据
const exportData = () => {
  if (filteredReservations.value.length === 0) {
    ElMessage.warning('没有数据可导出')
    return
  }
  
  const data = filteredReservations.value.map(r => ({
    '预约编号': r.yuyuebianhao,
    '课程名称': r.xiangmumingcheng,
    '课程类型': r.leixing,
    '教练': r.jiaolianmingcheng,
    '用户账号': r.yonghuzhanghao,
    '用户姓名': r.yonghuxingming,
    '预约时间': formatDateTime(r.yuyueshijian),
    '状态': r.zhuangtai,
    '备注': r.beizhu || ''
  }))
  
  const csv = convertToCSV(data)
  const blob = new Blob(["\uFEFF" + csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.href = url
  link.setAttribute('download', `预约记录_${new Date().toLocaleDateString()}.csv`)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const convertToCSV = (data) => {
  if (data.length === 0) return ''
  const headers = Object.keys(data[0])
  const rows = data.map(row => headers.map(header => `"${row[header] || ''}"`).join(','))
  return [headers.join(','), ...rows].join('\n')
}

// 获取状态标签类型
const getStatusType = (status) => {
  const map = {
    '待确认': 'warning',
    '已确认': 'success',
    '进行中': 'primary',
    '已完成': 'info',
    '已取消': 'danger'
  }
  return map[status] || 'info'
}

// 格式化日期时间 - 使用北京时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      timeZone: 'Asia/Shanghai'
    })
  } catch (error) {
    return dateString
  }
}
// 生命周期
onMounted(async () => {
  loading.value = true
  // 🔥 修改：调用获取所有预约的方法
  await reservationStore.fetchAllReservations()  // 改这里
  console.log('📋 管理员预约管理加载完成，预约数量:', allReservations.value.length)
  loading.value = false
})


</script>

<style scoped>
.reservation-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 15px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.stat-card.pending .stat-value { color: #E6A23C; }
.stat-card.confirmed .stat-value { color: #67C23A; }
.stat-card.progress .stat-value { color: #409EFF; }
.stat-card.completed .stat-value { color: #909399; }
.stat-card.cancelled .stat-value { color: #F56C6C; }

.filter-card {
  margin-bottom: 20px;
}

.filter-card .el-row {
  width: 100%;
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
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .stats-cards .el-col {
    margin-bottom: 15px;
  }
  
  .text-right {
    text-align: left;
    margin-top: 15px;
  }
  
  .filter-card .el-row {
    flex-direction: column;
    gap: 10px;
  }
  
  .filter-card .el-col {
    width: 100%;
  }
}
</style>