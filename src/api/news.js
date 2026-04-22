import request from './request'

export const newsApi = {
    // 获取新闻列表
    getNewsList: () => request.get('/news/list'),

    // 根据ID获取新闻
    getNewsById: (id) => request.get(`/news/${id}`),

    // 添加新闻
    addNews: (data) => request.post('/news/add', data),

    // 更新新闻
    updateNews: (id, data) => request.put(`/news/${id}`, data),

    // 搜索新闻
    searchNews: (keyword) => request.get(`/news/search?keyword=${keyword}`),

    // 删除新闻
    deleteNews: (id) => request.delete(`/news/${id}`)
}