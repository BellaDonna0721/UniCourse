<template>
  <div class="teacher-dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>添加新课程</span>
        </div>
      </template>
      <el-form :model="courseForm" :rules="rules" ref="courseFormRef" label-width="100px" style="max-width: 600px; margin: 0 auto;">
        <el-form-item label="课程代码" prop="courseCode">
          <el-input v-model="courseForm.courseCode" placeholder="请输入课程代码（如：CS101）" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseForm.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="学分" prop="credit">
          <el-input-number v-model="courseForm.credit" :min="1" :max="10" :precision="1" />
        </el-form-item>
        <el-form-item label="课程容量" prop="capacity">
          <el-input-number v-model="courseForm.capacity" :min="1" :max="500" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitCourse" :loading="loading">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { addCourse } from '@/api/course'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const courseFormRef = ref()
const user = JSON.parse(localStorage.getItem('user') || '{}')

const courseForm = reactive({
  courseCode: '',
  courseName: '',
  teacherId: user.id,
  credit: 2.0,
  capacity: 100,
  remaining: 100
})

const rules = {
  courseCode: [{ required: true, message: '请输入课程代码', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }]
}

const submitCourse = () => {
  courseFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        // 设置初始余量等于容量
        courseForm.remaining = courseForm.capacity
        await addCourse(courseForm)
        ElMessage.success('课程添加成功')
        resetForm()
      } finally {
        loading.value = false
      }
    }
  })
}

const resetForm = () => {
  courseFormRef.value.resetFields()
}
</script>

<style scoped>
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
