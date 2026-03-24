package com.example.smsforwarder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

object Config {
    const val ACTION_LOG_UPDATED = "com.example.smsforwarder.LOG_UPDATED"
    
    private const val PREF_NAME = "SmsForwarderConfig"
    private const val KEY_TOKEN = "pushplus_token"
    private const val KEY_TOPIC = "pushplus_topic"
    private const val KEY_CHANNEL = "pushplus_channel"
    private const val KEY_TEMPLATE = "pushplus_template"
    private const val KEY_ENABLED = "service_enabled"
    private const val KEY_LAST_LOG = "last_log"
    private const val KEY_TITLE_TEMPLATE = "title_template"
    private const val KEY_CONTENT_TEMPLATE = "content_template"

    const val DEFAULT_TITLE_TEMPLATE = "短信转发：{sender}"
    const val DEFAULT_CONTENT_TEMPLATE = "时间: {time}\n发件人: {sender}\n\n内容: {content}"

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    fun setToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getTopic(context: Context): String {
        return getPrefs(context).getString(KEY_TOPIC, "") ?: ""
    }

    fun setTopic(context: Context, topic: String) {
        getPrefs(context).edit().putString(KEY_TOPIC, topic).apply()
    }

    fun getChannel(context: Context): String {
        return getPrefs(context).getString(KEY_CHANNEL, "wechat") ?: "wechat"
    }

    fun setChannel(context: Context, channel: String) {
        getPrefs(context).edit().putString(KEY_CHANNEL, channel).apply()
    }

    fun getTemplate(context: Context): String {
        return getPrefs(context).getString(KEY_TEMPLATE, "html") ?: "html"
    }

    fun setTemplate(context: Context, template: String) {
        getPrefs(context).edit().putString(KEY_TEMPLATE, template).apply()
    }

    fun isEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ENABLED, false)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun getTitleTemplate(context: Context): String {
        return getPrefs(context).getString(KEY_TITLE_TEMPLATE, DEFAULT_TITLE_TEMPLATE) ?: DEFAULT_TITLE_TEMPLATE
    }

    fun setTitleTemplate(context: Context, template: String) {
        getPrefs(context).edit().putString(KEY_TITLE_TEMPLATE, template).apply()
    }

    fun getContentTemplate(context: Context): String {
        return getPrefs(context).getString(KEY_CONTENT_TEMPLATE, DEFAULT_CONTENT_TEMPLATE) ?: DEFAULT_CONTENT_TEMPLATE
    }

    fun setContentTemplate(context: Context, template: String) {
        getPrefs(context).edit().putString(KEY_CONTENT_TEMPLATE, template).apply()
    }

    @Synchronized
    fun log(context: Context, message: String) {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val newLog = "[$timestamp] $message"
        
        val prefs = getPrefs(context)
        var logs = prefs.getString(KEY_LAST_LOG, "") ?: ""
        
        // 保留最近的 10 条日志
        val logList = logs.split("\n").filter { it.isNotBlank() }.toMutableList()
        logList.add(0, newLog)
        if (logList.size > 10) {
            logList.removeAt(logList.size - 1)
        }
        
        prefs.edit().putString(KEY_LAST_LOG, logList.joinToString("\n")).apply()
        
        // 发送广播通知 UI 更新
        val intent = Intent(ACTION_LOG_UPDATED)
        intent.setPackage(context.packageName)
        context.sendBroadcast(intent)
    }

    fun getLastLog(context: Context): String {
        val logs = getPrefs(context).getString(KEY_LAST_LOG, "暂无日志") ?: "暂无日志"
        return if (logs.isBlank()) "暂无日志" else logs
    }
}
