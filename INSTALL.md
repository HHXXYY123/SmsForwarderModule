# 安装与编译指南

本项目是一个标准的 Android Studio 项目，同时包含了 KernelSU/Magisk 模块的打包脚本。

## 准备工作
- 安装 Android Studio 或 Gradle。
- 配置 Android SDK (Target API 34)。
- 安装 Java JDK 17 或更高版本。

## 第一步：编译 APK

1. 使用 Android Studio 打开 `SmsForwarderModule` 文件夹。
2. 确保 Gradle 依赖同步完成。
3. 编译 APK:
   - Android Studio 菜单: `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`.
   - 命令行: 运行 `./gradlew :app:assembleDebug` (如果需要签名则用 `assembleRelease`).

4. 找到编译好的 APK 文件:
   - 通常路径在 `app/build/outputs/apk/debug/app-debug.apk`。

## 第二步：打包模块

1. 将生成的 APK 重命名为 `SmsForwarder.apk`。
2. 将 `SmsForwarder.apk` 复制到项目内的以下目录：
   `module/system/priv-app/SmsForwarder/`
   
   (确保目录结构完整: `module/system/priv-app/SmsForwarder/SmsForwarder.apk`)

3. 进入 `module` 目录。
4. **选中**该目录下的所有文件 (`module.prop`, `customize.sh`, `service.sh`, `system/` 文件夹)。
5. 将它们压缩为一个 ZIP 文件，命名为 `SmsForwarderModule.zip`。
   - **注意**: 不要压缩 `module` 文件夹本身，必须压缩它里面的**内容**。

## 第三步：刷入设备

1. 将 `SmsForwarderModule.zip` 传输到你的 Android 手机。
2. 打开 KernelSU 或 Magisk 管理器。
3. 进入 **模块 (Modules)** 页面。
4. 点击 **从本地安装 (Install from storage)**。
5. 选择刚才的 ZIP 文件。
6. 等待刷入完成，点击 **重启 (Reboot)**。

## 第四步：验证

1. 重启后，检查是否安装了 "SMS Forwarder" 应用。
2. 打开应用并配置 PushPlus Token。
3. 确认服务正在运行，且通知栏有常驻图标。
