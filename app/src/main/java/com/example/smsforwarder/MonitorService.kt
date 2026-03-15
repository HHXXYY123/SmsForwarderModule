package com.example.smsforwarder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MonitorService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        try {
            startForegroundServiceSafe()
        } catch (e: Exception) {
            e.printStackTrace()
            // 最后的挣扎：如果前面都失败，尝试用最简单的 ID 启动
            try {
                startForeground(1, Notification())
            } catch (ignored: Exception) {}
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            startForegroundServiceSafe()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Config.log(this, "服务已启动")
        return START_STICKY
    }

    private fun startForegroundServiceSafe() {
        createNotificationChannel()
        
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        // 使用系统自带的 chat 图标，通常是安全的
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("短信转发助手")
            .setContentText("正在后台监听短信...")
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "短信监听服务通知",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示服务运行状态"
                enableLights(false)
                enableVibration(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Config.log(this, "服务已停止")
    }

    companion object {
        const val CHANNEL_ID = "SmsMonitorChannel"
    }
}
