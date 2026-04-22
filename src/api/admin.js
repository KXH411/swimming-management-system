import request from './request'

export const adminApi = {
    // 用户管理API
    getUsers: () => request.get('/user/list'),
    getUserById: (id) => request.get(`/user/${id}`),
    addUser: (data) => request.post('/user/register', data),
    updateUser: (data) => request.put('/user/update', data),
    deleteUser: (id) => request.delete(`/user/delete/${id}`), // 使用新的删除接口
    resetUserPassword: (id) => request.post(`/user/reset-password/${id}`),

    // 教练管理API
    getCoaches: () => request.get('/coach/list'),
    addCoach: (data) => request.post('/coach/add', data),
    updateCoach: (data) => request.put('/coach/update', data),
    deleteCoach: (id) => request.delete(`/coach/delete/${id}`),

    // 课程管理API
    getCourses: () => request.get('/course/list'),
    getCourseById: (id) => request.get(`/course/${id}`),
    addCourse: (data) => request.post('/course/add', data),
    updateCourse: (data) => request.put(`/course/${data.id}`, data), // 使用你的接口路径
    deleteCourse: (id) => request.delete(`/course/${id}`),
    getCoursesByType: (type) => request.get(`/course/type/${type}`),
    searchCourses: (keyword) => request.get(`/course/search?keyword=${keyword}`),
}