<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息管理</span>
        </div>
      </template>
      
      <el-form 
        :model="profileForm" 
        :rules="rules" 
        ref="profileFormRef" 
        label-width="100px" 
        v-loading="loading"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="profileForm.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="profileForm.name" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="专业" prop="major">
          <el-input v-model="profileForm.major" placeholder="请输入所属专业" />
        </el-form-item>

        <el-divider content-position="left">修改密码（不填则保持不变）</el-divider>

        <el-form-item label="新密码" prop="password">
          <el-input 
            v-model="profileForm.password" 
            type="password" 
            placeholder="请输入新密码" 
            show-password 
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="profileForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            show-password 
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="submitLoading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getUserById, updateUser } from '@/api/user'
import { ElMessage } from 'element-plus'

const profileFormRef = ref()
const loading = ref(false)
const submitLoading = ref(false)

// 从 localStorage 获取当前登录用户 ID
const localUser = JSON.parse(localStorage.getItem('user') || '{}')

const profileForm = reactive({
  id: localUser.id,
  username: '',
  name: '',
  major: '',
  password: '',
  confirmPassword: '',
  role: '' // 角色信息通常不在这里修改，但需要传递给后端以保持一致
})

const validatePass2 = (rule: any, value: any, callback: any) => {
  if (profileForm.password && value !== profileForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  password: [{ min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ]
}

const fetchUserInfo = async () => {
  if (!localUser.id) return
  loading.value = true
  try {
    const res = await getUserById(localUser.id)
    profileForm.username = res.username
    profileForm.name = res.name
    profileForm.major = res.major
    profileForm.role = res.role
  } catch (error) {
    console.error('获取用户信息失败', error)
  } finally {
    loading.value = false
  }
}

const handleUpdate = () => {
  profileFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        const updateData: any = {
          id: profileForm.id,
          username: profileForm.username,
          name: profileForm.name,
          major: profileForm.major,
          role: profileForm.role
        }
        
        // 如果输入了密码，则加入更新列表
        if (profileForm.password) {
          updateData.password = profileForm.password
        }

        await updateUser(updateData)
        ElMessage.success('个人信息更新成功')
        
        // 更新成功后同步更新 localStorage 中的用户信息（除了密码）
        const newUser = { 
          ...localUser, 
          username: profileForm.username, 
          name: profileForm.name 
        }
        localStorage.setItem('user', JSON.stringify(newUser))
        
        // 清空密码框
        profileForm.password = ''
        profileForm.confirmPassword = ''
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}
.profile-card {
  width: 600px;
}
.card-header {
  font-weight: bold;
  font-size: 18px;
}
</style>
