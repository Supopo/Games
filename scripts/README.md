# Android 打包脚本

本目录包含 APK 打包相关文件：

| 文件 | 说明 |
| --- | --- |
| `build-apk.bat` | Windows 入口（双击或命令行） |
| `build-apk.ps1` | 实际打包逻辑（进度、复制 APK、结束暂停） |
| `keystore.properties.example` | 签名配置模板 |

## 使用前

在**项目根目录**创建签名配置（不要提交到 Git）：

```bat
copy scripts\keystore.properties.example keystore.properties
```

编辑根目录的 `keystore.properties`，填入真实的 `storeFile` / 密码 / alias。

## 打包

```bat
scripts\build-apk.bat
scripts\build-apk.bat debug
scripts\build-apk.bat release clean open
```

或：

```powershell
.\scripts\build-apk.ps1
.\scripts\build-apk.ps1 -BuildType release -Clean -Open
```

成功后 APK 在 `app\release\`。
