import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: false // 如果不需要cookie验证，设为false
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        console.log('🚀 发送请求:', config.method?.toUpperCase(), config.url)
        console.log('📦 请求数据:', config.data)

        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }

        // 添加CORS相关headers
        config.headers['X-Requested-With'] = 'XMLHttpRequest'

        return config
    },
    error => {
        console.error('❌ 请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        console.log('✅ 收到响应:', response.config.url)
        console.log('📦 响应数据:', response.data)
        return response.data
    },
    error => {
        console.error('❌ 响应错误:', error)

        if (error.code === 'ECONNABORTED') {
            ElMessage.error('请求超时，请检查网络连接')
        } else if (error.response) {
            // 服务器返回错误状态码
            const status = error.response.status
            const message = error.response.data?.message || error.response.data || '请求失败'

            switch (status) {
                case 400:
                    ElMessage.error(`请求参数错误: ${message}`)
                    break
                case 401:
                    ElMessage.error('未授权，请重新登录')
                    localStorage.removeItem('token')
                    localStorage.removeItem('userInfo')
                    window.location.href = '/login'
                    break
                case 403:
                    ElMessage.error('权限不足')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error('服务器内部错误')
                    break
                default:
                    ElMessage.error(message)
            }
        } else if (error.request) {
            // 请求发送失败 - 特别处理CORS错误
            if (error.message && error.message.includes('CORS')) {
                ElMessage.error('跨域请求被阻止，请检查后端CORS配置')
                console.error('🔧 CORS错误详情:', {
                    url: error.config?.url,
                    method: error.config?.method,
                    message: error.message
                })
            } else {
                ElMessage.error('网络错误，请检查：\n1. 后端服务是否启动\n2. 网络连接是否正常')
            }
            console.error('网络错误详情:', error.request)
        } else {
            ElMessage.error('请求配置错误: ' + error.message)
        }

        return Promise.reject(error)
    }
)

export default request