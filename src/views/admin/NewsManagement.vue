<!-- src/views/admin/NewsManagement.vue -->
<template>
  <div class="news-management">
    <!-- 页面标题和操作栏 -->
    <div class="page-header">
      <h2>新闻管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddNews">
          <el-icon><Plus /></el-icon>
          添加新闻
        </el-button>
        <el-button @click="fetchNewsList" :loading="newsStore.loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索新闻标题、简介或内容..."
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
          <el-button type="primary" @click="handleSearch">
            搜索
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
        </el-col>
        <el-col :span="12" class="text-right">
          <span class="news-count">共 {{ filteredNews.length }} 条新闻</span>
        </el-col>
      </el-row>
    </div>

    <!-- 新闻列表 -->
    <div class="news-list">
      <el-table
          :data="paginatedNews"
          v-loading="newsStore.loading"
          style="width: 100%"
          empty-text="暂无新闻数据"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="scope">
            <div class="news-title-cell">
              <div class="news-title">{{ scope.row.title }}</div>
              <div class="news-intro-preview">{{ scope.row.introduction }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="图片" width="100">
          <template #default="scope">
            <el-image
                v-if="scope.row.image"
                :src="scope.row.image"
                :preview-src-list="[scope.row.image]"
                fit="cover"
                style="width: 60px; height: 40px; border-radius: 4px;"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <span v-else class="no-image">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="addtime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.addtime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
                size="small"
                type="primary"
                @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
                size="small"
                type="danger"
                @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑新闻对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEditing ? '编辑新闻' : '添加新闻'"
        width="700px"
        :close-on-click-modal="false"
    >
      <el-form
          ref="newsFormRef"
          :model="newsForm"
          :rules="newsRules"
          label-width="100px"
      >
        <el-form-item label="新闻标题" prop="title">
          <el-input
              v-model="newsForm.title"
              placeholder="请输入新闻标题"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="新闻简介" prop="introduction">
          <el-input
              v-model="newsForm.introduction"
              type="textarea"
              :rows="3"
              placeholder="请输入新闻简介"
              maxlength="200"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="新闻内容" prop="content">
          <el-input
              v-model="newsForm.content"
              type="textarea"
              :rows="6"
              placeholder="请输入新闻内容"
              maxlength="2000"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="图片URL" prop="image">
          <el-input
              v-model="newsForm.image"
              placeholder="请输入新闻图片URL"
          />
          <div class="image-preview" v-if="newsForm.image">
            <el-image
                :src="newsForm.image"
                style="width: 100px; height: 80px; margin-top: 8px;"
                fit="cover"
            >
              <template #error>
                <div class="image-slot">图片加载失败</div>
              </template>
            </el-image>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false" :disabled="submitting">
            取消
          </el-button>
          <el-button
              type="primary"
              @click="handleSubmit"
              :loading="submitting"
          >
            {{ isEditing ? '更新' : '添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Picture } from '@element-plus/icons-vue'
import { useNewsStore } from '@/store/news'

const newsStore = useNewsStore()

// 响应式数据
const dialogVisible = ref(false)
const isEditing = ref(false)
const submitting = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const newsFormRef = ref()

// 新闻表单
const newsForm = reactive({
  id: null,
  title: '',
  introduction: '',
  content: '',
  image: ''
})

// 验证规则
const newsRules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  introduction: [
    { required: true, message: '请输入新闻简介', trigger: 'blur' },
    { min: 10, max: 200, message: '简介长度在 10 到 200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入新闻内容', trigger: 'blur' },
    { min: 20, max: 2000, message: '内容长度在 20 到 2000 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const filteredNews = computed(() => newsStore.filteredNews)
const total = computed(() => filteredNews.value.length)

const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredNews.value.slice(start, end)
})

// 方法
const fetchNewsList = async () => {
  await newsStore.fetchNewsList()
}

const handleSearch = () => {
  newsStore.searchNews(searchKeyword.value)
  currentPage.value = 1
}

const handleReset = () => {
  searchKeyword.value = ''
  newsStore.searchKeyword = ''
  currentPage.value = 1
  fetchNewsList()
}

const handleAddNews = () => {
  isEditing.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (news) => {
  isEditing.value = true
  Object.assign(newsForm, {
    id: news.id,
    title: news.title || '',
    introduction: news.introduction || '',
    content: news.content || '',
    image: news.image || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await newsFormRef.value.validate()
    submitting.value = true

    const newsData = { ...newsForm }

    if (isEditing.value) {
      // 更新新闻
      await newsStore.updateNews(newsData.id, newsData)
      ElMessage.success('新闻更新成功')
    } else {
      // 添加新闻
      await newsStore.addNews(newsData)
      ElMessage.success('新闻添加成功')
    }

    dialogVisible.value = false
    await fetchNewsList()
  } catch (error) {
    console.error('提交新闻失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (news) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除新闻"${news.title}"吗？此操作不可恢复。`,
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning',
          confirmButtonClass: 'el-button--danger'
        }
    )

    await newsStore.deleteNews(news.id)
    ElMessage.success('新闻删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除新闻失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
}

const resetForm = () => {
  Object.assign(newsForm, {
    id: null,
    title: '',
    introduction: '',
    content: '',
    image: ''
  })
  if (newsFormRef.value) {
    newsFormRef.value.clearValidate()
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生命周期
onMounted(() => {
  fetchNewsList()
})
</script>

<style scoped>
.news-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.text-right {
  text-align: right;
}

.news-count {
  color: #666;
  font-size: 14px;
  line-height: 32px;
}

.news-list {
  margin-bottom: 20px;
}

.news-title-cell {
  line-height: 1.4;
}

.news-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.news-intro-preview {
  font-size: 12px;
  color: #909399;
  line-height: 1.3;
}

.no-image {
  color: #c0c4cc;
  font-size: 12px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
}

.image-preview {
  margin-top: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>