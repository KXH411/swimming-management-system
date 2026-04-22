<template>
  <div class="layout">
    <el-container>
      <!-- 头部导航 -->
      <el-header class="header">
        <div class="nav-content">
          <div class="logo">
            <h2>🏊 游泳馆管理系统</h2>
          </div>
          <el-menu
              mode="horizontal"
              :default-active="activeIndex"
              @select="handleSelect"
              class="nav-menu"
          >
            <el-menu-item index="/home">首页</el-menu-item>
            <el-menu-item index="/courses">游泳课程</el-menu-item>
            <el-menu-item index="/coaches">教练信息</el-menu-item>
            <el-menu-item index="/news">新闻资讯</el-menu-item>
            <el-menu-item index="/reservations">我的预约</el-menu-item>
            <el-menu-item index="/profile">个人中心</el-menu-item>
          </el-menu>
          <div class="user-info">
            <el-dropdown v-if="isLoggedIn">
              <span class="el-dropdown-link">
                <el-avatar :size="32" :src="userInfo.touxiang">
                  {{ userInfo.yonghuxingming?.charAt(0) || '用户' }}
                </el-avatar>
                {{ userInfo.yonghuxingming || '用户' }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <div v-else>
              <el-button @click="$router.push('/login')">登录</el-button>
              <el-button type="primary" @click="$router.push('/register')">注册</el-button>
            </div>
          </div>
        </div>
      </el-header>

      <!-- 主要内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <div class="footer-content">
          <p>© 2026 游泳馆管理系统 - 基于Spring Boot和Vue.js开发</p>
          <p>联系电话: 400-123-4567 | 营业时间: 09:00-22:00</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

const activeIndex = ref('/home')

// 计算属性
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

const userInfo = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || '{}')
  } catch {
    return {}
  }
})

// 方法
const handleSelect = (index) => {
  router.push(index)
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    // 用户取消退出
  }
}

// 监听路由变化，更新激活菜单
watch(
    () => route.path,
    (newPath) => {
      activeIndex.value = newPath
    },
    { immediate: true }
)

onMounted(() => {
  // 初始化激活菜单
  activeIndex.value = route.path
})
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo h2 {
  margin: 0;
  color: #409EFF;
}

.nav-menu {
  flex: 1;
  margin: 0 20px;
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.footer {
  background: #f5f7fa;
  text-align: center;
  padding: 20px;
}

.footer-content p {
  margin: 5px 0;
  color: #666;
}
</style>