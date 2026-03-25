@echo off
title 选课系统前端启动器
color 0A

echo ==========================================
echo   正在启动选课系统前端项目...
echo ==========================================

:: 1. 切换到前端项目的绝对路径
:: /d 参数确保可以跨盘符切换（从 C 盘切到 D 盘）
cd /d "d:\study\SpringBootStudy\course-selection-system\code-for-front\course-selection-front"

:: 2. 检查 node_modules 是否存在，如果不存在则提醒安装
if not exist "node_modules\" (
    echo [错误] 未检测到依赖库，请先在项目目录下运行 npm install
    pause
    exit
)

:: 3. 执行启动命令
npm run dev

:: 如果进程意外结束，保持窗口开启以查看错误信息
pause