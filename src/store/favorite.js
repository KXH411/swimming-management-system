import { defineStore } from 'pinia'
import request from '@/api/request'

export const useFavoriteStore = defineStore('favorite', {
    state: () => ({
        favorites: [],      // 收藏的课程列表
        favoriteIds: new Set(), // 收藏的课程ID集合
        loading: false
    }),
    
    getters: {
        favoriteCount: (state) => state.favorites.length,
        isFavorited: (state) => (courseId) => state.favoriteIds.has(courseId)
    },
    
    actions: {
        // 获取我的收藏列表 - 使用正确的后端接口
        async fetchMyFavorites() {
            this.loading = true
            try {
                const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
                if (!userInfo.id) {
                    console.log('用户未登录，跳过获取收藏')
                    this.favorites = []
                    return
                }
                
                console.log('📚 开始获取收藏列表，用户ID:', userInfo.id)
                // 使用正确的接口: GET /api/favorite/user/{userId}
                const response = await request.get(`/favorite/user/${userInfo.id}`)
                console.log('📚 获取收藏列表响应:', response)
                
                if (Array.isArray(response)) {
                    // 将后端返回的 Favorite 实体转换为前端需要的格式
                    this.favorites = response.map(item => ({
                        id: item.refid,           // 课程ID
                        favoriteId: item.id,       // 收藏记录ID
                        xiangmumingcheng: item.name,
                        leixing: item.type,
                        fengmian: item.picture,
                        jiaolianmingcheng: item.coachName || '教练',
                        jiage: item.price || 0,
                        addtime: item.addtime
                    }))
                } else {
                    this.favorites = []
                }
                
                // 更新收藏ID集合
                this._updateFavoriteIds()
                console.log('✅ 收藏列表加载成功，共', this.favorites.length, '个')
            } catch (error) {
                console.error('❌ 获取收藏列表失败:', error)
                this.favorites = []
                this.favoriteIds.clear()
            } finally {
                this.loading = false
            }
        },
        
        // 切换收藏状态 - 使用正确的后端接口
        async toggleFavorite(courseId, courseData) {
            try {
                const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
                if (!userInfo.id) {
                    throw new Error('请先登录')
                }
                
                const isCurrentlyFavorited = this.favoriteIds.has(courseId)
                console.log(`⭐ ${isCurrentlyFavorited ? '取消' : '添加'}收藏，课程ID:`, courseId)
                
                // 构建请求数据 - 匹配后端 Favorite 实体
                const requestData = {
                    userId: userInfo.id,
                    tableName: 'course',
                    name: courseData?.xiangmumingcheng || `课程${courseId}`,
                    picture: courseData?.fengmian || '',
                    type: courseData?.leixing || '游泳课程',
                    refId: courseId
                }
                
                console.log('📦 发送切换收藏请求:', requestData)
                // 使用正确的接口: POST /api/favorite/toggle
                const response = await request.post('/favorite/toggle', requestData)
                console.log('⭐ 切换收藏响应:', response)
                
                if (response && response.success === true) {
                    const isFavorited = response.isFavorited
                    
                    if (isFavorited) {
                        // 添加收藏成功
                        this.favoriteIds.add(courseId)
                        // 添加到列表
                        this.favorites.unshift({
                            id: courseId,
                            favoriteId: response.data?.id || Date.now(),
                            xiangmumingcheng: requestData.name,
                            leixing: requestData.type,
                            fengmian: requestData.picture,
                            jiaolianmingcheng: courseData?.jiaolianmingcheng || '教练',
                            jiage: courseData?.jiage || 0,
                            addtime: new Date().toISOString()
                        })
                        return { success: true, message: '收藏成功', isFavorited: true }
                    } else {
                        // 取消收藏成功
                        this.favoriteIds.delete(courseId)
                        this.favorites = this.favorites.filter(f => f.id !== courseId)
                        return { success: true, message: '已取消收藏', isFavorited: false }
                    }
                } else {
                    throw new Error(response?.message || '操作失败')
                }
            } catch (error) {
                console.error('❌ 切换收藏失败:', error)
                
                // 降级：本地更新
                const isCurrentlyFavorited = this.favoriteIds.has(courseId)
                if (isCurrentlyFavorited) {
                    this.favoriteIds.delete(courseId)
                    this.favorites = this.favorites.filter(f => f.id !== courseId)
                    return { success: true, localOnly: true, message: '已取消收藏', isFavorited: false }
                } else {
                    this.favoriteIds.add(courseId)
                    this.favorites.unshift({
                        id: courseId,
                        favoriteId: Date.now(),
                        xiangmumingcheng: courseData?.xiangmumingcheng || `课程${courseId}`,
                        leixing: courseData?.leixing || '游泳课程',
                        fengmian: courseData?.fengmian || '',
                        jiaolianmingcheng: courseData?.jiaolianmingcheng || '教练',
                        jiage: courseData?.jiage || 0,
                        addtime: new Date().toISOString()
                    })
                    return { success: true, localOnly: true, message: '收藏成功', isFavorited: true }
                }
            }
        },
        
        // 添加收藏（调用toggle）
        async addFavorite(courseId, courseData) {
            return await this.toggleFavorite(courseId, courseData)
        },
        
        // 取消收藏（调用toggle）
        async removeFavorite(courseId) {
            const course = this.favorites.find(f => f.id === courseId)
            const courseData = course ? {
                xiangmumingcheng: course.xiangmumingcheng,
                leixing: course.leixing,
                fengmian: course.fengmian,
                jiaolianmingcheng: course.jiaolianmingcheng,
                jiage: course.jiage
            } : null
            return await this.toggleFavorite(courseId, courseData)
        },
        
        // 更新收藏ID集合
        _updateFavoriteIds() {
            this.favoriteIds.clear()
            this.favorites.forEach(course => {
                if (course && course.id) {
                    this.favoriteIds.add(course.id)
                }
            })
        }
    }
})