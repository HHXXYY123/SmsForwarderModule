# KernelSU 短信转发模块 (SmsForwarder to WeChat)

这是一个 KernelSU/Magisk 模块，通过安装一个系统应用，将已 Root 的 Android 设备收到的短信自动转发到微信（通过 PushPlus API）。

## 核心功能
- **自动转发**: 实时监听并秒级转发接收到的短信。
- **微信推送**: 集成 PushPlus API，确保推送到微信。
- **后台保活**: 包含前台服务通知，防止进程被杀。
- **电量优化**: 自动申请忽略电池优化，并利用 Root 权限将应用加入系统白名单。
- **ADB 调试**: 模块启动时自动开启 ADB 网络调试 (端口 5555)。
- **开机自启**: 设备重启后自动启动服务。
- **重试机制**: 使用 Android WorkManager，网络故障时自动进入重试队列。
- **配置界面**: 提供简洁的 App 界面用于设置 PushPlus Token 和测试推送。

## 环境要求
- Android 8.0+ (SDK 26+)
- 已安装 KernelSU 或 Magisk 的 Root 环境
- 互联网连接

## 安装说明
1. 下载模块 ZIP 包（或自行编译，详见 [INSTALL.md](INSTALL.md)）。
2. 打开 KernelSU 或 Magisk 管理器。
3. 进入 "模块" -> "从本地安装"。
4. 选择 ZIP 文件刷入。
5. 重启手机。

## 配置指南
1. 重启后，在桌面上打开 "SMS Forwarder" 应用。
2. 授予必要的权限（短信、通知）。
3. 输入您的 PushPlus Token (获取地址: [http://www.pushplus.plus/](http://www.pushplus.plus/))。
4. 点击 "Save Configuration" 保存。
5. 开启 "Enable Monitoring Service" 开关。
6. 点击 "Test Push" 测试是否能收到微信通知。
7. 点击 "Request Ignore Battery Optimization" 确保应用不会被系统休眠。

## 常见问题
- 如果无法发送，请查看 App 内的 "Last Log"。
- 确保通知栏中有 "Monitoring SMS..." 的常驻通知。
- 检查网络连接是否正常。
