import { defineStore } from 'pinia'
import { coachApi } from '@/api/coach'

export const useCoachStore = defineStore('coach', {
    state: () => ({
        coaches: [],
        currentCoach: null,
        loading: false,
        searchKeyword: '',
        filterGender: '',
        filterSpecialty: ''
    }),

    getters: {
        // 过滤后的教练列表
        filteredCoaches: (state) => {
            let coaches = [...state.coaches]

            // 搜索过滤
            if (state.searchKeyword) {
                const keyword = state.searchKeyword.toLowerCase()
                coaches = coaches.filter(coach =>
                    coach.jiaolianxingming.toLowerCase().includes(keyword) ||
                    coach.jianjie?.toLowerCase().includes(keyword) ||
                    coach.zhuanye?.toLowerCase().includes(keyword)
                )
            }

            // 性别过滤
            if (state.filterGender) {
                coaches = coaches.filter(coach => coach.xingbie === state.filterGender)
            }

            // 专业过滤
            if (state.filterSpecialty) {
                coaches = coaches.filter(coach =>
                    coach.zhuanye?.includes(state.filterSpecialty)
                )
            }

            return coaches
        },

        // 教练性别选项
        genderOptions: () => ['男', '女'],

        // 教练专业领域
        specialtyOptions: (state) => {
            const specialties = [...new Set(state.coaches.map(coach => coach.zhuanye).filter(Boolean))]
            return specialties.sort()
        }
    },

    actions: {
        // 获取教练列表
        async fetchCoaches() {
            this.loading = true
            try {
                const response = await coachApi.getCoaches()
                if (Array.isArray(response)) {
                    this.coaches = response
                } else {
                    this.coaches = []
                }
                console.log('✅ 获取教练列表成功:', this.coaches.length, '个教练')
            } catch (error) {
                console.error('❌ 获取教练列表失败:', error)
                this.coaches = this.getMockCoaches() // 使用模拟数据
            } finally {
                this.loading = false
            }
        },

        // 获取教练详情
        async fetchCoachById(id) {
            try {
                const response = await coachApi.getCoachById(id)
                this.currentCoach = response
                return response
            } catch (error) {
                console.error('❌ 获取教练详情失败:', error)
                // 从本地数据中查找
                this.currentCoach = this.coaches.find(coach => coach.id === id) || null
                return this.currentCoach
            }
        },

        // 搜索教练
        async searchCoaches(keyword) {
            this.searchKeyword = keyword
            if (!keyword.trim()) {
                await this.fetchCoaches()
                return
            }

            try {
                const response = await coachApi.searchCoaches(keyword)
                if (Array.isArray(response)) {
                    this.coaches = response
                }
            } catch (error) {
                console.error('❌ 搜索教练失败:', error)
                // 使用本地搜索作为备选
                await this.fetchCoaches()
            }
        },

        // 按性别筛选
        async filterByGender(gender) {
            this.filterGender = gender
            if (!gender) {
                await this.fetchCoaches()
                return
            }

            try {
                const response = await coachApi.getCoachesByGender(gender)
                if (Array.isArray(response)) {
                    this.coaches = response
                }
            } catch (error) {
                console.error('❌ 按性别筛选教练失败:', error)
                await this.fetchCoaches()
            }
        },

        //模拟教练数据（当后端不可用时使用）
        getMockCoaches() {
            return [
                {
                    id: 1,
                    jiaolianxingming: '张教练',
                    zhaopian: '/images/coach1.jpg',
                    xingbie: '男',
                    lianxidianhua: '13800138001',
                    jianjie: '国家级游泳教练，拥有10年教学经验，擅长成人游泳和竞技游泳培训。培养过多名省级游泳冠军。',
                    zhuanye: '竞技游泳',
                    addtime: '2024-01-15T08:00:00'
                },
                {
                    id: 2,
                    jiaolianxingming: '李教练',
                    zhaopian: '/images/coach2.jpg',
                    xingbie: '女',
                    lianxidianhua: '13800138002',
                    jianjie: '专业儿童游泳教练，持有儿童心理学证书。教学耐心细致，深受家长和孩子们的喜爱。',
                    zhuanye: '儿童游泳',
                    addtime: '2024-02-20T10:30:00'
                },
                {
                    id: 3,
                    jiaolianxingming: '王教练',
                    zhaopian: '/images/coach3.jpg',
                    xingbie: '男',
                    lianxidianhua: '13800138003',
                    jianjie: '健身游泳专家，专注于成人健身游泳和康复训练。拥有运动康复学背景。',
                    zhuanye: '健身游泳',
                    addtime: '2024-03-10T14:15:00'
                },
                {
                    id: 4,
                    jiaolianxingming: '赵教练',
                    zhaopian: '/images/coach4.jpg',
                    xingbie: '女',
                    lianxidianhua: '13800138004',
                    jianjie: '前省级游泳运动员，退役后从事教练工作。擅长自由泳和蝶泳技术指导。',
                    zhuanye: '竞技游泳',
                    addtime: '2024-03-25T09:45:00'
                },
                {
                    id: 5,
                    jiaolianxingming: '刘教练',
                    zhaopian: '/images/coach5.jpg',
                    xingbie: '女',
                    lianxidianhua: '13800138005',
                    jianjie: '孕妇游泳保健专家，持有孕产期运动指导证书。帮助众多准妈妈安全进行水中运动。',
                    zhuanye: '康复游泳',
                    addtime: '2024-04-05T16:20:00'
                },
                {
                    id: 6,
                    jiaolianxingming: '陈教练',
                    zhaopian: '/images/coach6.jpg',
                    xingbie: '男',
                    lianxidianhua: '13800138006',
                    jianjie: '老年人水中健身指导专家，专注于低冲击水中运动，改善老年人关节健康。',
                    zhuanye: '康复游泳',
                    addtime: '2024-04-18T11:10:00'
                },
                {
                    id: 7,
                    jiaolianxingming: '杨教练',
                    zhaopian: '/images/coach7.jpg',
                    xingbie: '男',
                    lianxidianhua: '13800138007',
                    jianjie: '游泳救生员培训师，持有国际救生员证书。专注于游泳安全和水上救援培训。',
                    zhuanye: '安全培训',
                    addtime: '2024-05-12T13:30:00'
                },
                {
                    id: 8,
                    jiaolianxingming: '黄教练',
                    zhaopian: '/images/coach8.jpg',
                    xingbie: '女',
                    lianxidianhua: '13800138008',
                    jianjie: '青少年游泳培训专家，善于激发孩子的游泳兴趣，培养正确的游泳习惯。',
                    zhuanye: '儿童游泳',
                    addtime: '2024-05-28T15:45:00'
                }
            ]
        }
    }
})