import { createRouter, createWebHistory } from 'vue-router'
import type { RouteLocationNormalized, NavigationGuardNext } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { showMessage } from '@/utils/message'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home.vue')
    },
    {
      path: '/article/:id',
      name: 'article',
      component: () => import('@/views/ArticleDetail.vue')
    },
    {
      path: '/archive',
      name: 'archive',
      component: () => import('@/views/Archive.vue')
    },
    {
      path: '/gallery',
      name: 'gallery',
      component: () => import('@/views/Gallery.vue')
    },
    {
      path: '/projects',
      name: 'projects',
      component: () => import('@/views/Projects.vue')
    },
    {
      path: '/guestbook',
      name: 'guestbook',
      component: () => import('@/views/Guestbook.vue')
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/About.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),
      meta: { guest: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/Admin.vue'),
      redirect: '/admin/dashboard',
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: 'dashboard',
          name: 'admin-dashboard',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'articles',
          name: 'admin-articles',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'tags',
          name: 'admin-tags',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'comments',
          name: 'admin-comments',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'projects',
          name: 'admin-projects',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'gallery',
          name: 'admin-gallery',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'links',
          name: 'admin-links',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'settings',
          name: 'admin-settings',
          component: () => import('@/views/Admin.vue')
        },
        {
          path: 'tools',
          name: 'admin-tools',
          component: () => import('@/views/Admin.vue')
        }
      ]
    },
    {
      path: '/error',
      name: 'error',
      component: () => import('@/views/Error.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFound.vue')
    }
  ]
})

// 路由守卫
router.beforeEach(async (
  to: RouteLocationNormalized,
  _from: RouteLocationNormalized,
  next: NavigationGuardNext
) => {
  const userStore = useUserStore()

  // 如果用户状态未初始化，先初始化
  if (!userStore.isLoggedIn && localStorage.getItem('token')) {
    try {
      await userStore.initUser()
    } catch {
      // 初始化失败，清除 token
      localStorage.removeItem('token')
    }
  }

  // 需要登录的页面
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn) {
      showMessage.warning('请先登录')
      return next({ name: 'home', query: { redirect: to.fullPath } })
    }

    // 需要管理员权限
    if (to.meta.requiresAdmin && !userStore.isAdmin()) {
      showMessage.error('无权访问该页面')
      return next({ name: 'home' })
    }
  }

  // 游客页面（已登录用户不能访问）
  if (to.meta.guest && userStore.isLoggedIn) {
    return next({ name: 'home' })
  }

  next()
})

export default router
