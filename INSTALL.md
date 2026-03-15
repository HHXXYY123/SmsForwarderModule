# Installation and Build Guide

This project is structured as an Android Studio project with a KernelSU/Magisk module directory.

## Prerequisites
- Android Studio or Gradle installed on your PC.
- Android SDK (target API 34).
- Java JDK 17 or higher.

## Building the APK

1. Open the `SmsForwarderModule` folder in Android Studio.
2. Ensure dependencies are synced.
3. Build the APK:
   - In Android Studio: `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`.
   - Using Gradle: Run `./gradlew :app:assembleDebug` (or `assembleRelease` if you sign it).

4. Locate the built APK:
   - Usually in `app/build/outputs/apk/debug/app-debug.apk`.

## Packaging the Module

1. Rename the built APK to `SmsForwarder.apk`.
2. Copy `SmsForwarder.apk` to the following path inside the project directory:
   `module/system/priv-app/SmsForwarder/`
   
   (Ensure the directory structure exists: `module/system/priv-app/SmsForwarder/SmsForwarder.apk`)

3. Navigate to the `module` directory.
4. Select all files inside the `module` directory (`module.prop`, `customize.sh`, `service.sh`, `system/` folder).
5. Zip them into a file named `SmsForwarderModule.zip`.
   - **Important**: Do not zip the `module` folder itself, zip the *contents* of the `module` folder.

## Flashing the Module

1. Transfer `SmsForwarderModule.zip` to your Android device.
2. Open KernelSU or Magisk Manager.
3. Go to the **Modules** tab.
4. Tap **Install from storage**.
5. Select the `SmsForwarderModule.zip` file.
6. Wait for installation and tap **Reboot**.

## Verification

1. After reboot, check if the app "SMS Forwarder" is installed.
2. Open the app and configure your PushPlus token.
3. Verify that the service is running and notifications appear.
