package com.example.smsforwarder

import android.content.Context
import android.content.SharedPreferences

object Config {
    private const val PREF_NAME = "SmsForwarderConfig"
    private const val KEY_TOKEN = "pushplus_token"
    private const val KEY_ENABLED = "service_enabled"
    private const val KEY_LAST_LOG = "last_log"

    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    fun setToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    fun isEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ENABLED, false)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun log(context: Context, message: String) {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val logMsg = "[$timestamp] $message"
        getPrefs(context).edit().putString(KEY_LAST_LOG, logMsg).apply()
    }

    fun getLastLog(context: Context): String {
        return getPrefs(context).getString(KEY_LAST_LOG, "No logs yet") ?: "No logs yet"
    }
}
