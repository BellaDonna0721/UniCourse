<template>
  <div class="student-dashboard">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="选课中心" name="selection">
        <el-card>
          <div class="search-bar">
            <el-input v-model="courseSearch.courseName" placeholder="搜索课程" style="width: 200px; margin-right: 10px;" />
            <el-button type="primary" @click="fetchCourses">查询</el-button>
          </div>
          <el-table :data="courseList" style="width: 100%" v-loading="loading">
            <el-table-column prop="courseCode" label="课程代码" width="120" />
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column label="授课教师" width="120">
              <template #default="scope">
                {{ getTeacherName(scope.row.teacherId) }}
              </template>
            </el-table-column>
            <el-table-column prop="credit" label="学分" width="80" />
            <el-table-column prop="capacity" label="容量" width="80" />
            <el-table-column label="余量" width="80">
              <template #default="scope">
                {{ scope.row.capacity - scope.row.selected }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="primary" size="small" @click="handleSelect(scope.row.id)" :disabled="(scope.row.capacity - scope.row.selected) <= 0">选课</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            class="pagination"
            v-model:current-page="courseSearch.page"
            v-model:page-size="courseSearch.pageSize"
            :total="courseTotal"
            layout="prev, pager, next"
            @current-change="fetchCourses"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的选课" name="my-selections">
        <el-card>
          <el-table :data="mySelections" style="width: 100%" v-loading="loading">
            <el-table-column prop="courseCode" label="课程代码" width="120" />
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column label="授课教师" width="120">
              <template #default="scope">
                {{ getTeacherName(scope.row.teacherId) }}
              </template>
            </el-table-column>
            <el-table-column prop="credit" label="学分" width="80" align="center" />
            <el-table-column prop="selected" label="已选人数" width="100" align="center" />
            <el-table-column prop="createTime" label="选课时间" align="center" />
            <el-table-column label="操作" width="120" align="center">
              <template #default="scope">
                <el-button type="danger" size="small" @click="handleDrop(scope.row.courseId)">退课</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { listCourses, getCourseById } from '@/api/course'
import { listUsers } from '@/api/user'
import { addSelection, deleteSelection, getByUserId } from '@/api/selection'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('selection')
const loading = ref(false)
const user = JSON.parse(localStorage.getItem('user') || '{}')

// 教师 ID 到姓名的映射
const teacherMap = ref<Record<string, string>>({})

// 课程详情缓存，避免重复请求
const courseCache = new Map<number, any>()

// 课程列表
const courseList = ref([])
const courseTotal = ref(0)
const courseSearch = reactive({
  page: 1,
  pageSize: 10,
  courseName: ''
})

// 已选课程
const mySelections = ref([])

// 获取教师列表以构建 ID 到姓名的映射
const fetchTeachers = async () => {
  try {
    const res = await listUsers({ role: 'teacher', page: 1, pageSize: 1000 })
    res.rows.forEach((teacher: any) => {
      teacherMap.value[teacher.id] = teacher.name
    })
  } catch (error) {
    console.error('获取教师列表失败', error)
  }
}

// 获取教师姓名的辅助函数
const getTeacherName = (teacherId: string) => {
  if (!teacherId) return '未知'
  return teacherMap.value[teacherId] || `教师(ID:${teacherId})`
}

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await listCourses(courseSearch)
    courseList.value = res.rows
    courseTotal.value = res.totals
    
    // 更新缓存
    res.rows.forEach((course: any) => {
      courseCache.set(course.id, course)
    })
  } finally {
    loading.value = false
  }
}

const fetchMySelections = async () => {
  loading.value = true
  try {
    const selections = await getByUserId(user.id)
    
    // 为每个选课记录补充课程详情
    const enrichedSelections = await Promise.all(
      selections.map(async (selection: any) => {
        let courseInfo = courseCache.get(selection.courseId)
        
        if (!courseInfo) {
          try {
            courseInfo = await getCourseById(selection.courseId)
            courseCache.set(selection.courseId, courseInfo)
          } catch (e) {
            console.error(`无法获取课程 ID 为 ${selection.courseId} 的详情`, e)
            courseInfo = { courseName: '未知课程', courseCode: '未知' }
          }
        }
        
        return {
          ...selection,
          ...courseInfo,
          // 确保 ID 不被覆盖
          selectionId: selection.id
        }
      })
    )
    
    mySelections.value = enrichedSelections
  } finally {
    loading.value = false
  }
}

const handleSelect = async (courseId: number) => {
  try {
    await addSelection(user.id, courseId)
    ElMessage.success('选课成功')
    fetchCourses()
    fetchMySelections()
  } catch (err) {
    // 错误处理由 request 拦截器完成
  }
}

const handleDrop = async (courseId: number) => {
  try {
    await ElMessageBox.confirm('确定要退选这门课程吗？', '提示', { type: 'warning' })
    await deleteSelection(user.id, courseId)
    ElMessage.success('退课成功')
    fetchCourses()
    fetchMySelections()
  } catch (err) {
    if (err !== 'cancel') {
      // 错误处理由 request 拦截器完成
    }
  }
}

onMounted(async () => {
  await fetchTeachers()
  fetchCourses()
  fetchMySelections()
})
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
