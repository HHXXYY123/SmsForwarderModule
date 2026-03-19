package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Config.log(context, "设备开机，正在启动服务...")
            if (Config.isEnabled(context)) {
                val serviceIntent = Intent(context, MonitorService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }
}
