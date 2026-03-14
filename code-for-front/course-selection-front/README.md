# 选课系统前端

基于 Vue 3 + Vite + TypeScript + Element Plus 构建的选课系统前端界面。

## 项目结构

- `src/api`: API 请求层，对接后端接口
- `src/views`: 页面视图
  - `Login.vue`: 登录界面
  - `Layout.vue`: 通用布局
  - `admin/`: 管理员界面（学生、课程、选课记录查询）
  - `student/`: 学生界面（选课、查看已选课程）
  - `teacher/`: 教师界面（添加课程）
- `src/utils/request.ts`: Axios 请求封装

## 如何运行

1. 安装依赖：
   ```bash
   npm install
   ```

2. 运行开发服务器：
   ```bash
   npm run dev
   ```

3. 构建项目：
   ```bash
   npm run build
   ```

## 注意事项

- **登录接口**：由于后端登录接口尚未完成，目前登录界面采用模拟登录逻辑。你可以使用任何用户名和密码登录，并根据选择的角色进入对应的界面。
- **代理配置**：在 `vite.config.ts` 中配置了代理，将 `/api` 请求转发到 `http://localhost:8080`。请确保后端服务在此地址运行。
- **权限校验**：前端实现了简单的路由守卫，根据用户角色限制访问。
