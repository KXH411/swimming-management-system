import request from './request'

export const courseApi = {
    // 获取课程列表
    getCourses: () => request.get('/course/list'),

    // 根据ID获取课程
    getCourseById: (id) => request.get(`/course/${id}`),

    // 根据类型获取课程
    getCoursesByType: (type) => request.get(`/course/type/${type}`),

    // 搜索课程
    searchCourses: (keyword) => request.get(`/course/search?keyword=${keyword}`),

    // 获取热门课程
    getPopularCourses: () => request.get('/course/popular'),

    // 获取最新课程
    getLatestCourses: () => request.get('/course/latest'),

    // 创建课程
    createCourse: (data) => request.post('/course/add', data),

    // 更新课程
    updateCourse: (id, data) => request.put(`/course/${id}`, data),

    // 删除课程
    deleteCourse: (id) => request.delete(`/course/${id}`),

    // 收藏课程
    favoriteCourse: (id) => request.put(`/course/${id}/favorite`),

    // 取消收藏
    unfavoriteCourse: (id) => request.put(`/course/${id}/unfavorite`)
}