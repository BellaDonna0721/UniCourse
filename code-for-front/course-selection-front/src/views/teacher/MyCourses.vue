<template>
  <div class="my-courses">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我开的课</span>
        </div>
      </template>

      <el-table :data="courseList" style="width: 100%" v-loading="loading" border stripe>
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="capacity" label="课程容量" width="100" align="center" />
        <el-table-column prop="selected" label="已选人数" width="100" align="center" />
        <el-table-column prop="updateTime" label="更新时间" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="viewSelections(scope.row.id)">查看选课</el-button>
            <el-button link type="primary" @click="handleCourseEdit(scope.row)">修改</el-button>
            <el-button link type="danger" @click="handleCourseDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="searchParams.page"
          v-model:page-size="searchParams.pageSize"
          :total="total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchCourses"
          @current-change="fetchCourses"
        />
      </div>
    </el-card>

    <!-- 选课记录对话框 -->
    <el-dialog v-model="dialogVisible" title="选课详情" width="800px">
      <el-table :data="paginatedSelections" v-loading="selectionLoading" border stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column prop="updateTime" label="更新时间" align="center">
          <template #default="scope">
            {{ formatTime(scope.row.updateTime) }}
          </template>
        </el-table-column>
      </el-table>
      <div class="dialog-pagination">
        <el-pagination
          small
          v-model:current-page="selectionParams.page"
          v-model:page-size="selectionParams.pageSize"
          :total="selectionList.length"
          layout="total, prev, pager, next"
        />
      </div>
    </el-dialog>

    <!-- 修改课程对话框 -->
    <el-dialog v-model="editCourseDialogVisible" title="修改课程信息" width="500px">
      <el-form :model="editCourseForm" ref="editCourseFormRef" label-width="100px">
        <el-form-item label="课程代码">
          <el-input v-model="editCourseForm.courseCode" />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="editCourseForm.courseName" />
        </el-form-item>
        <el-form-item label="学分">
          <el-input-number v-model="editCourseForm.credit" :min="0" :max="10" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="editCourseForm.capacity" :min="0" :max="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="已选人数">
          <el-input-number v-model="editCourseForm.selected" :min="0" :max="editCourseForm.capacity" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editCourseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCourseEdit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { listCourses, updateCourse, deleteCourse } from '@/api/course'
import { getByCourseId } from '@/api/selection'
import { getUserById } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const selectionLoading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const courseList = ref([])

const dialogVisible = ref(false)
const editCourseDialogVisible = ref(false)
const editCourseFormRef = ref()

// 选课详情数据
const selectionList = ref<any[]>([])
const selectionParams = reactive({
  page: 1,
  pageSize: 10
})

// 修改课程表单数据
const editCourseForm = reactive({
  id: undefined,
  courseCode: '',
  courseName: '',
  teacherId: '',
  credit: 0,
  capacity: 0,
  selected: 0
})

// 计算分页后的选课数据（前端分页）
const paginatedSelections = computed(() => {
  const start = (selectionParams.page - 1) * selectionParams.pageSize
  const end = start + selectionParams.pageSize
  return selectionList.value.slice(start, end)
})

// 从 localStorage 获取当前登录用户信息
const user = JSON.parse(localStorage.getItem('user') || '{}')

const searchParams = reactive({
  page: 1,
  pageSize: 10,
  teacherId: user.id
})

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await listCourses(searchParams)
    courseList.value = res.rows
    total.value = res.totals
  } catch (error) {
    console.error('获取课程列表失败', error)
  } finally {
    loading.value = false
  }
}

const viewSelections = async (courseId: number) => {
  selectionLoading.value = true
  dialogVisible.value = true
  selectionList.value = []
  selectionParams.page = 1
  
  try {
    const selections = await getByCourseId(courseId)
    // 异步拉取所有学生详细信息
    const enrichedSelections = await Promise.all(
      selections.map(async (sel: any) => {
        try {
          const studentInfo = await getUserById(sel.userId)
          return { ...sel, ...studentInfo }
        } catch (e) {
          return { ...sel, username: '未知', name: '未知', major: '未知' }
        }
      })
    )
    selectionList.value = enrichedSelections
  } catch (error) {
    console.error('获取选课详情失败', error)
  } finally {
    selectionLoading.value = false
  }
}

const handleCourseEdit = (row: any) => {
  editCourseDialogVisible.value = true
  editCourseForm.id = row.id
  editCourseForm.courseCode = row.courseCode
  editCourseForm.courseName = row.courseName
  editCourseForm.teacherId = row.teacherId
  editCourseForm.credit = row.credit
  editCourseForm.capacity = row.capacity
  editCourseForm.selected = row.selected
}

const submitCourseEdit = async () => {
  submitLoading.value = true
  try {
    await updateCourse(editCourseForm)
    ElMessage.success('课程修改成功')
    editCourseDialogVisible.value = false
    fetchCourses()
  } finally {
    submitLoading.value = false
  }
}

const handleCourseDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await deleteCourse(row.id)
    ElMessage.success('删除成功')
    fetchCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.my-courses {
  padding: 20px;
}
.card-header {
  font-weight: bold;
  font-size: 18px;
}
.pagination, .dialog-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
