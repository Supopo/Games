# 启动 AIoT-IDE 安装的 redmi_watch 模拟器（.vela/vvd）
# 注意：这与 aiot server --open-nuttx（旧版 velasim/nuttx）不是同一套模拟器

$ErrorActionPreference = "Stop"

$velaHome = Join-Path $env:USERPROFILE ".vela"
$avdHome = Join-Path $velaHome "vvd"
$sdkRoot = Join-Path $velaHome "sdk"
$emu = Join-Path $sdkRoot "emulator\windows-x86_64\emulator.exe"
$avdName = if ($args.Count -gt 0) { $args[0] } else { "redmi_watch" }

if (-not (Test-Path $emu)) {
  Write-Error "找不到模拟器: $emu`n请先在 AIoT-IDE 中完成模拟器环境安装。"
}

if (-not (Test-Path (Join-Path $avdHome "$avdName.vvd"))) {
  Write-Error "找不到 AVD: $avdName`n可用设备见: $avdHome"
}

$env:ANDROID_AVD_HOME = $avdHome
$env:ANDROID_SDK_ROOT = $sdkRoot
$env:ANDROID_HOME = $sdkRoot

Write-Host "AVD Home : $env:ANDROID_AVD_HOME"
Write-Host "SDK Root : $env:ANDROID_SDK_ROOT"
Write-Host "Launching: $avdName"
Write-Host ""

# 与 IDE 的 emu-launch-params 对齐（去掉 -qt-hide-window，方便看见窗口）
& $emu `
  -vela `
  -avd $avdName `
  -show-kernel `
  -network-user-mode-options "hostfwd=tcp:127.0.0.1:10055-10.0.2.15:101" `
  -qemu `
  -device "virtio-snd,bus=virtio-mmio-bus.2" `
  -allow-host-audio `
  -semihosting `
  -smp 2
