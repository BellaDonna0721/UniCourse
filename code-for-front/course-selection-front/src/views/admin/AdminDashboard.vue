<template>
  <div class="admin-dashboard">
    <el-tabs v-model="activeTab" class="demo-tabs">
      <!-- 学生管理 -->
      <el-tab-pane label="学生管理" name="students">
        <el-card class="box-card">
          <div class="search-bar">
            <div class="search-left">
              <el-input v-model="studentSearch.name" placeholder="搜索姓名" style="width: 150px; margin-right: 10px;" clearable />
              <el-input v-model="studentSearch.major" placeholder="搜索专业" style="width: 150px; margin-right: 10px;" clearable />
              <el-button type="primary" @click="handleStudentSearch">查询</el-button>
              <el-button type="success" @click="openAddUserDialog">添加用户</el-button>
            </div>
            <div class="search-right">
              <el-pagination
                small
                v-model:current-page="studentSearch.page"
                v-model:page-size="studentSearch.pageSize"
                :page-sizes="[5, 10, 20, 50]"
                :total="studentTotal"
                layout="total, sizes, prev, pager, next"
                @size-change="fetchStudents"
                @current-change="fetchStudents"
              />
            </div>
          </div>
          <el-table :data="studentList" style="width: 100%" v-loading="loading" border stripe>
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="major" label="专业" width="150" />
            <el-table-column prop="createTime" label="注册时间" align="center" />
            <el-table-column label="操作" width="150" align="center">
              <template #default="scope">
                <el-button link type="primary" @click="handleEdit(scope.row)">修改</el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 老师管理 -->
      <el-tab-pane label="老师管理" name="teachers">
        <el-card class="box-card">
          <div class="search-bar">
            <div class="search-left">
              <el-input v-model="teacherSearch.name" placeholder="搜索姓名" style="width: 150px; margin-right: 10px;" clearable />
              <el-input v-model="teacherSearch.major" placeholder="搜索专业" style="width: 150px; margin-right: 10px;" clearable />
              <el-button type="primary" @click="handleTeacherSearch">查询</el-button>
              <el-button type="success" @click="openAddUserDialog">添加用户</el-button>
            </div>
            <div class="search-right">
              <el-pagination
                small
                v-model:current-page="teacherSearch.page"
                v-model:page-size="teacherSearch.pageSize"
                :page-sizes="[5, 10, 20, 50]"
                :total="teacherTotal"
                layout="total, sizes, prev, pager, next"
                @size-change="fetchTeachers"
                @current-change="fetchTeachers"
              />
            </div>
          </div>
          <el-table :data="teacherList" style="width: 100%" v-loading="loading" border stripe>
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="major" label="专业" width="150" />
            <el-table-column prop="createTime" label="注册时间" align="center" />
            <el-table-column label="操作" width="150" align="center">
              <template #default="scope">
                <el-button link type="primary" @click="handleEdit(scope.row)">修改</el-button>
                <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 课程管理 -->
      <el-tab-pane label="课程管理" name="courses">
        <el-card class="box-card">
          <div class="search-bar">
            <div class="search-left">
              <el-input v-model="courseSearch.courseName" placeholder="课程名称" style="width: 150px; margin-right: 10px;" clearable />
              <el-button type="primary" @click="handleCourseSearch">查询</el-button>
            </div>
            <div class="search-right">
              <el-pagination
                small
                v-model:current-page="courseSearch.page"
                v-model:page-size="courseSearch.pageSize"
                :page-sizes="[5, 10, 20, 50]"
                :total="courseTotal"
                layout="total, sizes, prev, pager, next"
                @size-change="fetchCourses"
                @current-change="fetchCourses"
              />
            </div>
          </div>
          <el-table :data="courseList" style="width: 100%" v-loading="loading" border stripe>
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="courseCode" label="课程代码" width="120" />
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column label="授课教师" width="120" align="center">
              <template #default="scope">
                {{ getTeacherName(scope.row.teacherId) }}
              </template>
            </el-table-column>
            <el-table-column prop="credit" label="学分" width="80" align="center" />
            <el-table-column prop="capacity" label="容量" width="80" align="center" />
            <el-table-column label="余量" width="80" align="center">
              <template #default="scope">
                {{ scope.row.capacity - scope.row.selected }}
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
        </el-card>
      </el-tab-pane>
    </el-tabs>

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

    <!-- 添加用户对话框 -->
    <el-dialog v-model="addUserDialogVisible" title="添加新用户" width="500px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="用于登录的账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="选择角色" style="width: 100%">
            <el-option label="学生" value="student" />
            <el-option label="老师" value="teacher" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="userForm.major" placeholder="专业名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addUserDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUser" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改用户对话框 -->
    <el-dialog v-model="editUserDialogVisible" title="修改用户信息" width="500px">
      <el-form :model="editForm" ref="editFormRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" placeholder="不填则保持原样" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="editForm.password" type="password" placeholder="不填则保持原样" show-password />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editForm.name" placeholder="不填则保持原样" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" placeholder="不填则保持原样" style="width: 100%" clearable>
            <el-option label="学生" value="student" />
            <el-option label="老师" value="teacher" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="editForm.major" placeholder="不填则保持原样" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editUserDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改课程对话框 -->
    <el-dialog v-model="editCourseDialogVisible" title="修改课程信息" width="500px">
      <el-form :model="editCourseForm" ref="editCourseFormRef" label-width="100px">
        <el-form-item label="课程代码">
          <el-input v-model="editCourseForm.courseCode" placeholder="不填则保持原样" />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="editCourseForm.courseName" placeholder="不填则保持原样" />
        </el-form-item>
        <el-form-item label="教师ID">
          <el-input v-model="editCourseForm.teacherId" placeholder="不填则保持原样" />
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
import { listUsers, addUser, updateUser, deleteUser, getUserById } from '@/api/user'
import { listCourses, updateCourse, deleteCourse } from '@/api/course'
import { getByCourseId } from '@/api/selection'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('students')
const loading = ref(false)
const selectionLoading = ref(false)
const dialogVisible = ref(false)
const addUserDialogVisible = ref(false)
const editUserDialogVisible = ref(false)
const editCourseDialogVisible = ref(false)
const submitLoading = ref(false)
const userFormRef = ref()
const editFormRef = ref()
const editCourseFormRef = ref()

// 用户表单数据
const userForm = reactive({
  username: '',
  name: '',
  role: 'student',
  major: ''
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

// 修改用户表单数据
const editForm = reactive({
  id: undefined,
  username: '',
  password: '',
  name: '',
  role: '',
  major: ''
})

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }]
}

// 学生数据
const studentList = ref([])
const studentTotal = ref(0)
const studentSearch = reactive({
  page: 1,
  pageSize: 10,
  name: '',
  role: 'student',
  major: ''
})

// 老师数据
const teacherList = ref([])
const teacherTotal = ref(0)
const teacherMap = ref<Record<string, string>>({})
const teacherSearch = reactive({
  page: 1,
  pageSize: 10,
  name: '',
  role: 'teacher',
  major: ''
})

// 课程数据
const courseList = ref([])
const courseTotal = ref(0)
const courseSearch = reactive({
  page: 1,
  pageSize: 10,
  courseName: ''
})

// 选课数据
const selectionList = ref<any[]>([])
const selectionParams = reactive({
  page: 1,
  pageSize: 10
})

// 计算分页后的选课数据（前端分页）
const paginatedSelections = computed(() => {
  const start = (selectionParams.page - 1) * selectionParams.pageSize
  const end = start + selectionParams.pageSize
  return selectionList.value.slice(start, end)
})

// 搜索处理函数（重置页码到第一页）
const handleStudentSearch = () => {
  studentSearch.page = 1
  fetchStudents()
}

const handleTeacherSearch = () => {
  teacherSearch.page = 1
  fetchTeachers()
}

const handleCourseSearch = () => {
  courseSearch.page = 1
  fetchCourses()
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
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--primary',
      cancelButtonClass: 'el-button--default',
      customClass: 'custom-message-box'
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

const fetchStudents = async () => {
  loading.value = true
  try {
    const res = await listUsers(studentSearch)
    studentList.value = res.rows
    studentTotal.value = res.totals
  } finally {
    loading.value = false
  }
}

const fetchTeachers = async () => {
  loading.value = true
  try {
    const res = await listUsers(teacherSearch)
    teacherList.value = res.rows
    teacherTotal.value = res.totals
    
    // 更新教师 ID 到姓名的映射
    res.rows.forEach((teacher: any) => {
      teacherMap.value[teacher.id] = teacher.name
    })
  } finally {
    loading.value = false
  }
}

// 获取教师姓名的辅助函数
const getTeacherName = (teacherId: string) => {
  if (!teacherId) return '未知'
  return teacherMap.value[teacherId] || `教师(ID:${teacherId})`
}

const openAddUserDialog = () => {
  addUserDialogVisible.value = true
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

const handleEdit = (row: any) => {
  editUserDialogVisible.value = true
  editForm.id = row.id
  editForm.username = ''
  editForm.password = ''
  editForm.name = ''
  editForm.role = ''
  editForm.major = ''
}

const submitEdit = async () => {
  // 检查是否至少填写了一个字段
  const hasValue = editForm.username || editForm.password || editForm.name || editForm.role || editForm.major
  if (!hasValue) {
    ElMessage.warning('请至少修改一个字段')
    return
  }

  submitLoading.value = true
  try {
    // 构造提交数据，只包含有值的字段
    const updateData: any = { id: editForm.id }
    if (editForm.username) updateData.username = editForm.username
    if (editForm.password) updateData.password = editForm.password
    if (editForm.name) updateData.name = editForm.name
    if (editForm.role) updateData.role = editForm.role
    if (editForm.major) updateData.major = editForm.major

    await updateUser(updateData)
    ElMessage.success('用户修改成功')
    editUserDialogVisible.value = false
    fetchStudents()
    fetchTeachers()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--primary',
      cancelButtonClass: 'el-button--default',
      // 让确定按钮在左边，取消按钮在右边
      // Element Plus 默认是 [取消] [确定]，且确定在右。
      // 为了满足用户需求，我们可以通过样式或者自定义布局来实现。
      // 但标准 ElMessageBox 很难直接反转按钮位置。
      // 不过我们可以通过自定义 class 来调整顺序。
      customClass: 'custom-message-box'
    })
    
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchStudents()
    fetchTeachers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const submitUser = () => {
  userFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        await addUser(userForm)
        ElMessage.success('用户添加成功')
        addUserDialogVisible.value = false
        if (userForm.role === 'student') {
          fetchStudents()
        } else {
          fetchTeachers()
        }
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await listCourses(courseSearch)
    courseList.value = res.rows
    courseTotal.value = res.totals
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

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  return timeStr.replace('T', ' ')
}

onMounted(async () => {
  // 先获取教师列表以构建映射，确保课程表能显示姓名
  await fetchTeachers()
  fetchStudents()
  fetchCourses()
})
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}
.search-left {
  display: flex;
  align-items: center;
}
.search-right {
  display: flex;
  align-items: center;
}
.dialog-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

<style>
/* 全局样式用于覆盖 ElMessageBox 的按钮顺序 */
.custom-message-box .el-message-box__btns {
  display: flex;
  flex-direction: row-reverse;
  justify-content: flex-end;
}
.custom-message-box .el-message-box__btns .el-button:first-child {
  margin-left: 10px;
}
</style>
