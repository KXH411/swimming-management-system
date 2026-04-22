<template>
  <div class="course-detail-page">
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="course" class="course-detail">
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/courses' }">课程列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ course.xiangmumingcheng }}</el-breadcrumb-item>
      </el-breadcrumb>

      <el-row :gutter="30" class="course-main">
        <el-col :xs="24" :md="10">
          <div class="course-image">
            <img 
              :src="course.fengmian || '/images/course-default.jpg'" 
              :alt="course.xiangmumingcheng"
              @error="handleImageError"
            >
          </div>
        </el-col>
        
        <el-col :xs="24" :md="14">
          <div class="course-info">
            <h1 class="course-title">{{ course.xiangmumingcheng }}</h1>
            <div class="course-tags">
              <el-tag type="primary">{{ course.leixing }}</el-tag>
              <el-tag v-if="course.storeupnum > 0">
                <el-icon><Star /></el-icon>
                {{ course.storeupnum }}人收藏
              </el-tag>
            </div>
            
            <div class="course-meta">
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>教练：{{ course.jiaolianmingcheng }}</span>
              </div>
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>课程时长：{{ course.shichang }}分钟</span>
              </div>
              <div class="meta-item">
                <el-icon><Calendar /></el-icon>
                <span>发布时间：{{ formatDate(course.addtime) }}</span>
              </div>
            </div>
            
            <div class="course-price">
              <span class="price">¥{{ course.jiage }}</span>
            </div>
            
            <div class="course-actions">
              <el-button type="primary" size="large" @click="handleReserve" :loading="reserving">
                {{ reserving ? '预约中...' : '立即预约' }}
              </el-button>
              <el-button size="large" @click="toggleFavorite" :type="isFavorited ? 'danger' : 'default'">
                <el-icon><Star v-if="!isFavorited" /><StarFilled v-else /></el-icon>
                {{ isFavorited ? '已收藏' : '收藏课程' }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-card class="course-description-card">
        <template #header>
          <h3>课程介绍</h3>
        </template>
        <div class="course-description">
          <p>{{ course.kechengjianjie || '暂无详细介绍' }}</p>
        </div>
      </el-card>

      <el-card class="related-courses-card" v-if="relatedCourses.length > 0">
        <template #header>
          <h3>相关推荐</h3>
        </template>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="item in relatedCourses" :key="item.id">
            <el-card class="related-course" shadow="hover" @click="goToCourse(item.id)">
              <div class="related-course-image">
                <img :src="item.fengmian || '/images/course-default.jpg'" :alt="item.xiangmumingcheng">
              </div>
              <div class="related-course-info">
                <h4>{{ item.xiangmumingcheng }}</h4>
                <p>教练：{{ item.jiaolianmingcheng }}</p>
                <div class="price">¥{{ item.jiage }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <div v-else class="empty-state">
      <el-empty description="课程不存在">
        <el-button type="primary" @click="$router.push('/courses')">返回课程列表</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCourseStore } from '@/store/course'
import { useUserStore } from '@/store/user'
import { useFavoriteStore } from '@/store/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Clock, Calendar, Star, StarFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const courseStore = useCourseStore()
const userStore = useUserStore()
const favoriteStore = useFavoriteStore()

const course = ref(null)
const relatedCourses = ref([])
const loading = ref(true)
const reserving = ref(false)

const isFavorited = computed(() => favoriteStore.isFavorited(course.value?.id))

const fetchCourseDetail = async () => {
  const courseId = route.params.id
  if (!courseId) {
    router.push('/courses')
    return
  }
  
  loading.value = true
  try {
    const data = await courseStore.fetchCourseById(Number(courseId))
    course.value = data
    await fetchRelatedCourses()
  } catch (error) {
    console.error('获取课程详情失败:', error)
    ElMessage.error('获取课程详情失败')
  } finally {
    loading.value = false
  }
}

const fetchRelatedCourses = async () => {
  try {
    const allCourses = courseStore.courses
    relatedCourses.value = allCourses
      .filter(c => c.id !== course.value?.id && c.leixing === course.value?.leixing)
      .slice(0, 3)
  } catch (error) {
    console.error('获取相关课程失败:', error)
  }
}

const handleReserve = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要预约《${course.value.xiangmumingcheng}》课程吗？`,
      '确认预约',
      {
        confirmButtonText: '确定预约',
        cancelButtonText: '再考虑下',
        type: 'warning'
      }
    )

    reserving.value = true

    const reservationData = {
      kechengid: course.value.id,
      xiangmumingcheng: course.value.xiangmumingcheng,
      leixing: course.value.leixing,
      jiaolianmingcheng: course.value.jiaolianmingcheng,
      jiaolianid: course.value.jiaolianid || 1,
      yuyueshijian: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(),
      beizhu: `预约课程：${course.value.xiangmumingcheng}`
    }

    const result = await userStore.createReservation(reservationData)

    if (result.success) {
      ElMessage.success('预约成功！请到"我的预约"查看')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预约失败:', error)
      ElMessage.error(error.message || '预约失败')
    }
  } finally {
    reserving.value = false
  }
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    const result = await favoriteStore.toggleFavorite(course.value.id, course.value)
    if (result.success) {
      ElMessage.success(result.message)
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  }
}

const goToCourse = (courseId) => {
  router.push(`/course-detail/${courseId}`)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const handleImageError = (event) => {
  event.target.src = '/images/course-default.jpg'
}

onMounted(() => {
  fetchCourseDetail()
})
</script>

<style scoped>
.course-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.loading-state { padding: 40px; }
.breadcrumb { margin-bottom: 30px; }
.course-main {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.course-image img {
  width: 100%;
  border-radius: 8px;
  object-fit: cover;
}
.course-info { padding: 0 20px; }
.course-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 15px;
}
.course-tags {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.course-meta { margin-bottom: 20px; }
.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #666;
}
.course-price {
  margin-bottom: 30px;
  padding: 15px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}
.price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
}
.course-actions {
  display: flex;
  gap: 15px;
}
.course-actions .el-button { flex: 1; }
.course-description-card,
.related-courses-card {
  margin-bottom: 30px;
}
.course-description {
  line-height: 1.8;
  color: #666;
  font-size: 16px;
}
.related-course {
  cursor: pointer;
  transition: transform 0.3s;
}
.related-course:hover { transform: translateY(-4px); }
.related-course-image {
  height: 120px;
  overflow: hidden;
  border-radius: 8px;
}
.related-course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.related-course-info {
  padding: 10px;
  text-align: center;
}
.related-course-info h4 {
  margin: 0 0 5px;
  font-size: 14px;
  color: #333;
}
.related-course-info p {
  margin: 5px 0;
  font-size: 12px;
  color: #666;
}
.related-course-info .price {
  font-size: 16px;
  color: #f56c6c;
}
.empty-state {
  padding: 80px 0;
  text-align: center;
}
@media (max-width: 768px) {
  .course-main { padding: 20px; }
  .course-info { padding: 20px 0 0; }
  .course-title { font-size: 22px; }
  .price { font-size: 24px; }
  .course-actions { flex-direction: column; }
}
</style>