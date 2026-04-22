import request from './request'

// 确保正确导出
export const favoriteApi = {
    // 获取我的收藏列表
    getMyFavorites: () => request.get('/user/favorites'),
    
    // 添加收藏
    addFavorite: (courseId) => request.post(`/favorites/${courseId}`),
    
    // 取消收藏
    removeFavorite: (courseId) => request.delete(`/favorites/${courseId}`),
    
    // 检查是否已收藏
    checkFavorite: (courseId) => request.get(`/favorites/check/${courseId}`)
}

// 添加默认导出
export default favoriteApi