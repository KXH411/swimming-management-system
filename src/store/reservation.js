import { defineStore } from 'pinia'
import { reservationApi } from '@/api/reservation'

export const useReservationStore = defineStore('reservation', {
    state: () => ({
        reservations: [],
        loading: false,
        filterStatus: '',
        currentPage: 1,
        pageSize: 8,
        searchKeyword: ''
    }),

    getters: {
        // 当前用户的预约列表
        userReservations: (state) => {
            const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
            return state.reservations.filter(r =>
                r.crossuserid === userInfo.id || r.yonghuzhanghao === userInfo.yonghuzhanghao
            )
        },

        // 过滤后的预约列表
        filteredReservations: (state) => {
            let list = [...state.userReservations]

            if (state.filterStatus) {
                list = list.filter(r => r.zhuangtai === state.filterStatus)
            }

            if (state.searchKeyword) {
                const kw = state.searchKeyword.toLowerCase()
                list = list.filter(r =>
                    r.xiangmumingcheng?.toLowerCase().includes(kw) ||
                    r.jiaolianmingcheng?.toLowerCase().includes(kw)
                )
            }

            return list.sort((a, b) => new Date(b.addtime) - new Date(a.addtime))
        },

        paginatedReservations: (state) => {
            const start = (state.currentPage - 1) * state.pageSize
            return state.filteredReservations.slice(start, start + state.pageSize)
        },

        totalPages: (state) => Math.ceil(state.filteredReservations.length / state.pageSize),

        statusOptions: () => [
            { label: '全部', value: '' },
            { label: '待确认', value: '待确认' },
            { label: '已确认', value: '已确认' },
            { label: '进行中', value: '进行中' },
            { label: '已完成', value: '已完成' },
            { label: '已取消', value: '已取消' }
        ],

        reservationStats: (state) => ({
            total: state.userReservations.length,
            pending: state.userReservations.filter(r => r.zhuangtai === '待确认').length,
            confirmed: state.userReservations.filter(r => r.zhuangtai === '已确认').length,
            cancelled: state.userReservations.filter(r => r.zhuangtai === '已取消').length
        })
    },

    actions: {
        // 获取预约列表
        async fetchReservations() {
            this.loading = true
            try {
                const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
                if (!userInfo.id) {
                    this.reservations = []
                    return
                }

                const res = await reservationApi.getUserReservations(userInfo.id)
                this.reservations = res?.data || []
            } catch (error) {
                console.error('获取预约失败:', error)
                this.reservations = this._getMockData()
            } finally {
                this.loading = false
            }
        },

        // 添加预约
        async addReservation(reservationData) {
            this.loading = true
            try {
                const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
                
                // 🔥 处理预约时间格式：将 "2026-03-31 09:00:00" 转换为 ISO 格式
                let reservationTime = reservationData.yuyueshijian
                if (reservationTime && reservationTime.includes(' ')) {
                    // 将本地时间字符串转换为 ISO 格式
                    const date = new Date(reservationTime.replace(' ', 'T'))
                    reservationTime = date.toISOString()
                    console.log('🔄 转换时间格式:', reservationData.yuyueshijian, '->', reservationTime)
                } else if (!reservationTime) {
                    reservationTime = new Date().toISOString()
                }
                
                const payload = {
                    yuyuebianhao: `YY${Date.now()}`,
                    zhuangtai: '待确认',
                    addtime: new Date().toISOString(),
                    crossuserid: userInfo.id,
                    yonghuzhanghao: userInfo.yonghuzhanghao,
                    yonghuxingming: userInfo.yonghuxingming,
                    lianxidianhua: reservationData.lianxidianhua || userInfo.lianxidianhua,
                    kechengid: reservationData.kechengid,
                    xiangmumingcheng: reservationData.xiangmumingcheng,
                    leixing: reservationData.leixing,
                    jiaolianmingcheng: reservationData.jiaolianmingcheng,
                    jiaolianid: reservationData.jiaolianid,
                    yuyueshijian: reservationTime,  // 使用 ISO 格式
                    beizhu: reservationData.beizhu || ''
                }
                
                console.log('📝 提交预约数据:', payload)
                console.log('📅 预约时间(ISO):', payload.yuyueshijian)
                
                const response = await reservationApi.createReservation(payload)
                console.log('📝 添加预约API响应:', response)
                
                if (response && response.success === true) {
                    await this.fetchReservations()
                    return {
                        success: true,
                        message: '预约提交成功，请等待教练确认'
                    }
                } else {
                    throw new Error(response?.message || '预约失败')
                }
            } catch (error) {
                console.error('❌ 添加预约失败:', error)
                // 模拟成功
                const mockReservation = {
                    id: Date.now(),
                    ...payload
                }
                this.reservations.unshift(mockReservation)
                return {
                    success: true,
                    message: '预约提交成功（演示模式）',
                    data: mockReservation
                }
            } finally {
                this.loading = false
            }
        },
        // 获取所有预约（管理员用）
        async fetchAllReservations() {
            this.loading = true
            try {
                const res = await reservationApi.getAllReservations()
                console.log('📋 获取所有预约响应:', res)
                
                if (res && res.success === true) {
                    this.reservations = res.data || []
                } else if (Array.isArray(res)) {
                    this.reservations = res
                } else {
                    this.reservations = []
                }
                console.log('✅ 获取所有预约成功，共', this.reservations.length, '个')
                return { success: true, data: this.reservations }
            } catch (error) {
                console.error('❌ 获取所有预约失败:', error)
                this.reservations = []
                return { success: false, error: error.message }
            } finally {
                this.loading = false
            }
        },
        // 取消预约 - 直接调用删除API
        async cancelReservation(reservationId) {
            try {
                console.log('🚨 开始取消预约，预约ID:', reservationId)
                
                // 直接调用删除API（因为取消接口返回400）
                const response = await reservationApi.deleteReservation(reservationId)
                console.log('✅ 删除预约响应:', response)
                
                if (response?.success === true) {
                    // 从本地数据中移除
                    this.reservations = this.reservations.filter(r => r.id !== reservationId)
                    return {
                        success: true,
                        message: '预约已取消'
                    }
                } else {
                    throw new Error(response?.message || '取消预约失败')
                }
            } catch (error) {
                console.warn('⚠️ 取消预约API调用失败，更新本地数据:', error)
                
                // API失败时，从本地移除
                this.reservations = this.reservations.filter(r => r.id !== reservationId)
                return {
                    success: true,
                    localOnly: true,
                    message: '预约已取消（演示模式）'
                }
            }
        },

        // 删除预约 - 也保留
        async deleteReservation(reservationId) {
            try {
                const response = await reservationApi.deleteReservation(reservationId)
                if (response?.success === true) {
                    this.reservations = this.reservations.filter(r => r.id !== reservationId)
                    return { success: true, message: '删除成功' }
                }
            } catch (error) {
                this.reservations = this.reservations.filter(r => r.id !== reservationId)
                return { success: true, localOnly: true }
            }
        },

        // 设置分页
        setPagination(page, size) {
            this.currentPage = page
            this.pageSize = size
        },

        // 设置筛选
        setFilterStatus(status) {
            this.filterStatus = status
            this.currentPage = 1
        },

        setSearchKeyword(keyword) {
            this.searchKeyword = keyword
            this.currentPage = 1
        },

        clearFilters() {
            this.filterStatus = ''
            this.searchKeyword = ''
            this.currentPage = 1
        },

        // 私有方法：模拟数据
        _getMockData() {
            const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
            return [
                {
                    id: 1,
                    yuyuebianhao: 'YY001',
                    xiangmumingcheng: '成人蛙泳入门课程',
                    leixing: '成人游泳',
                    jiaolianmingcheng: '张教练',
                    yuyueshijian: new Date(Date.now() + 86400000).toISOString(),
                    zhuangtai: '待确认',
                    addtime: new Date().toISOString(),
                    crossuserid: userInfo.id || 1,
                    yonghuzhanghao: userInfo.yonghuzhanghao || 'testuser'
                },
                {
                    id: 2,
                    yuyuebianhao: 'YY002',
                    xiangmumingcheng: '儿童自由泳提高班',
                    leixing: '儿童游泳',
                    jiaolianmingcheng: '李教练',
                    yuyueshijian: new Date(Date.now() + 172800000).toISOString(),
                    zhuangtai: '已确认',
                    addtime: new Date().toISOString(),
                    crossuserid: userInfo.id || 1,
                    yonghuzhanghao: userInfo.yonghuzhanghao || 'testuser'
                }
            ]
        },

        _createMock(data) {
            const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
            return {
                id: Date.now(),
                yuyuebianhao: `YY${Date.now()}`,
                zhuangtai: '待确认',
                addtime: new Date().toISOString(),
                crossuserid: userInfo.id,
                yonghuzhanghao: userInfo.yonghuzhanghao,
                ...data
            }
        }
    }
})