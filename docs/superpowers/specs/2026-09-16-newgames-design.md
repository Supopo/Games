# NewGames 设计规格

## 目标

在现有 `Games` 工程旁创建独立的 `NewGames` Android 应用，完整迁移现有非广告游戏页面与本地数据，同时保持原应用的深色渐变背景、彩色圆角入口、中文文案和轻量游戏氛围。新项目不接入广告 SDK、更新 SDK 或旧项目的广告相关权限。

## 范围

- 保留首页及现有可用游戏入口：真心话大冒险、愤怒的皮卡丘、谁是卧底、逛公园。
- 迁移现有本地题库、公园等资源，以及核心页面需要的图片和音频资源。
- 保留原页面的主要交互：新增玩家、添加真心话/大冒险题目、随机选择、洗牌/翻牌、分组、结果展示和返回导航。
- 广告测试入口、Pangle、蒲公英、相关 Provider、元数据与权限全部移除。
- 旧 `Games` 与 `LibMVVM` 不修改、不作为新项目依赖。

## 架构

`NewGames` 使用单 Activity + Compose Navigation。每个功能页面由独立的 Compose screen、不可变 UI state 和 ViewModel 组成；页面之间通过 typed route 参数或共享 saved state 传递最小数据。静态题库和名字数据继续使用 `assets`，读取由 Kotlin repository/helper 完成，避免把资源解析逻辑放入 Composable。

## 技术栈

- Kotlin 2.2.21 / K2
- Android Gradle Plugin 8.13.2
- Gradle 8.13
- Jetpack Compose + Material 3 + Compose BOM 2025.08.00 (Compose 1.9 stable line)
- Navigation Compose
- Lifecycle ViewModel + Kotlin Coroutines/StateFlow
- AndroidX Core、Activity Compose、DataStore 仅在需要持久化时引入
- 不使用 RxJava 2、ButterKnife、DataBinding、旧 Support Library、NineOldAndroids、旧自建 MVVM 库或广告/更新 SDK

## 视觉与适配

- 背景沿用 `#1C1333 → #521E38 → #1B1230` 的深色纵向渐变。
- 首页入口保留旧版黄色/橙色、蓝紫、绿色等彩色渐变卡片与约 20dp 圆角。
- 以 Compose `WindowInsets` 处理状态栏/导航栏，使用 `LazyVerticalGrid` 或自适应列数保持手机与大屏的比例。
- 页面内部继续使用高对比白色文字、圆角操作卡和简单弹性/淡入动画，不引入与旧版风格冲突的复杂视觉系统。

## 测试与验收

- JVM 单元测试覆盖资源解析、随机/分组核心状态变化。
- Compose UI 测试覆盖首页入口文案、广告入口不存在、入口点击后的目标页面。
- Gradle 构建至少执行 `assembleDebug` 和单元测试。
- 旧 `Games` 构建文件与源码不得因新项目迁移而发生变化。
