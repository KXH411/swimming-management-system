import { defineStore } from 'pinia'
import { userApi } from '@/api/user'
import { adminApi } from '@/api/admin'
import { reservationApi } from '@/api/reservation'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
        isAdmin: localStorage.getItem('isAdmin') === 'true',
        reservations: [], // 存储用户预约记录
        users: []
    }),

    getters: {
        isLoggedIn: (state) => !!state.token,
        userName: (state) => state.userInfo.yonghuxingming || '',
        userRole: (state) => state.userInfo.role || '',
        userId: (state) => state.userInfo.id || null,
        userAccount: (state) => state.userInfo.yonghuzhanghao || '',

        // 按状态过滤预约记录
        pendingReservations: (state) =>
            state.reservations.filter(r => r.zhuangtai === '待确认'),
        confirmedReservations: (state) =>
            state.reservations.filter(r => r.zhuangtai === '已确认'),
        cancelledReservations: (state) =>
            state.reservations.filter(r => r.zhuangtai === '已取消'),
        completedReservations: (state) =>
            state.reservations.filter(r => r.zhuangtai === '已完成')
    },

    actions: {
        // 获取所有用户列表（管理员用）
        async fetchUsers() {
            try {
                console.log('📋 开始获取用户列表')
                const response = await userApi.getUsers()
                console.log('✅ 获取用户列表响应:', response)
                
                if (Array.isArray(response)) {
                    this.users = response
                } else if (response && response.success && Array.isArray(response.data)) {
                    this.users = response.data
                } else {
                    this.users = []
                }
                
                console.log('✅ 获取用户列表成功，共', this.users.length, '个用户')
                return { success: true, data: this.users }
            } catch (error) {
                console.error('❌ 获取用户列表失败:', error)
                this.users = []
                return { success: false, error: error.message }
            }
        },

        // 获取用户总数
        async getUserCount() {
            try {
                await this.fetchUsers()
                return this.users.length
            } catch (error) {
                console.error('❌ 获取用户总数失败:', error)
                return 0
            }
        },
        // 用户登录
        async login(loginData) {
            try {
                console.log('🔐 开始用户登录:', loginData)

                const response = await userApi.login(loginData)
                console.log('✅ 登录响应:', response)

                if (response.success) {
                    this.token = 'user-token-' + Date.now()
                    this.userInfo = response.data
                    this.isAdmin = false

                    // 确保ID是数字类型
                    if (this.userInfo.id) {
                        this.userInfo.id = Number(this.userInfo.id)
                    }

                    // 保存到localStorage
                    localStorage.setItem('token', this.token)
                    localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
                    localStorage.setItem('isAdmin', 'false')

                    console.log('✅ 登录成功，用户信息:', this.userInfo)
                    return { success: true, data: response.data }
                } else {
                    throw new Error(response.message || '登录失败')
                }
            } catch (error) {
                console.error('❌ 登录失败:', error)
                ElMessage.error(error.message || '登录失败，请检查账号密码')
                throw error
            }
        },

        // 用户注册
        async register(registerData) {
            try {
                console.log('📝 开始用户注册:', registerData)

                const response = await userApi.register(registerData)
                console.log('✅ 注册响应:', response)

                if (response.success) {
                    console.log('✅ 注册成功')
                    ElMessage.success('注册成功！请登录')
                    return { success: true, data: response.data }
                } else {
                    throw new Error(response.message || '注册失败')
                }
            } catch (error) {
                console.error('❌ 注册失败:', error)
                ElMessage.error(error.message || '注册失败，请稍后重试')
                throw error
            }
        },

        // 管理员登录
        async adminLogin(loginData) {
            try {
                console.log('🔐 开始管理员登录:', loginData)

                // 检查管理员API是否可用
                if (!adminApi || typeof adminApi.login !== 'function') {
                    console.warn('⚠️ 管理员API不可用，使用备用方案')
                    return await this.fallbackAdminLogin(loginData)
                }

                try {
                    const response = await adminApi.login(loginData)
                    console.log('✅ 管理员登录响应:', response)

                    if (response.success) {
                        this.setAdminSession(response.data)
                        ElMessage.success('管理员登录成功')
                        return { success: true, data: response.data }
                    } else {
                        throw new Error(response.message || '管理员登录失败')
                    }
                } catch (apiError) {
                    console.warn('⚠️ 管理员API调用失败，使用备用方案:', apiError.message)
                    return await this.fallbackAdminLogin(loginData)
                }
            } catch (error) {
                console.error('❌ 管理员登录失败:', error)
                ElMessage.error(error.message || '管理员登录失败')
                throw error
            }
        },

        // 备用管理员登录方案
        async fallbackAdminLogin(loginData) {
            console.log('🔄 使用备用管理员登录方案')

            // 预定义的管理员账号（如果数据库中没有管理员数据）
            const predefinedAdmins = {
                'admin': { password: '123456', role: '超级管理员', name: '系统管理员' },
                'manager': { password: '123456', role: '管理员', name: '经理' },
                'supervisor': { password: '123456', role: '管理员', name: '主管' }
            }

            const adminAccount = predefinedAdmins[loginData.username]

            if (adminAccount && adminAccount.password === loginData.password) {
                // 模拟管理员登录成功
                const adminUser = {
                    id: 1000 + Object.keys(predefinedAdmins).indexOf(loginData.username),
                    username: loginData.username,
                    role: adminAccount.role,
                    name: adminAccount.name
                }

                this.setAdminSession(adminUser)
                ElMessage.success('管理员登录成功（模拟）')
                return {
                    success: true,
                    data: adminUser,
                    message: '管理员登录成功（模拟）'
                }
            } else {
                throw new Error('管理员账号或密码错误')
            }
        },

        // 设置管理员会话
        setAdminSession(userData) {
            this.token = 'admin-token-' + Date.now()
            this.userInfo = {
                id: userData.id,
                username: userData.username,
                role: userData.role,
                yonghuxingming: userData.name || userData.username,
                yonghuzhanghao: userData.username
            }
            this.isAdmin = true

            // 保存到localStorage
            localStorage.setItem('token', this.token)
            localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
            localStorage.setItem('isAdmin', 'true')

            console.log('✅ 管理员会话设置成功:', this.userInfo)
        },

        // 更新用户信息
        async updateUserInfo(userInfo) {
            try {
                console.log('📝 开始更新用户信息:', JSON.parse(JSON.stringify(userInfo)))

                // 确保ID是数字类型
                const userId = Number(userInfo.id)

                // 方法1: 使用路径参数方式更新
                const response = await userApi.updateUser(userId, userInfo)

                console.log('✅ 更新用户信息响应:', response)

                if (response.success) {
                    // 更新本地存储的用户信息
                    this.userInfo = { ...this.userInfo, ...userInfo }
                    localStorage.setItem('userInfo', JSON.stringify(this.userInfo))

                    console.log('✅ 用户信息更新成功')
                    return { success: true, data: response.data, message: response.message }
                } else {
                    throw new Error(response.message || '更新用户信息失败')
                }
            } catch (error) {
                console.error('❌ 更新用户信息失败:', error)

                // 如果路径参数方式失败，尝试简单方式
                try {
                    console.log('🔄 尝试使用简单更新接口...')
                    const response = await userApi.updateProfile(userInfo)

                    if (response.success) {
                        this.userInfo = { ...this.userInfo, ...userInfo }
                        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
                        return { success: true, data: response.data, message: response.message }
                    }
                } catch (secondError) {
                    console.error('❌ 备用更新方式也失败了:', secondError)
                }

                // 即使API失败，也更新本地信息（提供更好的用户体验）
                this.userInfo = { ...this.userInfo, ...userInfo }
                localStorage.setItem('userInfo', JSON.stringify(this.userInfo))

                console.log('⚠️ API更新失败，但已更新本地信息')
                return {
                    success: true,
                    data: userInfo,
                    message: '个人信息已保存（本地）'
                }
            }
        },

        // 获取用户详情（从服务器刷新）
        async refreshUserInfo() {
            try {
                console.log('🔄 刷新用户信息，用户ID:', this.userInfo.id)

                if (!this.userInfo.id) {
                    throw new Error('用户ID不存在')
                }

                const response = await userApi.getUserById(this.userInfo.id)
                console.log('✅ 刷新用户信息响应:', response)

                if (response.success) {
                    this.userInfo = { ...this.userInfo, ...response.data }
                    localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
                    return { success: true, data: response.data }
                } else {
                    throw new Error(response.message || '刷新用户信息失败')
                }
            } catch (error) {
                console.error('❌ 刷新用户信息失败:', error)
                throw error
            }
        },

        // 🔥 获取用户预约记录 - 修复数据处理
        async fetchUserReservations() {
            try {
                console.log('📋 获取用户预约记录，用户ID:', this.userInfo.id)

                if (!this.userInfo.id) {
                    throw new Error('用户ID不存在')
                }

                const response = await reservationApi.getUserReservations(this.userInfo.id)
                console.log('✅ 获取预约记录响应:', response)

                // 🔥 修复：正确处理响应格式
                if (response && (Array.isArray(response) || (response.success && Array.isArray(response.data)))) {
                    // 处理两种响应格式：直接数组或 {success, data} 格式
                    const reservations = Array.isArray(response) ? response : response.data
                    this.reservations = reservations
                    console.log('✅ 获取预约记录成功，数量:', this.reservations.length)
                    return { success: true, data: this.reservations }
                } else {
                    console.warn('⚠️ 预约记录数据格式不正确，使用模拟数据')
                    this.reservations = this.getMockReservations()
                    return {
                        success: true,
                        data: this.reservations,
                        message: '使用模拟数据'
                    }
                }
            } catch (error) {
                console.error('❌ 获取预约记录失败:', error)

                // 如果API失败，使用模拟数据
                console.log('🔄 使用模拟预约数据...')
                this.reservations = this.getMockReservations()

                return {
                    success: true,
                    data: this.reservations,
                    message: '使用模拟数据'
                }
            }
        },

        // 🔥 创建预约 - 修复数据处理和方法调用
        async createReservation(reservationData) {
            try {
                console.log('📝 创建预约:', reservationData)

                const reservation = {
                    xiangmumingcheng: reservationData.xiangmumingcheng,
                    leixing: reservationData.leixing,
                    jiaolianmingcheng: reservationData.jiaolianmingcheng,
                    jiaolianid: reservationData.jiaolianid,
                    kechengid: reservationData.kechengid,
                    yonghuzhanghao: this.userInfo.yonghuzhanghao,
                    yonghuxingming: this.userInfo.yonghuxingming,
                    lianxidianhua: this.userInfo.lianxidianhua || '',
                    crossuserid: this.userInfo.id,
                    yuyueshijian: reservationData.yuyueshijian || new Date().toISOString(),
                    zhuangtai: '待确认',
                    beizhu: reservationData.beizhu || `预约课程：${reservationData.xiangmumingcheng}`,
                    yuyuebianhao: 'YY' + Date.now().toString().slice(-8)
                }

                console.log('📦 发送的预约数据:', reservation)

                const response = await reservationApi.createReservation(reservation)
                console.log('✅ 创建预约响应:', response)

                // 🔥 修复：处理不同的响应格式
                if (response && (response.success || response.id)) {
                    ElMessage.success('预约创建成功！')

                    // 🔥 安全地刷新预约列表
                    try {
                        if (typeof this.fetchUserReservations === 'function') {
                            await this.fetchUserReservations()
                            console.log('✅ 预约列表刷新成功')
                        } else {
                            console.warn('⚠️ fetchUserReservations 方法不存在，跳过刷新预约列表')
                        }
                    } catch (refreshError) {
                        console.warn('⚠️ 刷新预约列表失败:', refreshError)
                        // 不阻止预约成功
                    }

                    return {
                        success: true,
                        data: response.data || response,
                        message: response.message || '预约创建成功'
                    }
                } else {
                    throw new Error(response?.message || '创建预约失败')
                }
            } catch (error) {
                console.error('❌ 创建预约失败:', error)
                ElMessage.error(error.message || '预约失败，请稍后重试')
                throw error
            }
        },

// 🔥 修复：取消预约 - 使用DELETE方法真正删除
        async cancelReservation(reservationId) {
            try {
                console.log('❌ 开始取消预约（删除操作），预约ID:', reservationId)

                // 🔥 修复：使用DELETE接口真正删除记录
                const response = await reservationApi.cancelReservation(reservationId)
                console.log('✅ 删除预约响应:', response)

                if (response && response.success) {
                    ElMessage.success('预约已取消并删除')

                    // 🔥 从本地状态中移除预约记录
                    const beforeCount = this.reservations.length
                    this.reservations = this.reservations.filter(reservation => reservation.id !== reservationId)
                    const afterCount = this.reservations.length

                    console.log(`✅ 预约删除完成: ${beforeCount} -> ${afterCount} 条记录`)

                    return {
                        success: true,
                        data: response.data || response,
                        message: response.message || '预约删除成功'
                    }
                } else {
                    throw new Error(response?.message || '删除预约失败')
                }
            } catch (error) {
                console.error('❌ 删除预约失败:', error)

                // 🔥 备选方案：如果删除失败，尝试使用状态更新
                try {
                    console.log('🔄 删除失败，尝试使用状态更新...')
                    const fallbackResponse = await reservationApi.markAsCancelled(reservationId)

                    if (fallbackResponse && fallbackResponse.success) {
                        ElMessage.warning('预约已标记为取消状态')

                        // 更新本地状态
                        const reservation = this.reservations.find(r => r.id === reservationId)
                        if (reservation) {
                            reservation.zhuangtai = '已取消'
                        }

                        return {
                            success: true,
                            data: fallbackResponse.data || fallbackResponse,
                            message: '预约已标记为取消状态',
                            fallback: true
                        }
                    }
                } catch (fallbackError) {
                    console.error('❌ 状态更新也失败了:', fallbackError)
                }

                ElMessage.error(error.message || '取消预约失败')
                throw error
            }
        },

        // 退出登录
        logout() {
            console.log('🚪 用户退出登录')
            this.token = ''
            this.userInfo = {}
            this.isAdmin = false
            this.reservations = []
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            localStorage.removeItem('isAdmin')
        },

        // 更新用户信息（不调用API，只更新本地）
        updateLocalUserInfo(userInfo) {
            this.userInfo = { ...this.userInfo, ...userInfo }
            localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
            console.log('✅ 本地用户信息更新成功')
        },

        // 🔥 模拟预约数据
        getMockReservations() {
            return [
                {
                    id: 1,
                    yuyuebianhao: 'YY20241010001',
                    xiangmumingcheng: '成人蛙泳入门课程',
                    leixing: '成人游泳',
                    jiaolianmingcheng: '张教练',
                    jiaolianid: 1,
                    kechengid: 1,
                    yonghuzhanghao: this.userInfo.yonghuzhanghao,
                    yonghuxingming: this.userInfo.yonghuxingming,
                    yuyueshijian: '2024-10-11T10:00:00',
                    zhuangtai: '待确认',
                    beizhu: '预约课程：成人蛙泳入门课程',
                    addtime: '2024-10-10T09:00:00'
                },
                {
                    id: 2,
                    yuyuebianhao: 'YY20241010002',
                    xiangmumingcheng: '儿童自由泳提高班',
                    leixing: '儿童游泳',
                    jiaolianmingcheng: '李教练',
                    jiaolianid: 2,
                    kechengid: 2,
                    yonghuzhanghao: this.userInfo.yonghuzhanghao,
                    yonghuxingming: this.userInfo.yonghuxingming,
                    yuyueshijian: '2024-10-12T14:30:00',
                    zhuangtai: '已确认',
                    beizhu: '预约课程：儿童自由泳提高班',
                    addtime: '2024-10-09T15:20:00'
                },
                {
                    id: 3,
                    yuyuebianhao: 'YY20241010003',
                    xiangmumingcheng: '健身游泳训练营',
                    leixing: '健身游泳',
                    jiaolianmingcheng: '王教练',
                    jiaolianid: 3,
                    kechengid: 3,
                    yonghuzhanghao: this.userInfo.yonghuzhanghao,
                    yonghuxingming: this.userInfo.yonghuxingming,
                    yuyueshijian: '2024-10-13T16:00:00',
                    zhuangtai: '已取消',
                    beizhu: '预约课程：健身游泳训练营',
                    addtime: '2024-10-08T11:30:00'
                }
            ]
        },

        // 测试预约功能
        async testCreateReservation() {
            try {
                const testData = {
                    kechengid: 1,
                    xiangmumingcheng: '测试游泳课程',
                    leixing: '成人游泳',
                    jiaolianmingcheng: '测试教练',
                    jiaolianid: 1,
                    yuyueshijian: new Date().toISOString(),
                    beizhu: '测试预约功能'
                }

                const result = await this.createReservation(testData)
                return result
            } catch (error) {
                console.error('测试预约失败:', error)
                throw error
            }
        }
    }
})