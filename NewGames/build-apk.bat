@echo off
setlocal
pushd "%~dp0" || exit /b 1

set "EXIT_CODE=1"
if not exist "gradlew.bat" (
    echo [ERROR] gradlew.bat not found in %CD%
    goto finish
)
if not exist "keystore.properties" (
    echo [ERROR] keystore.properties not found in %CD%
    goto finish
)
if not exist "app.jks" (
    echo [ERROR] app.jks not found in %CD%
    goto finish
)
if defined JAVA_HOME if not exist "%JAVA_HOME%\bin\java.exe" (
    where java >nul 2>&1
    if errorlevel 1 (
        echo [ERROR] JAVA_HOME is invalid and Java is not on PATH.
        goto finish
    )
    echo [INFO] JAVA_HOME is invalid; using Java from PATH.
    set "JAVA_HOME="
)
if not exist ".gradle" mkdir ".gradle"
if not exist ".gradle" (
    echo [ERROR] Cannot create Gradle temporary directory.
    goto finish
)
set "JAVA_TOOL_OPTIONS=%JAVA_TOOL_OPTIONS% -Djdk.net.unixdomain.tmpdir=%CD%\.gradle"

echo Building NewGames signed Release APK...
call ".\gradlew.bat" :app:assembleRelease --no-daemon
if errorlevel 1 goto finish

set "OUTPUT_DIR=%CD%\app\build\outputs\apk\release"
set "APK=%OUTPUT_DIR%\app-release.apk"
if not exist "%APK%" (
    echo [ERROR] APK not found: %APK%
    goto finish
)
if not exist "%OUTPUT_DIR%\output-metadata.json" (
    echo [ERROR] APK version metadata not found in %OUTPUT_DIR%
    goto finish
)

powershell -NoProfile -Command "$ErrorActionPreference='Stop'; $dir=$env:OUTPUT_DIR; $version=(Get-Content -Raw -LiteralPath (Join-Path $dir 'output-metadata.json') | ConvertFrom-Json).elements[0].versionName; if ([string]::IsNullOrWhiteSpace($version)) { throw 'APK versionName is missing' }; $target=Join-Path $dir ('NewGames-v' + $version + '.apk'); Copy-Item -LiteralPath $env:APK -Destination $target -Force; Write-Output ('Build succeeded: ' + $target)"
if errorlevel 1 goto finish
set "EXIT_CODE=0"

:finish
if not "%EXIT_CODE%"=="0" echo Build failed.
popd
if /I not "%~1"=="nopause" pause
exit /b %EXIT_CODE%
