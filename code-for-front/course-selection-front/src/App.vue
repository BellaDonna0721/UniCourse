<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 自定义分页显示文本
const customLocale = {
  ...zhCn,
  el: {
    ...zhCn.el,
    pagination: {
      ...zhCn.el.pagination,
      total: '总记录数: {total}',
      pagesize: '/页'
    }
  }
}
</script>

<template>
  <el-config-provider :locale="customLocale">
    <router-view />
  </el-config-provider>
</template>

<style>
#app {
  height: 100vh;
  margin: 0;
  padding: 0;
}
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}
</style>
