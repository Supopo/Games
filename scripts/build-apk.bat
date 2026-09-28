@echo off
chcp 65001 >nul
setlocal EnableExtensions
title Games APK Builder
set "ERR=0"

REM 始终先切到项目根目录
cd /d "%~dp0\.."
if errorlevel 1 (
  echo [错误] 无法进入项目根目录
  set "ERR=1"
  goto :end_pause
)

REM 用法:
REM   scripts\build-apk.bat
REM   scripts\build-apk.bat debug
REM   scripts\build-apk.bat release clean open
REM   scripts\build-apk.bat release nopause

set "BUILD_TYPE=release"
set "EXTRA_ARGS="
set "DO_PAUSE=1"

:parse
if "%~1"=="" goto run
if /I "%~1"=="debug" set "BUILD_TYPE=debug"
if /I "%~1"=="release" set "BUILD_TYPE=release"
if /I "%~1"=="clean" set "EXTRA_ARGS=%EXTRA_ARGS% -Clean"
if /I "%~1"=="open" set "EXTRA_ARGS=%EXTRA_ARGS% -Open"
if /I "%~1"=="nopause" (
  set "DO_PAUSE=0"
  set "EXTRA_ARGS=%EXTRA_ARGS% -NoPause"
)
shift
goto parse

:run
echo.
echo [BAT] 项目目录: %CD%
echo [BAT] 构建类型: %BUILD_TYPE%
echo.

if not exist "%~dp0build-apk.ps1" (
  echo [错误] 找不到脚本: %~dp0build-apk.ps1
  set "ERR=1"
  goto :end_pause
)

if not exist "keystore.properties" (
  echo [错误] 项目根目录缺少 keystore.properties
  echo 请先执行:
  echo   copy scripts\keystore.properties.example keystore.properties
  echo 然后填写真实的签名信息。
  set "ERR=1"
  goto :end_pause
)

where powershell >nul 2>&1
if errorlevel 1 (
  echo [错误] 系统未找到 powershell，请检查 PATH
  set "ERR=1"
  goto :end_pause
)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build-apk.ps1" -BuildType %BUILD_TYPE% %EXTRA_ARGS%
set "ERR=%ERRORLEVEL%"

echo.
if not "%ERR%"=="0" (
  echo [BAT] 打包失败，退出码: %ERR%
) else (
  echo [BAT] 脚本执行结束
)

:end_pause
if "%DO_PAUSE%"=="1" (
  echo.
  echo 按任意键关闭窗口...
  pause >nul
)
exit /b %ERR%
