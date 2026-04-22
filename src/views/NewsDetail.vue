<template>
  <div class="news-detail-page">
    <div v-if="loading" class="loading-state">
      <el-skeleton :rows="8" animated />
    </div>

    <div v-else-if="news" class="news-detail">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/news' }">新闻资讯</el-breadcrumb-item>
        <el-breadcrumb-item>{{ news.title }}</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 新闻标题 -->
      <div class="news-header">
        <h1 class="news-title">{{ news.title }}</h1>
        <div class="news-meta">
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            <span>{{ formatDateTime(news.addtime) }}</span>
          </div>
          <div class="meta-item">
            <el-icon><View /></el-icon>
            <span>{{ news.liulancount || 0 }} 次浏览</span>
          </div>
        </div>
      </div>

      <!-- 新闻图片 -->
      <div v-if="news.picture" class="news-cover">
        <img :src="news.picture" :alt="news.title">
      </div>

      <!-- 新闻内容 -->
      <el-card class="news-content-card">
        <div class="news-content" v-html="news.content"></div>
      </el-card>

      <!-- 返回按钮 -->
      <div class="news-actions">
        <el-button @click="$router.push('/news')">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>
    </div>

    <div v-else class="empty-state">
      <el-empty description="新闻不存在">
        <el-button type="primary" @click="$router.push('/news')">返回新闻列表</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useNewsStore } from '@/store/news'
import { ElMessage } from 'element-plus'
import { Calendar, View, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const newsStore = useNewsStore()

const news = ref(null)
const loading = ref(true)

// 获取新闻详情
const fetchNewsDetail = async () => {
  const newsId = route.params.id
  console.log('📰 获取新闻详情，ID:', newsId)
  
  if (!newsId) {
    router.push('/news')
    return
  }
  
  loading.value = true
  try {
    // 直接从 API 获取新闻详情
    const data = await newsStore.fetchNewsById(Number(newsId))
    console.log('📰 新闻详情数据:', data)
    news.value = data
  } catch (error) {
    console.error('❌ 获取新闻详情失败:', error)
    ElMessage.error('获取新闻详情失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchNewsDetail()
})
</script>

<style scoped>
.news-detail-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.loading-state {
  padding: 40px;
}

.breadcrumb {
  margin-bottom: 30px;
}

.news-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.news-title {
  font-size: 32px;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.3;
}

.news-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #999;
  font-size: 14px;
}

.news-cover {
  margin-bottom: 30px;
  text-align: center;
}

.news-cover img {
  max-width: 100%;
  max-height: 400px;
  border-radius: 8px;
  object-fit: cover;
}

.news-content-card {
  margin-bottom: 30px;
}

.news-content {
  line-height: 1.8;
  color: #555;
  font-size: 16px;
}

.news-content img {
  max-width: 100%;
  height: auto;
  margin: 20px 0;
  border-radius: 8px;
}

.news-content p {
  margin-bottom: 16px;
}

.news-content h2,
.news-content h3 {
  margin: 24px 0 16px;
  color: #333;
}

.news-actions {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
}

.empty-state {
  padding: 80px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .news-detail-page {
    padding: 15px;
  }

  .news-title {
    font-size: 24px;
  }

  .news-meta {
    gap: 15px;
  }

  .news-content {
    font-size: 15px;
  }
}
</style>