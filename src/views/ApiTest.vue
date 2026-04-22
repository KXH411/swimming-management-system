<template>
  <div class="api-test">
    <h2>API测试页面</h2>

    <el-card class="test-card">
      <template #header>
        <span>用户注册测试</span>
      </template>
      <el-form :model="registerForm" label-width="100px">
        <el-form-item label="账号">
          <el-input v-model="registerForm.yonghuzhanghao" placeholder="输入账号"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.mima" type="password" placeholder="输入密码"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="registerForm.yonghuxingming" placeholder="输入姓名"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="testRegister">测试注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="test-card">
      <template #header>
        <span>用户登录测试</span>
      </template>
      <el-form :model="loginForm" label-width="100px">
        <el-form-item label="账号">
          <el-input v-model="loginForm.yonghuzhanghao" placeholder="输入账号"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.mima" type="password" placeholder="输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="testLogin">测试登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="test-card">
      <template #header>
        <span>用户列表测试</span>
      </template>
      <el-button @click="testUserList">获取用户列表</el-button>
      <div v-if="userList.length > 0" class="result">
        <h4>用户列表:</h4>
        <pre>{{ JSON.stringify(userList, null, 2) }}</pre>
      </div>
    </el-card>

    <el-card class="test-card">
      <template #header>
        <span>控制台输出</span>
      </template>
      <div class="console">
        <p>请打开浏览器开发者工具查看详细的网络请求和响应</p>
        <p>按F12 → Network标签 → 查看API请求</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const registerForm = ref({
  yonghuzhanghao: 'testuser' + Date.now().toString().slice(-4),
  mima: '123456',
  yonghuxingming: '测试用户'
})

const loginForm = ref({
  yonghuzhanghao: 'testuser',
  mima: '123456'
})

const userList = ref([])

const testRegister = async () => {
  try {
    console.log('🧪 测试注册:', registerForm.value)
    const result = await userApi.register(registerForm.value)
    console.log('✅ 注册测试结果:', result)
    ElMessage.success('注册测试成功，查看控制台详情')
  } catch (error) {
    console.error('❌ 注册测试失败:', error)
    ElMessage.error('注册测试失败，查看控制台错误信息')
  }
}

const testLogin = async () => {
  try {
    console.log('🧪 测试登录:', loginForm.value)
    const result = await userApi.login(loginForm.value)
    console.log('✅ 登录测试结果:', result)
    ElMessage.success('登录测试成功，查看控制台详情')
  } catch (error) {
    console.error('❌ 登录测试失败:', error)
    ElMessage.error('登录测试失败，查看控制台错误信息')
  }
}

const testUserList = async () => {
  try {
    console.log('🧪 测试获取用户列表')
    const result = await userApi.getUsers()
    console.log('✅ 用户列表测试结果:', result)
    userList.value = result
    ElMessage.success('获取用户列表成功')
  } catch (error) {
    console.error('❌ 用户列表测试失败:', error)
    ElMessage.error('获取用户列表失败，查看控制台错误信息')
  }
}
</script>

<style scoped>
.api-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-card {
  margin-bottom: 20px;
}

.result {
  margin-top: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.console {
  color: #666;
  font-size: 14px;
}

pre {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>