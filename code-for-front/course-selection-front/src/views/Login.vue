<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>选课系统登录</span>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, listUsers } from '@/api/user'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        // 1. 调用后端登录接口
        const token = await login({
          username: loginForm.username,
          password: loginForm.password
        })
        
        // 2. 存储 token 到 localStorage
        localStorage.setItem('token', token)
        
        // 3. 获取用户信息。由于后端登录接口仅返回 token，我们需要通过接口获取详细信息。
        // 我们根据 username 查找，不再依赖用户手动选择角色。
        const userRes = await listUsers({ 
          username: loginForm.username, 
          page: 1,
          pageSize: 1
        })
        
        if (userRes && userRes.rows && userRes.rows.length > 0) {
          const dbUser = userRes.rows[0]
          
          // 处理角色判定逻辑：
          // 如果用户名为 admin，则强制设为 admin 角色（即使数据库中存储的是 student 类型）
          // 否则使用数据库返回的 role (student 或 teacher)
          const finalRole = loginForm.username === 'admin' ? 'admin' : dbUser.role
            
          const userInfo = {
            id: dbUser.id,
            username: dbUser.username,
            name: dbUser.name,
            role: finalRole
          }
          
          localStorage.setItem('user', JSON.stringify(userInfo))
          ElMessage.success('登录成功')
          
          // 根据角色重定向
          if (finalRole === 'admin') {
            router.push('/admin')
          } else if (finalRole === 'student') {
            router.push('/student')
          } else if (finalRole === 'teacher') {
            router.push('/teacher')
          }
        } else {
          ElMessage.error('获取用户信息失败')
        }
      } catch (error: any) {
        console.error('Login error:', error)
        // 错误信息已由 request.ts 拦截器显示
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}
.login-card {
  width: 450px;
}
.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}
.login-btn {
  width: 100%;
}
</style>
