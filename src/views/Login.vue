<template>
  <div class="login-container">
    <div class="login-form">
      <h2>游泳馆管理系统</h2>
      <p class="subtitle">用户登录</p>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="用户登录" name="user">
          <el-form :model="userForm" :rules="userRules" ref="userLoginForm" class="login-form-content">
            <el-form-item prop="yonghuzhanghao">
              <el-input
                  v-model="userForm.yonghuzhanghao"
                  placeholder="请输入账号"
                  size="large"
                  @keyup.enter="handleUserLogin"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="mima">
              <el-input
                  v-model="userForm.mima"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  @keyup.enter="handleUserLogin"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                  type="primary"
                  size="large"
                  @click="handleUserLogin"
                  :loading="loading"
                  style="width: 100%"
              >
                {{ loading ? '登录中...' : '用户登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="管理员登录" name="admin">
          <el-form :model="adminForm" :rules="adminRules" ref="adminLoginForm" class="login-form-content">
            <el-form-item prop="username">
              <el-input
                  v-model="adminForm.username"
                  placeholder="请输入管理员账号"
                  size="large"
                  @keyup.enter="handleAdminLogin"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                  v-model="adminForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  @keyup.enter="handleAdminLogin"
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                  type="success"
                  size="large"
                  @click="handleAdminLogin"
                  :loading="loading"
                  style="width: 100%"
              >
                {{ loading ? '登录中...' : '管理员登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="login-links">
        <router-link to="/register">还没有账号？立即注册</router-link>
        <span class="divider">|</span>
        <router-link to="/home">先浏览首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const activeTab = ref('user')
const loading = ref(false)
const showTestData = ref(true)

const userLoginForm = ref()
const adminLoginForm = ref()

// 用户登录表单
const userForm = ref({
  yonghuzhanghao: '',
  mima: ''
})

// 管理员登录表单
const adminForm = ref({
  username: '',
  password: ''
})

// 验证规则
const userRules = {
  yonghuzhanghao: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  mima: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const adminRules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 方法
const handleUserLogin = async () => {
  try {
    await userLoginForm.value.validate()
    loading.value = true

    const result = await userStore.login(userForm.value)

    if (result.success) {
      ElMessage.success('用户登录成功')
      // 用户登录跳转到首页
      router.push('/home')
    }
  } catch (error) {
    console.error('登录错误详情:', error)
    ElMessage.error(error.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

const handleAdminLogin = async () => {
  try {
    await adminLoginForm.value.validate()
    loading.value = true

    // 检查管理员API是否可用
    try {
      const result = await userStore.adminLogin(adminForm.value)

      if (result.success) {
        ElMessage.success('管理员登录成功')
        // ⭐️ 重要修改：管理员登录后跳转到管理后台
        router.push('/admin/dashboard')
      }
    } catch (apiError) {
      console.error('管理员API错误:', apiError)

      // 如果管理员API不可用，尝试使用用户API登录管理员账号
      if (apiError.message.includes('API配置错误') || apiError.message.includes('adminApi')) {
        ElMessage.warning('管理员API暂不可用，尝试使用用户登录...')

        // 使用用户登录方式
        const userLoginData = {
          yonghuzhanghao: adminForm.value.username,
          mima: adminForm.value.password
        }

        const userResult = await userStore.login(userLoginData)
        if (userResult.success) {
          // 检查是否是管理员账号
          if (adminForm.value.username === 'admin') {
            // 手动设置为管理员
            userStore.isAdmin = true
            localStorage.setItem('isAdmin', 'true')
            ElMessage.success('管理员登录成功（模拟）')
            // ⭐️ 重要修改：管理员登录后跳转到管理后台
            router.push('/admin/dashboard')
          } else {
            ElMessage.success('用户登录成功')
            router.push('/home')
          }
        }
      } else {
        throw apiError
      }
    }
  } catch (error) {
    console.error('管理员登录错误详情:', error)
    ElMessage.error(error.message || '管理员登录失败')
  } finally {
    loading.value = false
  }
}

// 填充测试数据
const fillTestData = () => {
  userForm.value = {
    yonghuzhanghao: 'testuser',
    mima: '123456'
  }

  adminForm.value = {
    username: 'admin',
    password: '123456'
  }
}

// 生命周期
onMounted(() => {
  // 开发环境下自动填充测试数据
  if (process.env.NODE_ENV === 'development') {
    fillTestData()
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-form {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.login-form h2 {
  text-align: center;
  margin-bottom: 5px;
  color: #333;
  font-size: 24px;
}

.subtitle {
  text-align: center;
  margin-bottom: 30px;
  color: #666;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-form-content {
  margin-top: 20px;
}

.login-links {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}

.login-links a {
  color: #409EFF;
  text-decoration: none;
  margin: 0 10px;
}

.login-links a:hover {
  text-decoration: underline;
}

.divider {
  color: #ccc;
  margin: 0 5px;
}

.test-data {
  margin-top: 20px;
}
</style>