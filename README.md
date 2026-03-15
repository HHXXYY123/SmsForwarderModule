# SMS Forwarder to WeChat (PushPlus)

This is a KernelSU/Magisk module that installs an Android system app to forward SMS messages from a rooted Android device to WeChat via the PushPlus API.

## Features
- **Automatic Forwarding**: Listens for incoming SMS and forwards them instantly.
- **PushPlus Integration**: Uses PushPlus API for reliable WeChat notifications.
- **Background Service**: Includes a foreground service to keep the process alive.
- **Battery Optimization**: Automatically requests to ignore battery optimizations and uses `dumpsys` whitelist via root.
- **Boot Start**: Automatically starts on device boot.
- **Retry Mechanism**: Uses Android WorkManager for reliable delivery and retries on network failure.
- **Configuration UI**: Simple app interface to set your PushPlus token and test the connection.

## Requirements
- Android 8.0+ (SDK 26+)
- Rooted with KernelSU or Magisk
- Internet connection

## Installation
1. Download the module zip (or build it yourself, see INSTALL.md).
2. Open KernelSU/Magisk Manager.
3. Go to "Modules" -> "Install from storage".
4. Select the zip file.
5. Reboot.

## Configuration
1. After reboot, open the "SMS Forwarder" app from your launcher.
2. Grant the required permissions (SMS, Notification).
3. Enter your PushPlus Token (get it from [http://www.pushplus.plus/](http://www.pushplus.plus/)).
4. Click "Save Configuration".
5. Toggle "Enable Monitoring Service".
6. Click "Test Push" to verify.
7. Click "Request Ignore Battery Optimization" to ensure the app is not killed by the system.

## Troubleshooting
- If messages are not sent, check the "Last Log" in the app.
- Ensure the "Monitoring SMS..." notification is visible.
- Check your internet connection.
