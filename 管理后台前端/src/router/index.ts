import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'system/users',
        name: 'UserManagement',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/roles',
        name: 'RoleManagement',
        component: () => import('@/views/system/Role.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/menus',
        name: 'MenuManagement',
        component: () => import('@/views/system/Menu.vue'),
        meta: { title: '菜单管理' }
      },
      {
        path: 'system/operation-logs',
        name: 'OperationLog',
        component: () => import('@/views/system/OperationLog.vue'),
        meta: { title: '操作日志' }
      },
      {
        path: 'system/login-logs',
        name: 'LoginLog',
        component: () => import('@/views/system/LoginLog.vue'),
        meta: { title: '登录日志' }
      },
      {
        path: 'erp/products',
        name: 'ProductManagement',
        component: () => import('@/views/erp/Product.vue'),
        meta: { title: '产品档案' }
      },
      {
        path: 'erp/materials',
        name: 'MaterialManagement',
        component: () => import('@/views/erp/Material.vue'),
        meta: { title: '物料主数据' }
      },
      {
        path: 'erp/boms',
        name: 'BomManagement',
        component: () => import('@/views/erp/Bom.vue'),
        meta: { title: 'BOM管理' }
      },
      {
        path: 'erp/bom-versions',
        name: 'BomVersionManagement',
        component: () => import('@/views/erp/BomVersion.vue'),
        meta: { title: 'BOM版本管理' }
      },
      {
        path: 'erp/alternative-materials',
        name: 'AlternativeMaterialManagement',
        component: () => import('@/views/erp/AlternativeMaterial.vue'),
        meta: { title: '替代料管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')
  
  if (to.path === '/login') {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
