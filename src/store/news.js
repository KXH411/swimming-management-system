// src/store/news.js
import { defineStore } from 'pinia'
import { newsApi } from '@/api/news'

export const useNewsStore = defineStore('news', {
    state: () => ({
        newsList: [],
        currentNews: null,
        loading: false,
        searchKeyword: '',
        currentPage: 1,        // 新增：当前页码
        pageSize: 10           // 新增：每页大小
    }),

    getters: {
        // 搜索过滤后的新闻列表
        filteredNews: (state) => {
            if (!state.searchKeyword) {
                return state.newsList
            }
            const keyword = state.searchKeyword.toLowerCase()
            return state.newsList.filter(news =>
                news.title?.toLowerCase().includes(keyword) ||
                news.introduction?.toLowerCase().includes(keyword) ||
                news.content?.toLowerCase().includes(keyword)
            )
        },

        // 新增：分页后的新闻
        paginatedNews: (state) => {
            const start = (state.currentPage - 1) * state.pageSize
            const end = start + state.pageSize
            return state.filteredNews.slice(start, end)
        },

        // 新增：总页数
        totalPages: (state) => Math.ceil(state.filteredNews.length / state.pageSize)
    },

    actions: {
        // 获取新闻列表
        async fetchNewsList() {
            this.loading = true
            try {
                console.log('📰 开始获取新闻列表...')
                const response = await newsApi.getNewsList()
                console.log('✅ 新闻API响应:', response)

                if (Array.isArray(response)) {
                    this.newsList = response
                    console.log(`✅ 获取新闻列表成功: ${this.newsList.length} 条新闻`)
                } else {
                    console.warn('⚠️ API返回数据不是数组，使用模拟数据')
                    this.newsList = this.getMockNews()
                }
            } catch (error) {
                console.error('❌ 获取新闻列表失败:', error)
                console.log('🔄 使用模拟数据作为备选')
                this.newsList = this.getMockNews()
            } finally {
                this.loading = false
            }
        },

        // 获取新闻详情
        async fetchNewsById(id) {
            try {
                console.log(`📰 获取新闻详情 ID: ${id}`)
                const response = await newsApi.getNewsById(id)
                this.currentNews = response
                return response
            } catch (error) {
                console.error('❌ 获取新闻详情失败:', error)
                // 从本地数据中查找
                this.currentNews = this.newsList.find(news => news.id === id) || null
                return this.currentNews
            }
        },

        // 添加新闻
        async addNews(newsData) {
            try {
                console.log('📰 添加新闻:', newsData)
                const response = await newsApi.addNews(newsData)
                console.log('✅ 添加新闻响应:', response)

                if (response.success) {
                    await this.fetchNewsList() // 刷新列表
                    return response
                } else {
                    throw new Error(response.message || '添加新闻失败')
                }
            } catch (error) {
                console.error('❌ 添加新闻失败:', error)
                throw error
            }
        },

        // 更新新闻
        async updateNews(id, newsData) {
            try {
                console.log(`📰 更新新闻 ID: ${id}`, newsData)
                const response = await newsApi.updateNews(id, newsData)
                console.log('✅ 更新新闻响应:', response)

                if (response.success) {
                    await this.fetchNewsList() // 刷新列表
                    return response
                } else {
                    throw new Error(response.message || '更新新闻失败')
                }
            } catch (error) {
                console.error('❌ 更新新闻失败:', error)
                throw error
            }
        },

        // 删除新闻
        async deleteNews(id) {
            try {
                console.log(`📰 删除新闻 ID: ${id}`)
                const response = await newsApi.deleteNews(id)
                console.log('✅ 删除新闻响应:', response)

                if (response.success) {
                    await this.fetchNewsList() // 刷新列表
                    return response
                } else {
                    throw new Error(response.message || '删除新闻失败')
                }
            } catch (error) {
                console.error('❌ 删除新闻失败:', error)
                throw error
            }
        },

        // 搜索新闻
        async searchNews(keyword) {
            this.searchKeyword = keyword
            console.log(`🔍 搜索新闻: ${keyword}`)

            if (!keyword.trim()) {
                console.log('🔄 关键词为空，获取全部新闻')
                await this.fetchNewsList()
                return
            }

            try {
                const response = await newsApi.searchNews(keyword)
                console.log('✅ 搜索新闻响应:', response)

                if (Array.isArray(response)) {
                    this.newsList = response
                } else {
                    console.warn('⚠️ 搜索API返回数据不是数组')
                    await this.fetchNewsList()
                }
            } catch (error) {
                console.error('❌ 搜索新闻失败:', error)
                console.log('🔄 使用本地搜索作为备选')
                await this.fetchNewsList()
            }
        },

        // 🔥 新增：设置分页
        setPagination(page, size) {
            this.currentPage = page
            this.pageSize = size
        },

        // 🔥 新增：设置搜索关键词
        setSearchKeyword(keyword) {
            this.searchKeyword = keyword
            this.currentPage = 1 // 重置到第一页
        },

        // 🔥 新增：清除筛选条件
        clearFilters() {
            this.searchKeyword = ''
            this.currentPage = 1
        },

        // 模拟数据（当后端不可用时使用）
        getMockNews() {
            console.log('🔄 使用模拟新闻数据')
            return [
                {
                    id: 1,
                    title: '游泳馆夏季开放通知',
                    introduction: '夏季游泳馆开放时间调整通知',
                    content: '尊敬的会员：夏季游泳馆开放时间将进行调整，具体安排如下：\n\n周一至周五：06:00-22:00\n周末及节假日：06:00-23:00\n\n请各位会员合理安排游泳时间，感谢您的理解与支持！',
                    image: '/images/news1.jpg',
                    addtime: '2024-09-24T10:00:00'
                },
                {
                    id: 2,
                    title: '游泳安全知识普及',
                    introduction: '学习游泳安全知识，享受健康运动',
                    content: '为了确保您的游泳安全，我们特别整理了以下游泳安全知识：\n\n1. 游泳前请做好充分的热身运动\n2. 请勿在饱食或饥饿状态下游泳\n3. 儿童游泳必须有成人陪同\n4. 如遇身体不适请立即停止游泳\n\n安全第一，快乐游泳！',
                    image: '/images/news2.jpg',
                    addtime: '2024-09-23T15:30:00'
                },
                {
                    id: 3,
                    title: '新课程上线通知',
                    introduction: '新增竞技游泳课程，欢迎报名',
                    content: '我们很高兴地宣布，游泳馆新增竞技游泳课程！\n\n课程特色：\n- 专业教练团队指导\n- 个性化训练计划\n- 比赛技巧培训\n- 体能训练指导\n\n欢迎有兴趣提升游泳技能的会员报名参加！',
                    image: '/images/news3.jpg',
                    addtime: '2024-09-22T09:15:00'
                }
            ]
        }
    }
})