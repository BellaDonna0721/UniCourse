<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="logo">选课管理系统</div>
      <div class="user-info">
        <span class="user-name">{{ user.name }} ({{ roleName }})</span>
        <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          router
        >
          <!-- 管理员菜单 -->
          <template v-if="user.role === 'admin'">
            <el-menu-item index="/admin">
              <el-icon><User /></el-icon>
              <span>管理中心</span>
            </el-menu-item>
          </template>

          <!-- 学生菜单 -->
          <template v-else-if="user.role === 'student'">
            <el-menu-item index="/student">
              <el-icon><Notebook /></el-icon>
              <span>选课中心</span>
            </el-menu-item>
            <el-menu-item index="/profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
          </template>

          <!-- 老师菜单 -->
          <template v-else-if="user.role === 'teacher'">
            <el-menu-item index="/teacher">
              <el-icon><Plus /></el-icon>
              <span>添加课程</span>
            </el-menu-item>
            <el-menu-item index="/my-courses">
              <el-icon><Notebook /></el-icon>
              <span>我开的课</span>
            </el-menu-item>
            <el-menu-item index="/profile">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, Notebook, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const roleName = computed(() => {
  const roles: any = {
    admin: '管理员',
    student: '学生',
    teacher: '教师'
  }
  return roles[user.role] || '未知'
})

const handleLogout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.header {
  background-color: #409eff;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
}
.user-info {
  display: flex;
  align-items: center;
}
.user-name {
  margin-right: 20px;
}
.el-aside {
  background-color: #f4f4f5;
  border-right: 1px solid #dcdfe6;
}
.el-menu {
  border-right: none;
}
</style>
