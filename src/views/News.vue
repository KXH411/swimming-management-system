<template>
  <div class="news-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>新闻资讯</h1>
      <p>了解最新游泳馆动态、活动信息和游泳知识</p>
    </div>

    <el-row :gutter="24">
      <!-- 主要内容区域 -->
      <el-col :xs="24" :lg="18">
        <!-- 搜索区域 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索新闻标题或内容..."
              clearable
              size="large"
              @clear="handleSearch"
              @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch" :loading="newsStore.loading">
                搜索
              </el-button>
            </template>
          </el-input>
        </div>

        <!-- 新闻列表 -->
        <div class="news-list">
          <div
              v-for="news in paginatedNews"
              :key="news.id"
              class="news-item"
              @click="viewNewsDetail(news.id)"
          >
            <el-card shadow="hover" class="news-card">
              <el-row :gutter="16">
                <!-- 新闻图片 -->
                <el-col :xs="24" :sm="8" :md="6">
                  <div class="news-image">
                    <img
                        :src="news.picture || '/images/news-default.jpg'"
                        :alt="news.title"
                        @error="handleImageError"
                    />
                  </div>
                </el-col>

                <!-- 新闻内容 -->
                <el-col :xs="24" :sm="16" :md="18">
                  <div class="news-content">
                    <h3 class="news-title">{{ news.title }}</h3>
                    <p class="news-intro">{{ news.introduction }}</p>

                    <div class="news-meta">
                      <div class="meta-item">
                        <el-icon><Calendar /></el-icon>
                        <span>{{ formatDate(news.addtime) }}</span>
                      </div>
                      <div class="meta-item">
                        <el-icon><View /></el-icon>
                        <span>{{ news.liulancount || 0 }} 浏览</span>
                      </div>
                    </div>

                    <div class="news-actions">
                      <el-button type="primary" text @click.stop="viewNewsDetail(news.id)">
                        阅读全文
                      </el-button>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredNews.length === 0 && !newsStore.loading" class="empty-state">
            <el-empty description="暂无相关新闻">
              <el-button type="primary" @click="resetSearch">重置搜索</el-button>
            </el-empty>
          </div>

          <!-- 加载状态 -->
          <div v-if="newsStore.loading" class="loading-state">
            <el-skeleton :rows="4" animated />
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[5, 10, 15, 20]"
              :total="filteredNews.length"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>
      </el-col>

      <!-- 侧边栏 -->
      <el-col :xs="24" :lg="6">
        <div class="sidebar">
          <!-- 热门新闻 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="sidebar-header">
                <el-icon><Sunny /></el-icon>
                <span>热门新闻</span>
              </div>
            </template>
            <div class="hot-news-list">
              <div
                  v-for="(news, index) in hotNews"
                  :key="news.id"
                  class="hot-news-item"
                  @click="viewNewsDetail(news.id)"
              >
                <div class="hot-news-rank" :class="`rank-${index + 1}`">
                  {{ index + 1 }}
                </div>
                <div class="hot-news-content">
                  <div class="hot-news-title">{{ news.title }}</div>
                  <div class="hot-news-views">{{ news.liulancount || 0 }} 浏览</div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 最新动态 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="sidebar-header">
                <el-icon><Clock /></el-icon>
                <span>最新动态</span>
              </div>
            </template>
            <div class="latest-news-list">
              <div
                  v-for="news in latestNews"
                  :key="news.id"
                  class="latest-news-item"
                  @click="viewNewsDetail(news.id)"
              >
                <div class="latest-news-title">{{ news.title }}</div>
                <div class="latest-news-time">{{ formatTime(news.addtime) }}</div>
              </div>
            </div>
          </el-card>

          <!-- 分类标签 -->
          <el-card class="sidebar-card" shadow="never">
            <template #header>
              <div class="sidebar-header">
                <el-icon><CollectionTag /></el-icon>
                <span>热门标签</span>
              </div>
            </template>
            <div class="tags-list">
              <el-tag
                  v-for="tag in popularTags"
                  :key="tag"
                  class="news-tag"
                  effect="plain"
                  @click="searchByTag(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useNewsStore } from '@/store/news'
import { ElMessage } from 'element-plus'
import {
  Search,
  Calendar,
  View,
  Sunny,
  Clock,
  CollectionTag
} from '@element-plus/icons-vue'

const router = useRouter()
const newsStore = useNewsStore()

// 响应式数据
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const filteredNews = computed(() => newsStore.filteredNews)
const paginatedNews = computed(() => newsStore.paginatedNews)
const totalPages = computed(() => newsStore.totalPages)
const hotNews = computed(() => newsStore.hotNews)
const latestNews = computed(() => newsStore.latestNews)

const popularTags = computed(() => [
  '游泳比赛', '课程通知', '安全知识', '会员优惠',
  '教练介绍', '设施升级', '健康养生', '儿童游泳'
])

// 方法
const handleSearch = () => {
  currentPage.value = 1
  if (searchKeyword.value.trim()) {
    newsStore.searchNews(searchKeyword.value.trim())
  } else {
    newsStore.fetchNewsList()
  }
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  newsStore.setPagination(currentPage.value, pageSize.value)
}

// 查看新闻详情
const viewNewsDetail = (newsId) => {
  router.push(`/news-detail/${newsId}`)
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  newsStore.setPagination(currentPage.value, pageSize.value)
}


const resetSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  newsStore.fetchNewsList()
}

const searchByTag = (tag) => {
  searchKeyword.value = tag
  handleSearch()
}

const handleImageError = (event) => {
  event.target.src = '/images/news-default.jpg'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const formatTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date

  if (diff < 60 * 60 * 1000) {
    return '刚刚'
  } else if (diff < 24 * 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 60 * 1000))}小时前`
  } else {
    return formatDate(dateString)
  }
}

// 监听筛选条件变化
watch(searchKeyword, () => {
  currentPage.value = 1
})

// 生命周期
onMounted(() => {
  newsStore.setPagination(currentPage.value, pageSize.value)
  newsStore.fetchNewsList()
})
</script>

<style scoped>
.news-page {
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

.search-section {
  margin-bottom: 30px;
}

.news-list {
  margin-bottom: 40px;
}

.news-item {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;
}

.news-item:hover {
  transform: translateY(-2px);
}

.news-card {
  border: none;
  border-radius: 8px;
}

.news-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.news-image {
  height: 140px;
  border-radius: 6px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.news-item:hover .news-image img {
  transform: scale(1.05);
}

.news-content {
  padding: 8px 0;
  height: 140px;
  display: flex;
  flex-direction: column;
}

.news-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-intro {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.news-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 13px;
}

.news-actions {
  text-align: right;
}

.empty-state,
.loading-state {
  padding: 60px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

/* 侧边栏样式 */
.sidebar {
  position: sticky;
  top: 20px;
}

.sidebar-card {
  margin-bottom: 20px;
  border: 1px solid #f0f0f0;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #333;
}

/* 热门新闻列表 */
.hot-news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-news-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.hot-news-item:hover {
  background-color: #f5f7fa;
}

.hot-news-rank {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: white;
  flex-shrink: 0;
}

.rank-1 {
  background: #f56c6c;
}

.rank-2 {
  background: #e6a23c;
}

.rank-3 {
  background: #67c23a;
}

.rank-4,
.rank-5 {
  background: #909399;
}

.hot-news-content {
  flex: 1;
  min-width: 0;
}

.hot-news-title {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  margin-bottom: 2px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hot-news-views {
  font-size: 12px;
  color: #999;
}

/* 最新动态列表 */
.latest-news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.latest-news-item {
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.latest-news-item:hover {
  background-color: #f5f7fa;
}

.latest-news-title {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.latest-news-time {
  font-size: 12px;
  color: #999;
}

/* 标签列表 */
.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.news-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.news-tag:hover {
  background-color: #409EFF;
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .news-page {
    padding: 10px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .news-content {
    height: auto;
    min-height: 120px;
  }

  .sidebar {
    margin-top: 30px;
  }
}
</style>