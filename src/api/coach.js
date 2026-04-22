import request from './request'

export const coachApi = {
    // 获取教练列表
    getCoaches: () => request.get('/coach/list'),

    // 根据ID获取教练
    getCoachById: (id) => request.get(`/coach/${id}`),

    // 添加教练
    addCoach: (data) => request.post('/coach/add', data),

    // 更新教练
    updateCoach: (id, data) => request.put(`/coach/${id}`, data),

    // 删除教练
    deleteCoach: (id) => request.delete(`/coach/${id}`),

    // 按性别查询教练
    getCoachesByGender: (gender) => request.get(`/coach/gender/${gender}`),

    // 按专业查询教练
    getCoachesBySpecialty: (specialty) => request.get(`/coach/specialty/${specialty}`),

    // 搜索教练
    searchCoaches: (keyword) => request.get(`/coach/search?keyword=${keyword}`)
}