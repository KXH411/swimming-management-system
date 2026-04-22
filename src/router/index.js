import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
    {
        path: '/',
        redirect: '/Login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue'),
        meta: {
            requiresAuth: false,
            hideLayout: true,
            title: '用户登录 - 游泳馆管理系统'
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue'),
        meta: {
            requiresAuth: false,
            hideLayout: true,
            title: '用户注册 - 游泳馆管理系统'
        }
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: {
            requiresAuth: true,
            title: '首页 - 游泳馆管理系统'
        }
    },
    {
        path: '/courses',
        name: 'Courses',
        component: () => import('@/views/Courses.vue'),
        meta: {
            requiresAuth: true,
            title: '游泳课程 - 游泳馆管理系统'
        }
    },
    {
        path: '/coaches',
        name: 'Coaches',
        component: () => import('@/views/Coaches.vue'),
        meta: {
            requiresAuth: true,
            title: '教练信息 - 游泳馆管理系统'
        }
    },
    {
        path: '/news',
        name: 'News',
        component: () => import('@/views/News.vue'),
        meta: {
            requiresAuth: true,
            title: '新闻资讯 - 游泳馆管理系统'
        }
    },
        {
        path: '/news-detail/:id',
        name: 'NewsDetail',
        component: () => import('@/views/NewsDetail.vue'),
        meta: { requiresAuth: false, title: '新闻详情 - 游泳馆管理系统' }
    },
    {
        path: '/reservations',
        name: 'Reservations',
        component: () => import('@/views/Reservations.vue'),
        meta: {
            requiresAuth: true,
            title: '我的预约 - 游泳馆管理系统'
        }
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: {
            requiresAuth: true,
            title: '个人中心 - 游泳馆管理系统'
        }
    },

    {
        path: '/course-detail/:id',
        name: 'CourseDetail',
        component: () => import('@/views/CourseDetail.vue'),
        meta: { requiresAuth: false, title: '课程详情 - 游泳馆管理系统' }
    },
    {
        path: '/api-test',
        name: 'ApiTest',
        component: () => import('@/views/ApiTest.vue'),
        meta: {
            requiresAuth: false,
            hideLayout: true,
            title: 'API测试 - 游泳馆管理系统'
        }
    },
    // 管理员路由
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('@/views/admin/AdminLayout.vue'),
        meta: {
            requiresAuth: true,
            requiresAdmin: true,
            title: '管理后台 - 游泳馆管理系统'
        },
        redirect: '/admin/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'AdminDashboard',
                component: () => import('@/views/admin/Dashboard.vue'),
                meta: {
                    title: '仪表板 - 管理后台',
                    requiresAdmin: true
                }
            },
            {
                path: 'users',
                name: 'AdminUsers',
                component: () => import('@/views/admin/UserManagement.vue'),
                meta: {
                    title: '用户管理 - 管理后台',
                    requiresAdmin: true
                }
            },
            {
                path: 'courses',
                name: 'AdminCourses',
                component: () => import('@/views/admin/CourseManagement.vue'),
                meta: {
                    title: '课程管理 - 管理后台',
                    requiresAdmin: true
                }
            },
            {
                path: 'coaches',
                name: 'AdminCoaches',
                component: () => import('@/views/admin/CoachManagement.vue'),
                meta: {
                    title: '教练管理 - 管理后台',
                    requiresAdmin: true
                }
            },
            {
                path: 'reservations',
                name: 'AdminReservations',
                component: () => import('@/views/admin/ReservationManagement.vue'),
                meta: {
                    title: '预约管理 - 管理后台',
                    requiresAdmin: true
                }
            },
            {
                path: 'news',
                name: 'AdminNews',
                component: () => import('@/views/admin/NewsManagement.vue'),
                meta: {
                    title: '新闻管理 - 管理后台',
                    requiresAdmin: true
                }
            }
        ]
    },
    // 404页面
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFound.vue'),
        meta: {
            requiresAuth: false,
            hideLayout: true,
            title: '页面未找到 - 游泳馆管理系统'
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const isAdmin = localStorage.getItem('isAdmin') === 'true'

    console.log('🛡️ 路由守卫检查:')
    console.log('  - 目标路由:', to.path)
    console.log('  - 是否有Token:', !!token)
    console.log('  - 用户信息:', userInfo)
    console.log('  - 是否是管理员:', isAdmin)

    // 设置页面标题
    if (to.meta.title) {
        document.title = to.meta.title
    }

    // 检查是否需要登录
    if (to.meta.requiresAuth && !token) {
        console.log('❌ 需要登录，重定向到登录页')
        ElMessage.warning('请先登录')
        next('/login')
        return
    }

    // 如果已登录但访问登录/注册页，重定向到首页
    if ((to.path === '/login' || to.path === '/register') && token) {
        console.log('✅ 已登录，重定向到首页')
        ElMessage.info('您已登录')
        next('/home')
        return
    }

    // 检查管理员权限
    if (to.meta.requiresAdmin && !isAdmin) {
        console.log('❌ 需要管理员权限')
        ElMessage.error('需要管理员权限')
        next('/home')
        return
    }

    // API测试页面只在开发环境显示
    if (to.path === '/api-test' && process.env.NODE_ENV === 'production') {
        console.log('❌ 生产环境禁止访问测试页面')
        next('/home')
        return
    }

    console.log('✅ 路由检查通过，允许访问')
    next()
})

// 路由后置钩子
router.afterEach((to, from) => {
    console.log(`✅ 路由跳转完成: ${from.path} -> ${to.path}`)
})

// 错误处理
router.onError((error) => {
    console.error('❌ 路由错误:', error)
    const errMsg = error.message || '路由加载失败'

    if (errMsg.includes('Failed to fetch dynamically imported module')) {
        ElMessage.error('页面加载失败，请刷新重试')
    } else if (errMsg.includes('Loading chunk')) {
        ElMessage.error('资源加载失败，请检查网络连接')
    }
})

export default router