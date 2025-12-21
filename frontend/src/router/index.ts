import { createRouter, createWebHistory } from 'vue-router'

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
      component: () => import('@/views/Register.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/Admin.vue'),
      redirect: '/admin/dashboard',
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

export default router
