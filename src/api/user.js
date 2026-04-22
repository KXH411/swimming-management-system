import request from './request'

export const userApi = {
    // 用户注册
    register: (data) => request.post('/user/register', data),

    // 用户登录
    login: (data) => request.post('/user/login', data),

    // 获取用户列表
    getUsers: () => request.get('/user/list'),

    // 根据ID获取用户
    getUserById: (id) => request.get(`/user/${id}`),

    // 更新用户信息
    updateUser: (id, data) => request.put(`/user/${id}`, data),

    // 删除用户
    deleteUser: (id) => request.delete(`/user/${id}`),

    // 修改密码
    changePassword: (data) => request.post('/user/change-password', data),

    // 上传头像
    uploadAvatar: (formData) => request.post('/user/upload-avatar', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }),

    // 获取用户预约记录
    getUserReservations: (userId) => request.get(`/user/${userId}/reservations`),

    // 获取用户收藏
    getUserFavorites: (userId) => request.get(`/user/${userId}/favorites`)
}