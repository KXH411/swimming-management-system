<template>
  <div class="profile-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>个人中心</h1>
      <p>管理您的个人信息和账户设置</p>
    </div>

    <div class="profile-container">
      <!-- 左侧：个人信息 -->
      <div class="profile-sidebar">
        <el-card class="user-card">
          <!-- 用户头像 -->
          <div class="user-avatar-section">
            <el-avatar
                :size="100"
                :src="userInfo.touxiang"
                class="user-avatar"
            >
              {{ userInfo.yonghuxingming?.charAt(0) || '用户' }}
            </el-avatar>
          </div>

          <!-- 用户信息 -->
          <div class="user-info">
            <h2 class="user-name">{{ userInfo.yonghuxingming || '用户' }}</h2>
            <p class="user-account">账号: {{ userInfo.yonghuzhanghao || userInfo.username }}</p>
            <el-tag
                v-if="isAdmin"
                type="success"
                size="small"
            >
              管理员
            </el-tag>
            <el-tag v-else type="info" size="small">
              普通用户
            </el-tag>
          </div>

          <!-- 用户统计 -->
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">{{ userStats.reservationCount }}</div>
              <div class="stat-label">总预约</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userStats.favoriteCount }}</div>
              <div class="stat-label">收藏</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">0</div>
              <div class="stat-label">会员等级</div>
            </div>
          </div>
        </el-card>

        <!-- 导航菜单 -->
        <el-card class="nav-card">
          <el-menu
              :default-active="activeTab"
              class="profile-menu"
              @select="handleMenuSelect"
          >
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="favorites">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>账户安全</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </div>

      <!-- 右侧：内容区域 -->
      <div class="profile-content">
        <!-- 个人信息 -->
        <el-card v-if="activeTab === 'profile'" class="content-card">
          <template #header>
            <div class="card-header">
              <h3>个人信息</h3>
              <el-button
                  type="primary"
                  @click="editProfile"
                  :loading="loading"
              >
                {{ isEditing ? '保存' : '编辑' }}
              </el-button>
            </div>
          </template>

          <el-form
              :model="profileForm"
              :rules="profileRules"
              ref="profileFormRef"
              label-width="100px"
              :disabled="!isEditing"
          >
            <el-form-item label="姓名" prop="yonghuxingming">
              <el-input
                  v-model="profileForm.yonghuxingming"
                  placeholder="请输入姓名"
              />
            </el-form-item>

            <el-form-item label="性别" prop="xingbie">
              <el-radio-group v-model="profileForm.xingbie">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="联系电话" prop="lianxidianhua">
              <el-input
                  v-model="profileForm.lianxidianhua"
                  placeholder="请输入联系电话"
              />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input
                  v-model="profileForm.email"
                  placeholder="请输入邮箱地址"
              />
            </el-form-item>

            <el-form-item label="地址" prop="address">
              <el-input
                  v-model="profileForm.address"
                  placeholder="请输入地址"
                  type="textarea"
                  :rows="2"
              />
            </el-form-item>

            <el-form-item label="个人简介" prop="jianjie">
              <el-input
                  v-model="profileForm.jianjie"
                  placeholder="请输入个人简介"
                  type="textarea"
                  :rows="3"
              />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 我的收藏 -->
        <el-card v-else-if="activeTab === 'favorites'" class="content-card">
          <template #header>
            <div class="card-header">
              <h3>我的收藏</h3>
              <div class="header-actions">
                <el-button @click="refreshFavorites" :loading="favoriteStore.loading">
                  <el-icon><Refresh /></el-icon>
                  刷新
                </el-button>
                <el-button type="primary" @click="$router.push('/courses')">
                  <el-icon><Plus /></el-icon>
                  继续收藏
                </el-button>
              </div>
            </div>
          </template>

          <div class="favorites-list">
            <div v-if="favoriteStore.loading" class="loading-state">
              <el-skeleton :rows="2" animated />
            </div>

            <div v-else>
              <el-row :gutter="20">
                <el-col
                    v-for="course in favoriteStore.favorites"
                    :key="course.id"
                    :xs="24" :sm="12" :md="8"
                >
                  <el-card class="favorite-item" shadow="hover">
                    <div class="favorite-image">
                      <img 
                        :src="course.fengmian || '/images/course-default.jpg'" 
                        :alt="course.xiangmumingcheng"
                        @error="handleImageError"
                      >
                    </div>
                    <div class="favorite-content">
                      <h4 class="favorite-title">{{ course.xiangmumingcheng }}</h4>
                      <p class="favorite-coach">
                        <el-icon><User /></el-icon>
                        教练: {{ course.jiaolianmingcheng || '待定' }}
                      </p>
                      <p class="favorite-type">
                        <el-icon><Collection /></el-icon>
                        类型: {{ course.leixing || '游泳课程' }}
                      </p>
                      <div class="favorite-price">¥{{ course.jiage || 0 }}</div>
                      <div class="favorite-actions">
                        <el-button type="primary" size="small" @click="viewCourseDetail(course.id)">
                          查看课程
                        </el-button>
                        <el-button type="danger" size="small" plain @click="removeFavorite(course.id)">
                          取消收藏
                        </el-button>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>

              <div v-if="favoriteStore.favorites.length === 0" class="empty-state">
                <el-empty description="暂无收藏课程">
                  <el-button type="primary" @click="$router.push('/courses')">
                    去发现课程
                  </el-button>
                </el-empty>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 账户安全 -->
        <el-card v-else-if="activeTab === 'security'" class="content-card">
          <template #header>
            <div class="card-header">
              <h3>账户安全</h3>
            </div>
          </template>

          <el-form
              :model="passwordForm"
              :rules="passwordRules"
              ref="passwordFormRef"
              label-width="120px"
          >
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                  v-model="passwordForm.currentPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
              />
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button
                  type="primary"
                  @click="changePassword"
                  :loading="loading"
              >
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useFavoriteStore } from '@/store/favorite'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Star,
  Lock,
  Refresh,
  Plus,
  Collection
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const favoriteStore = useFavoriteStore()

// 响应式数据
const activeTab = ref('profile')
const isEditing = ref(false)
const loading = ref(false)

const profileFormRef = ref()
const passwordFormRef = ref()

// 表单数据
const profileForm = reactive({
  yonghuxingming: '',
  xingbie: '男',
  lianxidianhua: '',
  email: '',
  address: '',
  jianjie: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 计算属性
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userStore.isAdmin)

const userStats = computed(() => ({
  reservationCount: userStore.reservations?.length || 0,
  favoriteCount: favoriteStore.favoriteCount
}))

// 验证规则
const profileRules = {
  yonghuxingming: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  lianxidianhua: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 方法
const handleMenuSelect = (index) => {
  activeTab.value = index
}

const editProfile = async () => {
  if (!isEditing.value) {
    Object.assign(profileForm, userInfo.value)
    isEditing.value = true
    return
  }

  try {
    await profileFormRef.value.validate()
    loading.value = true

    const result = await userStore.updateUserInfo(profileForm)

    if (result.success) {
      ElMessage.success('个人信息更新成功')
      isEditing.value = false
    }
  } catch (error) {
    console.error('保存个人信息失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()

    await ElMessageBox.confirm('确定要修改密码吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    loading.value = true

    ElMessage.warning('密码修改功能暂未实现，请联系管理员')

    Object.assign(passwordForm, {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    passwordFormRef.value.resetFields()

  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改密码失败:', error)
      ElMessage.error('修改密码失败')
    }
  } finally {
    loading.value = false
  }
}

// 🔥 只定义一次 refreshFavorites
const refreshFavorites = async () => {
  await favoriteStore.fetchMyFavorites()
  ElMessage.success('刷新成功')
}

// 取消收藏
const removeFavorite = async (courseId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const result = await favoriteStore.removeFavorite(courseId)

    if (result.success) {
      ElMessage.success(result.message || '已取消收藏')
      await favoriteStore.fetchMyFavorites()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 查看课程详情
const viewCourseDetail = (courseId) => {
  router.push(`/course-detail/${courseId}`)
}

// 图片加载失败处理
const handleImageError = (event) => {
  event.target.src = '/images/course-default.jpg'
}

// 生命周期
onMounted(async () => {
  // 初始化表单数据
  Object.assign(profileForm, userInfo.value)

  // 加载预约数据
  if (userStore.isLoggedIn) {
    await userStore.fetchUserReservations?.()
    await favoriteStore.fetchMyFavorites()
  }

  console.log('个人中心页面加载完成，收藏数量:', favoriteStore.favoriteCount)
})
</script>

<style scoped>
.profile-page {
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

.profile-container {
  display: flex;
  gap: 20px;
}

.profile-sidebar {
  width: 300px;
  flex-shrink: 0;
}

.profile-content {
  flex: 1;
}

.user-card {
  margin-bottom: 20px;
}

.user-avatar-section {
  text-align: center;
  padding: 20px 0;
}

.user-avatar {
  border: 3px solid #409EFF;
}

.user-info {
  text-align: center;
  padding: 10px 0;
}

.user-name {
  margin: 10px 0 5px;
  color: #333;
}

.user-account {
  color: #666;
  margin: 5px 0 10px;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.nav-card {
  margin-bottom: 20px;
}

.profile-menu {
  border: none;
}

.content-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.favorite-item {
  margin-bottom: 15px;
}

.favorite-content {
  padding: 10px;
}

.favorite-title {
  margin: 0 0 8px;
  color: #333;
}

.favorite-coach {
  color: #666;
  margin: 0 0 8px;
}

.favorite-price {
  color: #f56c6c;
  font-weight: bold;
  margin: 0 0 10px;
}

.favorite-actions {
  display: flex;
  gap: 8px;
}

.loading-state {
  padding: 20px 0;
}

.empty-state {
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
  }

  .profile-sidebar {
    width: 100%;
  }

  .user-stats {
    padding: 10px 0;
  }

  .stat-value {
    font-size: 16px;
  }
}
</style>