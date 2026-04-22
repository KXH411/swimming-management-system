<template>
  <div class="register-container">
    <div class="register-form">
      <h2>用户注册</h2>
      <p class="subtitle">创建您的游泳馆账户</p>

      <el-form :model="form" :rules="rules" ref="registerForm" class="register-form-content">
        <el-form-item prop="yonghuzhanghao">
          <el-input
              v-model="form.yonghuzhanghao"
              placeholder="请输入账号（3-20位字符）"
              size="large"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="mima">
          <el-input
              v-model="form.mima"
              type="password"
              placeholder="请输入密码（6-20位字符）"
              size="large"
              show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="yonghuxingming">
          <el-input
              v-model="form.yonghuxingming"
              placeholder="请输入真实姓名"
              size="large"
          >
            <template #prefix>
              <el-icon><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="xingbie">
          <el-radio-group v-model="form.xingbie">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item prop="lianxidianhua">
          <el-input
              v-model="form.lianxidianhua"
              placeholder="请输入联系电话"
              size="large"
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              @click="handleRegister"
              :loading="loading"
              style="width: 100%"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-links">
        <router-link to="/login">已有账号？立即登录</router-link>
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
const loading = ref(false)
const registerForm = ref()

// 注册表单
const form = ref({
  yonghuzhanghao: '',
  mima: '',
  confirmPassword: '',
  yonghuxingming: '',
  xingbie: '男',
  lianxidianhua: ''
})

// 自定义验证规则
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请确认密码'))
  } else if (value !== form.value.mima) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号码'))
  } else {
    callback()
  }
}

// 验证规则
const rules = {
  yonghuzhanghao: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  mima: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  yonghuxingming: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  xingbie: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  lianxidianhua: [
    { validator: validatePhone, trigger: 'blur' }
  ]
}

// 方法
const handleRegister = async () => {
  try {
    // 表单验证
    await registerForm.value.validate()
    loading.value = true

    // 准备注册数据（移除确认密码字段）
    const registerData = { ...form.value }
    delete registerData.confirmPassword

    // 调用注册接口
    const result = await userStore.register(registerData)

    if (result.success) {
      ElMessage.success('注册成功！请登录')
      router.push('/login')
    }
  } catch (error) {
    console.error('注册错误:', error)
    ElMessage.error(error.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 填充测试数据（开发环境使用）
const fillTestData = () => {
  form.value = {
    yonghuzhanghao: 'newuser' + Date.now().toString().slice(-4),
    mima: '123456',
    confirmPassword: '123456',
    yonghuxingming: '测试用户',
    xingbie: '男',
    lianxidianhua: '13800138000'
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
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-form {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.register-form h2 {
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

.register-form-content {
  margin-bottom: 20px;
}

.register-links {
  text-align: center;
  margin-top: 20px;
}

.register-links a {
  color: #409EFF;
  text-decoration: none;
}

.register-links a:hover {
  text-decoration: underline;
}
</style>