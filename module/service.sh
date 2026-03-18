#!/system/bin/sh

# Enable ADB over Network (Port 5555)
setprop service.adb.tcp.port 5555
stop adbd
start adbd

# Wait for boot completion
while [ "$(getprop sys.boot_completed)" != "1" ]; do
  sleep 1
done

# Wait for system services to be fully up
sleep 30

PKG="com.example.smsforwarder"
ACT="com.example.smsforwarder.MainActivity"

# Grant battery optimization whitelist
dumpsys deviceidle whitelist +$PKG

# Ensure permissions are granted
pm grant $PKG android.permission.RECEIVE_SMS
pm grant $PKG android.permission.READ_SMS
pm grant $PKG android.permission.INTERNET
pm grant $PKG android.permission.RECEIVE_BOOT_COMPLETED

# Start the service/activity
am start -n "$PKG/$ACT" --es "auto_start" "true"

# Keep the process in memory if possible (though Android O+ limits this, foreground service is better)
# This script ensures it starts on boot even if the receiver fails
