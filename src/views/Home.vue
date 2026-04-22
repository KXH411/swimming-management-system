<template>
  <div class="home-container">
    <!-- 轮播图 -->
    <el-carousel height="400px" class="banner">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-placeholder">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 功能卡片 -->
    <div class="features">
      <h2 class="section-title">系统功能</h2>
      <el-row :gutter="20">
        <el-col :span="6" v-for="feature in features" :key="feature.id">
          <el-card class="feature-card" shadow="hover" @click="handleFeatureClick(feature.path)">
            <div class="feature-icon">
              <el-icon :size="40"><component :is="feature.icon" /></el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最新课程 -->
    <div class="latest-courses">
      <h2 class="section-title">热门游泳课程</h2>
      <el-row :gutter="20">
        <el-col :span="8" v-for="course in courses" :key="course.id">
          <el-card class="course-card" shadow="hover">
            <div class="course-image-placeholder">
              <img 
                v-if="course.fengmian" 
                :src="course.fengmian" 
                :alt="course.xiangmumingcheng"
                class="course-image"
                @error="handleImageError"
              >
              <el-icon v-else :size="60"><Picture /></el-icon>
            </div>
            <div class="course-info">
              <h3>{{ course.xiangmumingcheng }}</h3>
              <p class="course-type">{{ course.leixing }}</p>
              <p class="course-coach">教练: {{ course.jiaolianmingcheng }}</p>
              <div class="course-actions">
                <el-button type="primary" size="small" @click="viewCourseDetail(course.id)">查看详情</el-button>
                <el-button size="small" @click="reserveCourse(course)">立即预约</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="more-link">
        <el-button type="primary" link @click="$router.push('/courses')">查看更多课程 ></el-button>
      </div>
    </div>

    <!-- 最新新闻 -->
    <div class="latest-news">
      <h2 class="section-title">最新资讯</h2>
      <el-timeline>
        <el-timeline-item
            v-for="newsItem in news"
            :key="newsItem.id"
            :timestamp="formatDate(newsItem.addtime)"
            placement="top"
        >
          <el-card class="news-card">
            <h4>{{ newsItem.title }}</h4>
            <p>{{ newsItem.introduction }}</p>
            <el-button text @click="viewNewsDetail(newsItem.id)">阅读更多</el-button>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <div class="more-link">
        <el-button type="primary" link @click="$router.push('/news')">查看更多资讯 ></el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCourseStore } from '@/store/course'
import { useNewsStore } from '@/store/news'
import { useUserStore } from '@/store/user'
import { useFavoriteStore } from '@/store/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ShoppingCart,
  User,
  Calendar,
  Document,
  Picture
} from '@element-plus/icons-vue'

const router = useRouter()
const courseStore = useCourseStore()
const newsStore = useNewsStore()
const userStore = useUserStore()
const favoriteStore = useFavoriteStore()

// 轮播图数据
const banners = ref([
  {
    id: 1,
    title: '夏季游泳课程优惠',
    description: '报名即享8折优惠'
  },
  {
    id: 2,
    title: '专业教练团队',
    description: '国家级教练亲自指导'
  },
  {
    id: 3,
    title: '儿童游泳培训班',
    description: '专为儿童设计的游泳课程'
  }
])

// 功能卡片
const features = ref([
  {
    id: 1,
    title: '课程预约',
    description: '在线预约游泳课程',
    icon: ShoppingCart,
    path: '/courses'
  },
  {
    id: 2,
    title: '教练信息',
    description: '查看专业教练资料',
    icon: User,
    path: '/coaches'
  },
  {
    id: 3,
    title: '我的预约',
    description: '管理您的课程预约',
    icon: Calendar,
    path: '/reservations'
  },
  {
    id: 4,
    title: '新闻资讯',
    description: '获取最新游泳资讯',
    icon: Document,
    path: '/news'
  }
])

// 课程数据（从store获取）
const courses = ref([])
// 新闻数据（从store获取）
const news = ref([])

// 方法
const handleFeatureClick = (path) => {
  const needLogin = ['/reservations', '/profile', '/favorites'].includes(path)
  const token = localStorage.getItem('token')
  
  if (needLogin && !token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(path)
}

// 查看课程详情
const viewCourseDetail = (courseId) => {
  router.push(`/course-detail/${courseId}`)
}

// 预约课程
const reserveCourse = async (course) => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要预约《${course.xiangmumingcheng}》课程吗？`,
      '确认预约',
      {
        confirmButtonText: '确定预约',
        cancelButtonText: '再考虑下',
        type: 'warning'
      }
    )

    const reservationData = {
      kechengid: course.id,
      xiangmumingcheng: course.xiangmumingcheng,
      leixing: course.leixing,
      jiaolianmingcheng: course.jiaolianmingcheng,
      jiaolianid: course.jiaolianid || 1,
      yuyueshijian: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(),
      beizhu: `预约课程：${course.xiangmumingcheng}`
    }

    const result = await userStore.createReservation(reservationData)

    if (result.success) {
      ElMessage.success(`成功预约课程: ${course.xiangmumingcheng}`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预约失败:', error)
      ElMessage.error(error.message || '预约失败')
    }
  }
}

// 查看新闻详情
const viewNewsDetail = (newsId) => {
  router.push(`/news-detail/${newsId}`)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 图片加载失败处理
const handleImageError = (event) => {
  event.target.src = ''
  event.target.parentElement.innerHTML = '<el-icon :size="60"><Picture /></el-icon>'
}

// 加载数据 - 添加空值检查
const loadData = async () => {
  try {
    // 加载课程列表
    await courseStore.fetchCourses()
    // 🔥 添加空值检查，确保 courses 是数组
    const courseList = courseStore.courses || []
    courses.value = courseList.slice(0, 3)
    console.log('✅ 首页课程加载完成:', courses.value.length)
    
    // 加载新闻列表
    await newsStore.fetchNewsList()
    // 🔥 添加空值检查，确保 news 是数组
    const newsList = newsStore.news || []
    news.value = newsList.slice(0, 3)
    console.log('✅ 首页新闻加载完成:', news.value.length)
    
    // 如果已登录，加载收藏列表
    const token = localStorage.getItem('token')
    if (token) {
      await favoriteStore.fetchMyFavorites()
    }
  } catch (error) {
    console.error('❌ 首页数据加载失败:', error)
    // 使用模拟数据作为备选
    courses.value = [
      {
        id: 1,
        xiangmumingcheng: '成人蛙泳入门',
        leixing: '成人游泳',
        jiaolianmingcheng: '张教练',
        jiage: 200,
        shichang: 60
      },
      {
        id: 2,
        xiangmumingcheng: '儿童自由泳',
        leixing: '儿童游泳',
        jiaolianmingcheng: '李教练',
        jiage: 150,
        shichang: 45
      },
      {
        id: 3,
        xiangmumingcheng: '健身游泳训练',
        leixing: '健身游泳',
        jiaolianmingcheng: '王教练',
        jiage: 300,
        shichang: 90
      }
    ]
    news.value = [
      {
        id: 1,
        title: '游泳馆夏季开放通知',
        introduction: '夏季游泳馆开放时间调整通知',
        addtime: new Date().toISOString()
      },
      {
        id: 2,
        title: '游泳安全知识普及',
        introduction: '学习游泳安全知识，享受健康运动',
        addtime: new Date().toISOString()
      },
      {
        id: 3,
        title: '新课程上线通知',
        introduction: '新增竞技游泳课程，欢迎报名',
        addtime: new Date().toISOString()
      }
    ]
  }
}

// 生命周期
onMounted(() => {
  loadData()
  console.log('🏠 首页加载完成')
})
</script>

<style scoped>
.home-container {
  padding: 20px 0;
}

.banner {
  margin-bottom: 40px;
  border-radius: 8px;
  overflow: hidden;
}

.banner-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.banner-placeholder h3 {
  font-size: 32px;
  margin-bottom: 16px;
}

.banner-placeholder p {
  font-size: 18px;
}

.section-title {
  text-align: center;
  margin: 40px 0 20px;
  color: #333;
  font-size: 28px;
}

.features {
  margin-bottom: 40px;
}

.feature-card {
  text-align: center;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.3s;
  height: 180px;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  margin-bottom: 15px;
  color: #409EFF;
}

.latest-courses {
  margin-bottom: 40px;
}

.course-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-4px);
}

.course-image-placeholder {
  height: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  color: #c0c4cc;
  overflow: hidden;
}

.course-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 15px;
}

.course-info h3 {
  margin-bottom: 10px;
  color: #333;
}

.course-type {
  color: #409EFF;
  font-weight: bold;
  margin-bottom: 5px;
}

.course-coach {
  color: #666;
  margin-bottom: 15px;
}

.course-actions {
  display: flex;
  justify-content: space-between;
}

.latest-news {
  margin-top: 40px;
}

.news-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.news-card:hover {
  transform: translateX(4px);
}

.more-link {
  text-align: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-placeholder h3 {
    font-size: 24px;
  }
  
  .banner-placeholder p {
    font-size: 14px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .course-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .course-actions .el-button {
    width: 100%;
  }
}
</style>