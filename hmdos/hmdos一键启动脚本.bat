@echo off
chcp 65001 >nul
echo ======================================
echo          hmdos 一键启动脚本
echo ======================================
echo.
echo [1]正在编译 hmdos.java ...
javac -encoding UTF-8 hmdos.java
if %errorlevel% neq 0 (
    echo 编译失败！请检查代码语法
    pause
    exit /b
)
echo 编译成功！
echo.
echo [2]启动 hmdos系统
echo --------------------------------------
java -Dfile.encoding=UTF-8 hmdos
echo.
echo --------------------------------------
echo hmdos 已经退出，按任意键关闭窗口
pause >nul
