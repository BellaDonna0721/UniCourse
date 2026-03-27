@echo off
title 选课系统一键启动器 (前端 + Redis)
color 0A

echo ==========================================
echo   正在启动 Redis 服务...
echo ==========================================
:: 利用 start 在新窗口打开 redis-server，避免阻塞后续前端启动
:: 注意：这里假设 redis-server 已经配置在了系统环境变量。
:: 如果没有加入环境变量，请将其替换为你的 Redis 绝对路径，
:: 例如: start "Redis" "D:\Redis\redis-server.exe" "D:\Redis\redis.windows.conf"
start "Redis Server" redis-server

:: 等待 2 秒确保 Redis 启动一下（可选）
timeout /t 2 /nobreak > nul

echo.
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

:: 3. 执行启动命令 (在当前主窗口运行)
npm run dev

:: 如果进程意外结束，保持窗口开启以查看错误信息
pause