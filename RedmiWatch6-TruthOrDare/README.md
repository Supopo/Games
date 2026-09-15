# 真心话大冒险 · REDMI Watch 6

基于手机版 `Games` 项目中的 **真心话大冒险** 模块，移植为适配 **REDMI Watch 6**（Xiaomi HyperOS / Vela）的手表应用。

技术栈：`Xiaomi Vela JS 应用`（快应用风格，`.ux` + JS）

官方文档：https://iot.mi.com/vela/quickapp/

## 功能对照

| 手机版 | 手表版 |
| --- | --- |
| `TurntableView` 大转盘选人 | 中间文字轮播展示抽取结果 |
| 弹窗选择真心话 / 大冒险 | `pages/result` 全屏步骤页 |
| 换一题 / 同意 / 拒绝 | 完整保留 |
| 拒绝惩罚「喝酒酒吧」 | 完整保留 |
| `qua.txt` / `do.txt` 题库 | 内置 `questions.js` / `dares.js`（197 + 66） |
| 添加玩家 / MMKV | `pages/players` + `system.storage` |
| 手表键盘受限 | 用人数加减（2~8）生成「N号玩家」 |

## 项目结构

```text
RedmiWatch6-TruthOrDare/
├── package.json
├── README.md
├── sign/                      # 签名证书目录（release 时放入 pem）
└── src/
    ├── app.ux
    ├── manifest.json
    ├── common/
    │   ├── images/logo.png
    │   ├── scripts/
    │   │   ├── questions.js   # 真心话
    │   │   ├── dares.js       # 大冒险
    │   │   ├── storage.js
    │   │   └── utils.js
    │   └── styles/app.css
    └── pages/
        ├── home/index.ux      # 抽取玩家
        ├── result/index.ux    # 题目流程
        └── players/index.ux   # 玩家人数管理
```

## 环境准备

1. 安装 [Node.js](https://nodejs.org/) >= 18
2. 安装 [AIoT-IDE](https://iot.mi.com/vela/quickapp/zh/tools/)（推荐）或仅使用命令行工具
3. 在本目录安装依赖：

```bash
npm install
```

## 运行与打包

当前依赖为 **`aiot-toolkit@^2.0.5`**（已按官方迁移说明从 1.x 升级）。

```bash
# 重新安装依赖（升级 toolkit 后务必执行一次）
npm install

# 编译并运行到模拟器（热更新；首次可能提示选择/创建模拟器）
npm start

# 仅打包 -> dist/*.rpk
npm run build

# release 包（需配置 sign/ 证书）
npm run release

# 查看已连接设备
npm run devices

# 初始化 / 创建 VVD 模拟器（可选）
npm run init-emulator
npm run create-avd
```

### 推荐：用 AIoT-IDE 跑本项目

1. 用 **AIoT-IDE ≥ 1.6** 打开目录 `RedmiWatch6-TruthOrDare`
2. 顶部选择模拟器 **`redmi_watch`**（路径：`C:\Users\Administrator\.vela\vvd`）
3. 点击 **运行 / Run**

### 命令行启动 IDE 同款模拟器（可选）

```bash
# 终端 1
npm run emulator

# 终端 2
npm start
```

说明：toolkit 2.0 的 `aiot start` 会对接 `.vela` VVD 模拟器体系；旧的 `--open-nuttx` / velasim 已不再作为本项目默认方案。

## 安装到 REDMI Watch 6

官方渠道通常需要应用商店审核。社区常见调试方式：

1. 用 AIoT-IDE / `aiot-toolkit` 连接手表调试器安装 `.rpk`
2. 或借助米坛社区工具（如 AstroBox）侧载（需自行评估风险）

设备特征：

- 系统：HyperOS（Vela）
- `deviceTypeList`: `watch`
- `designWidth`: `480`
- 震动：使用 `system.vibrator` 的 `vibrate({ mode: 'short' })`（Watch 6 支持）

## 玩法说明

1. 首页点击 **开始抽取**，名字轮播后选出玩家
2. 进入结果页，选择 **真心话** 或 **大冒险**
3. 可 **换一题** / **同意** / **拒绝**
4. 拒绝后进入惩罚页「喝酒酒吧」
5. **玩家** 页可调整 2~8 人

## 说明

- 题库内容直接来源于原 Android 项目 `app/src/main/assets/qua.txt`、`do.txt`
- 手表内存有限，已避免 Canvas 大转盘与过多页面，优先保证流畅与低占用
- 包名默认：`com.xxx.games.truthordare`，上架前请改成你的正式包名
