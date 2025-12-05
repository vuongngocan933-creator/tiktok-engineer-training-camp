# 🎙️ AI-Note-Assistant (B站视频语音转笔记助手)

> 一个基于 Android Jetpack (MVVM) 架构开发的效率工具，集成了用户鉴权系统与 Bilibili 视频语音转写功能。

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Arch-MVVM-orange.svg)]()
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

## 📖 项目简介 (Introduction)

本项目原本是用于解决“观看技术讲座/课程视频时记录笔记效率低”的个人痛点。它是一个完整的 Android 应用程序，不仅实现了标准的用户账户管理流程，还构建了一套自动化的音视频处理管线。

应用可以解析 Bilibili 视频链接，智能提取音频流，在本地通过 FFmpeg 进行标准化转码，最后调用阿里云通义千问（Paraformer）模型生成带时间戳的逐字稿。

## ✨ 核心功能 (Features)

### 🔐 1. 用户管理模块 (User System)
基于 MVVM 架构实现的完整用户生命周期管理：
- **注册与登录**：表单验证、用户数据持久化。
- **状态管理**：基于 `LiveData`/`StateFlow` 的登录状态分发，支持自动跳转与页面拦截。
- **页面导航**：实现 Activity/Fragment 之间的平滑切换与回退栈管理。
- **安全退出**：清除用户会话数据并重置 UI 状态。

### 🎧 2. 视频转写模块 (AI Transcription)
自动化的“视频-文本”转换管线：
- **智能解析**：
  - 支持输入 BV 号或 B 站完整 HTTPS 链接。
  - 通过 B 站内部 API (`view` & `playurl`) 逆向获取 CID 及 DASH 音频流。
- **音频处理**：
  - **下载**：配置 Referer 防盗链头，流式下载 `.m4s` 音频。
  - **转码**：集成 **FFmpegKit**，将音频标准化为 16k 采样率、单声道 MP3（满足 AI 模型输入要求）。
- **AI 识别**：
  - 集成 **阿里云 DashScope SDK** (Paraformer-Realtime 模型)。
  - 生成格式化的 `[MM:SS - MM:SS] 文本内容` 笔记。
- **文件导出**：
  - 适配 Android Scoped Storage，支持将处理后的音频导出至系统下载目录。

## 🛠️ 技术栈 (Tech Stack)

### Architecture & Design
- **MVVM (Model-View-ViewModel)**：实现 UI 与 业务逻辑的彻底解耦。
- **Repository Pattern**：数据仓库层，统一管理网络数据与本地文件操作。
- **Singleton Pattern**：单例模式管理全局服务。

### Android & Kotlin
- **Coroutines & Flow**：处理耗时任务（网络请求、文件IO、转码）与数据流响应。
- **ViewBinding**：替代 `findViewById`，实现类型安全的视图绑定。
- **ViewModel & LiveData**：生命周期感知的状态管理。

### Third-party Libraries
- **Network**: OkHttp3, Gson
- **Media**: [FFmpegKit](https://github.com/arthenica/ffmpeg-kit) (音频转码)
- **AI Service**: Aliyun DashScope SDK (语音识别)

## 🏗️ 项目结构 (Project Structure)

```text
com.example.myapplication
 ├── data/            # 数据层 (Entity, DAO, Database)
 ├── model/           # 业务模型 (User, BiliResponse, AliResult)
 ├── service/         # 核心后台服务 (独立于 UI)
 │    ├── AuthService.kt       # 鉴权逻辑
 │    ├── BilibiliService.kt   # B站接口逆向解析
 │    └── AudioService.kt      # 音频转码与 AI 识别实现
 ├── ui/              # 界面层 (Compose Screens)
 │    ├── components/          # 通用组件 (按钮, 头部等)
 │    ├── screens/             # 页面 (Login, Register, Transcribe, etc.)
 │    └── theme/               # 主题配置 (Color, Type)
 ├── viewmodel/       # 视图模型 (状态管理)
 │    ├── LoginViewModel.kt    
 │    └── TranscribeViewModel.kt 
 └── utils/           # 工具类 (FileUtils, TimeUtils)
