import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/DashboardView.vue')
        },
        {
          path: 'projects/:id',
          name: 'ProjectDetail',
          component: () => import('@/views/ProjectDetailView.vue')
        },
        {
          path: 'skills/:id',
          name: 'SkillEditor',
          component: () => import('@/views/SkillEditorView.vue')
        },
        {
          path: 'skills/:id/versions/:vid1/diff/:vid2',
          name: 'VersionDiff',
          component: () => import('@/views/VersionDiffView.vue')
        },
        {
          path: 'skills/:id/variants/compare',
          name: 'VariantCompare',
          component: () => import('@/views/VariantCompareView.vue')
        },
        {
          path: 'test-runs/:id',
          name: 'TestRunDetail',
          component: () => import('@/views/TestRunDetailView.vue')
        },
        {
          path: 'marketplace',
          name: 'Marketplace',
          component: () => import('@/views/MarketplaceView.vue')
        },
        {
          path: 'admin',
          name: 'Admin',
          component: () => import('@/views/AdminView.vue'),
          meta: { requiresAdmin: true }
        }
      ]
    }
  ]
})

router.beforeEach(async (to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.matched.some((r) => r.meta.requiresAuth) && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else if (to.matched.some((r) => r.meta.requiresAdmin)) {
    const auth = useAuthStore()
    if (!auth.user) await auth.fetchMe()
    if (auth.isAdmin) {
      next()
    } else {
      next('/')
    }
  } else {
    next()
  }
})

export default router
