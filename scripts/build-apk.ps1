# Games Android APK Builder
# Usage:
#   .\scripts\build-apk.ps1
#   .\scripts\build-apk.ps1 -BuildType debug
#   .\scripts\build-apk.ps1 -BuildType release -Clean -Open

[CmdletBinding()]
param(
  [ValidateSet("release", "debug")]
  [string]$BuildType = "release",

  [switch]$Clean,
  [switch]$Open,
  [switch]$NoPause
)

$ErrorActionPreference = "Stop"
$script:ExitCode = 1
$script:StartTime = Get-Date
$script:TotalSteps = 4
if ($Clean) {
  $script:TotalSteps = 5
}
$script:CurrentStep = 0

function Write-Banner {
  Write-Host ""
  Write-Host "========================================" -ForegroundColor DarkCyan
  Write-Host "       Games Android APK Builder" -ForegroundColor Cyan
  Write-Host "========================================" -ForegroundColor DarkCyan
}

function Show-Progress {
  param([string]$Status)
  $percent = [Math]::Min(100, [int](($script:CurrentStep / $script:TotalSteps) * 100))
  Write-Progress -Activity "Build APK" -Status $Status -PercentComplete $percent
}

function Complete-Progress {
  Write-Progress -Activity "Build APK" -Completed
}

function Start-Step {
  param([string]$Title)
  $script:CurrentStep++
  $percent = [Math]::Min(100, [int](($script:CurrentStep / $script:TotalSteps) * 100))
  Write-Host ""
  Write-Host ("[{0}/{1}] ({2}%) {3}" -f $script:CurrentStep, $script:TotalSteps, $percent, $Title) -ForegroundColor Cyan
  Show-Progress -Status $Title
}

function Write-Ok {
  param([string]$Message)
  Write-Host ("  [OK] {0}" -f $Message) -ForegroundColor Green
}

function Write-Info {
  param([string]$Message)
  Write-Host ("  - {0}" -f $Message) -ForegroundColor Gray
}

function Write-Fail {
  param([string]$Message)
  Write-Host ("  [FAIL] {0}" -f $Message) -ForegroundColor Red
}

function Invoke-GradleWithProgress {
  param(
    [Parameter(Mandatory = $true)]
    [string]$TaskName,

    [Parameter(Mandatory = $true)]
    [string]$GradlewPath
  )

  $argLine = "$TaskName --no-daemon --console=plain"
  Write-Info ("Run: gradlew {0}" -f $argLine)

  $psi = New-Object System.Diagnostics.ProcessStartInfo
  $psi.FileName = "cmd.exe"
  $psi.Arguments = "/c `"$GradlewPath`" $argLine"
  $psi.WorkingDirectory = (Get-Location).Path
  $psi.UseShellExecute = $false
  $psi.RedirectStandardOutput = $true
  $psi.RedirectStandardError = $true
  $psi.CreateNoWindow = $true

  $proc = New-Object System.Diagnostics.Process
  $proc.StartInfo = $psi
  $null = $proc.Start()

  $taskPattern = [regex]"^> Task :(.+)$"

  while (-not $proc.HasExited -or -not $proc.StandardOutput.EndOfStream) {
    $line = $proc.StandardOutput.ReadLine()
    if ($null -eq $line) {
      if ($proc.HasExited) {
        break
      }
      Start-Sleep -Milliseconds 50
      continue
    }

    Write-Host $line

    $taskMatch = $taskPattern.Match($line)
    if ($taskMatch.Success) {
      Show-Progress -Status ("Gradle: {0}" -f $taskMatch.Groups[1].Value)
    }
  }

  $stderr = $proc.StandardError.ReadToEnd()
  if (-not [string]::IsNullOrWhiteSpace($stderr)) {
    Write-Host $stderr -ForegroundColor DarkYellow
  }

  $proc.WaitForExit()
  return $proc.ExitCode
}

function Wait-ExitPrompt {
  param([bool]$Success)

  try {
    Complete-Progress
  }
  catch {
    # ignore
  }

  $elapsed = (Get-Date) - $script:StartTime
  Write-Host ""
  Write-Host "----------------------------------------" -ForegroundColor DarkGray
  Write-Host ("Elapsed: {0:mm\:ss}" -f $elapsed) -ForegroundColor Gray

  if ($Success) {
    Write-Host "Build finished. You can close this window." -ForegroundColor Green
  }
  else {
    Write-Host "Build failed. Check the log above." -ForegroundColor Red
  }

  if ($NoPause) {
    return
  }

  Write-Host ""
  Write-Host "Press any key to exit..." -ForegroundColor Yellow
  cmd.exe /c pause > $null
}

# ---------- main ----------
Write-Banner

try {
  $Root = Split-Path -Parent $PSScriptRoot
  if (-not (Test-Path (Join-Path $Root "gradlew.bat"))) {
    if (Test-Path (Join-Path $PSScriptRoot "gradlew.bat")) {
      $Root = $PSScriptRoot
    }
    else {
      throw "gradlew.bat not found. Run from project root or scripts folder."
    }
  }

  Set-Location $Root
  Write-Host ("Project : {0}" -f $Root)
  Write-Host ("Type    : {0}" -f $BuildType)

  Start-Step -Title "Check environment"
  $gradlew = Join-Path $Root "gradlew.bat"
  if (-not (Test-Path $gradlew)) {
    throw "gradlew.bat not found"
  }
  Write-Ok "Gradle Wrapper ready"

  $keystoreFile = Join-Path $Root "keystore.properties"
  if (-not (Test-Path $keystoreFile)) {
    Write-Fail "Missing keystore.properties"
    Write-Host "Run: copy scripts\keystore.properties.example keystore.properties" -ForegroundColor Yellow
    throw "Missing keystore.properties"
  }
  Write-Ok "keystore.properties found"

  if ($Clean) {
    Start-Step -Title "Clean project"
    $cleanCode = Invoke-GradleWithProgress -TaskName "clean" -GradlewPath $gradlew
    if ($cleanCode -ne 0) {
      throw ("gradle clean failed, exit code: {0}" -f $cleanCode)
    }
    Write-Ok "Clean done"
  }

  if ($BuildType -eq "release") {
    $task = "assembleRelease"
  }
  else {
    $task = "assembleDebug"
  }

  Start-Step -Title ("Build APK ({0})" -f $task)
  $buildCode = Invoke-GradleWithProgress -TaskName $task -GradlewPath $gradlew
  if ($buildCode -ne 0) {
    throw ("gradle {0} failed, exit code: {1}" -f $task, $buildCode)
  }
  Write-Ok "Gradle build success"

  Start-Step -Title "Find APK output"
  $outputDir = Join-Path $Root ("app\build\outputs\apk\{0}" -f $BuildType)
  if (-not (Test-Path $outputDir)) {
    $outputDir = Join-Path $Root "app\build\outputs\apk"
  }

  $apkFiles = Get-ChildItem -Path $outputDir -Filter "*.apk" -Recurse -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending

  if (-not $apkFiles -or $apkFiles.Count -eq 0) {
    throw ("APK not found under: {0}" -f $outputDir)
  }

  $latestApk = $apkFiles[0]
  Write-Ok ("Found: {0}" -f $latestApk.Name)
  Write-Info ("Path: {0}" -f $latestApk.FullName)
  Write-Info ("Size: {0:N2} MB" -f ($latestApk.Length / 1MB))

  Start-Step -Title "Copy to app/release"
  $releaseDir = Join-Path $Root "app\release"
  New-Item -ItemType Directory -Force -Path $releaseDir | Out-Null
  $destApk = Join-Path $releaseDir $latestApk.Name
  Copy-Item -Force $latestApk.FullName $destApk
  Write-Ok ("Copied: {0}" -f $destApk)

  if ($Open) {
    Start-Process explorer.exe $releaseDir
    Write-Info "Opened output folder"
  }

  $script:CurrentStep = $script:TotalSteps
  Show-Progress -Status "Done"
  Write-Host ""
  Write-Host "======== BUILD SUCCESS ========" -ForegroundColor Green
  Write-Host ("APK: {0}" -f $destApk) -ForegroundColor Green
  $script:ExitCode = 0
}
catch {
  $script:ExitCode = 1
  Write-Host ""
  Write-Fail $_.Exception.Message
}
finally {
  Wait-ExitPrompt -Success ($script:ExitCode -eq 0)
}

exit $script:ExitCode
