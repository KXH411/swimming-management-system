import { defineStore } from 'pinia'
import { courseApi } from '@/api/course'

export const useCourseStore = defineStore('course', {
    state: () => ({
        courses: [],
        currentCourse: null,
        loading: false,
        searchKeyword: '',
        filterType: '',
        sortBy: 'latest'
    }),

    getters: {
        // 过滤后的课程列表
        filteredCourses: (state) => {
            let courses = [...state.courses]

            // 搜索过滤
            if (state.searchKeyword) {
                courses = courses.filter(course =>
                    course.xiangmumingcheng.includes(state.searchKeyword) ||
                    course.kechengjianjie?.includes(state.searchKeyword)
                )
            }

            // 类型过滤
            if (state.filterType) {
                courses = courses.filter(course => course.leixing === state.filterType)
            }

            // 排序
            if (state.sortBy === 'price_asc') {
                courses.sort((a, b) => (a.jiage || 0) - (b.jiage || 0))
            } else if (state.sortBy === 'price_desc') {
                courses.sort((a, b) => (b.jiage || 0) - (a.jiage || 0))
            } else if (state.sortBy === 'latest') {
                courses.sort((a, b) => new Date(b.addtime) - new Date(a.addtime))
            }

            return courses
        },

        // 课程类型列表
        courseTypes: (state) => {
            const types = [...new Set(state.courses.map(course => course.leixing))]
            return types.filter(type => type).sort()
        }
    },

    actions: {
        // 获取课程列表
        async fetchCourses() {
            this.loading = true
            try {
                const response = await courseApi.getCourses()
                console.log('✅ 课程API响应:', response)

                // 🔥 修复：正确处理响应格式
                if (response.success && Array.isArray(response.data)) {
                    this.courses = response.data
                    console.log('✅ 获取课程列表成功:', this.courses.length, '个课程')
                } else {
                    console.warn('⚠️ 课程数据格式不正确，使用模拟数据')
                    this.courses = this.getMockCourses()
                }
            } catch (error) {
                console.error('❌ 获取课程列表失败:', error)
                this.courses = this.getMockCourses() // 使用模拟数据
            } finally {
                this.loading = false
            }
        },

        // 获取课程详情
        async fetchCourseById(id) {
            try {
                const response = await courseApi.getCourseById(id)
                // 🔥 修复：正确处理响应格式
                if (response.success) {
                    this.currentCourse = response.data
                    return response.data
                } else {
                    throw new Error(response.message || '获取课程详情失败')
                }
            } catch (error) {
                console.error('❌ 获取课程详情失败:', error)
                // 从本地数据中查找
                this.currentCourse = this.courses.find(course => course.id === id) || null
                return this.currentCourse
            }
        },

        // 搜索课程
        async searchCourses(keyword) {
            this.searchKeyword = keyword
            if (!keyword.trim()) {
                await this.fetchCourses()
                return
            }

            try {
                const response = await courseApi.searchCourses(keyword)
                // 🔥 修复：正确处理响应格式
                if (response.success && Array.isArray(response.data)) {
                    this.courses = response.data
                } else {
                    await this.fetchCourses()
                }
            } catch (error) {
                console.error('❌ 搜索课程失败:', error)
                // 使用本地搜索作为备选
                await this.fetchCourses()
            }
        },

        // 模拟课程数据（当后端不可用时使用）
        getMockCourses() {
            return [
                {
                    id: 1,
                    xiangmumingcheng: '成人蛙泳入门课程',
                    leixing: '成人游泳',
                    fengmian: '',
                    kechengjianjie: '适合零基础学员，学习蛙泳基本技巧和呼吸方法，掌握正确的游泳姿势',
                    jiaolianmingcheng: '张教练',
                    jiaolianid: 1,
                    shichang: 60,
                    jiage: 200,
                    storeupnum: 15,
                    addtime: '2024-09-20T10:00:00'
                },
                {
                    id: 2,
                    xiangmumingcheng: '儿童自由泳提高班',
                    leixing: '儿童游泳',
                    fengmian: '',
                    kechengjianjie: '针对有一定基础的儿童，提高自由泳技术和耐力，培养游泳兴趣',
                    jiaolianmingcheng: '李教练',
                    jiaolianid: 2,
                    shichang: 45,
                    jiage: 180,
                    storeupnum: 23,
                    addtime: '2024-09-18T14:30:00'
                },
                {
                    id: 3,
                    xiangmumingcheng: '健身游泳训练营',
                    leixing: '健身游泳',
                    fengmian: '',
                    kechengjianjie: '提高心肺功能，塑造身体线条，适合健身爱好者和减肥人群',
                    jiaolianmingcheng: '王教练',
                    jiaolianid: 3,
                    shichang: 90,
                    jiage: 300,
                    storeupnum: 8,
                    addtime: '2024-09-15T09:15:00'
                }
            ]
        }
    }
})