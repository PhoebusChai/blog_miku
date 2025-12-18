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
      component: () => import('@/views/Admin.vue')
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
