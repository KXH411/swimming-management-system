// 检查用户权限
export const checkPermission = (requiredRole) => {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    return userInfo.role === requiredRole || userInfo.role === '超级管理员'
}

// 格式化日期
export const formatDate = (dateString) => {
    if (!dateString) return ''
    const date = new Date(dateString)
    return date.toLocaleString('zh-CN')
}