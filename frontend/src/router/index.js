import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register'),
      meta: { requiresAuth: false }
    },
    {
      path: '/forgot-password',
      name: 'ForgotPassword',
      component: () => import('@/views/ForgotPassword'),
      meta: { requiresAuth: false }
    },
    {
      path: '/home',
      name: 'Home',
      component: () => import('@/views/Home'),
      meta: { requiresAuth: true }
    },
    {
      path: '/blog/:id',
      name: 'BlogDetail',
      component: () => import('@/views/BlogDetail'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/ProfilePage'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/:userId',
      name: 'UserProfile',
      component: () => import('@/views/UserProfile'),
      meta: { requiresAuth: true }
    },
    {
      path: '/blog-editor',
      name: 'BlogEditor',
      component: () => import('@/views/BlogEditor'),
      meta: { requiresAuth: true }
    },
    {
      path: '/blog-editor/:id',
      name: 'BlogEditorEdit',
      component: () => import('@/views/BlogEditor'),
      meta: { requiresAuth: true }
    },
    {
      path: '/category-management',
      name: 'CategoryManagement',
      component: () => import('@/views/CategoryManagement'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/tag-management',
      name: 'TagManagement',
      component: () => import('@/views/TagManagement'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/user-management',
      name: 'UserManagement',
      component: () => import('@/views/UserManagement'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/deleted-blogs',
      name: 'DeletedBlogList',
      component: () => import('@/views/DeletedBlogList'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else if (!requiresAuth && token && (to.path === '/login' || to.path === '/register')) {
    // 已登录，访问登录/注册页，跳转到首页
    next('/home')
  } else {
    next()
  }
})

export default router
