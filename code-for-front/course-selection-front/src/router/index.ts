import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
    {
      path: '/',
      component: Layout,
      redirect: '/login',
      children: [
        {
          path: 'admin',
          name: 'Admin',
          component: () => import('../views/admin/AdminDashboard.vue'),
          meta: { role: 'admin' }
        },
        {
          path: 'student',
          name: 'Student',
          component: () => import('../views/student/StudentDashboard.vue'),
          meta: { role: 'student' }
        },
        {
          path: 'teacher',
          name: 'Teacher',
          component: () => import('../views/teacher/TeacherDashboard.vue'),
          meta: { role: 'teacher' }
        },
        {
          path: 'my-courses',
          name: 'MyCourses',
          component: () => import('../views/teacher/MyCourses.vue'),
          meta: { role: 'teacher' }
        },
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../views/Profile.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  if (to.path === '/login') {
    next()
  } else if (!userStr) {
    next('/login')
  } else {
    const user = JSON.parse(userStr)
    // 简单的权限校验
    if (to.meta.role && to.meta.role !== user.role) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
