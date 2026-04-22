import request from './request'

export const reservationApi = {
    // 获取用户的预约记录
    getUserReservations: (userId) => request.get(`/user/${userId}/reservations`),

        // 🔥 获取所有预约记录（管理员用）
    getAllReservations: () => request.get('/reservations/list'),
    
    // 创建预约
    createReservation: (data) => request.post('/reservations', data),
    
    // 取消预约
    cancelReservation: (reservationId) => request.delete(`/reservations/${reservationId}`),
    
    // 确认预约
    confirmReservation: (reservationId) => request.put(`/reservations/${reservationId}/confirm`),
    
    // 删除预约
    deleteReservation: (reservationId) => request.delete(`/reservations/${reservationId}`),
    
    // 获取预约详情
    getReservationById: (reservationId) => request.get(`/reservations/${reservationId}`)
}