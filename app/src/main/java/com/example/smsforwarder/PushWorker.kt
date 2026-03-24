package com.example.smsforwarder

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class PushWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "SMS Forwarder"
        val content = inputData.getString("content") ?: "No content"
        val token = Config.getToken(applicationContext)
        val topic = Config.getTopic(applicationContext)
        val channel = Config.getChannel(applicationContext)
        val template = Config.getTemplate(applicationContext)

        if (token.isNullOrEmpty()) {
            Config.log(applicationContext, "❌ 推送失败: Token 为空")
            return Result.failure()
        }

        Config.log(applicationContext, "正在发送推送...")

        val client = OkHttpClient()
        
        val json = JSONObject()
        json.put("token", token)
        json.put("title", title)
        json.put("content", content)
        json.put("template", template)
        
        if (topic.isNotEmpty()) {
            json.put("topic", topic)
        }
        if (channel.isNotEmpty()) {
            json.put("channel", channel)
        } 

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://www.pushplus.plus/send")
            .post(body)
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            if (response.isSuccessful) {
                Config.log(applicationContext, "✅ 推送成功: $title")
                Result.success()
            } else {
                Config.log(applicationContext, "❌ 推送失败: HTTP ${response.code} - $responseBody")
                Result.retry() // Retry on server error
            }
        } catch (e: IOException) {
            Config.log(applicationContext, "⚠️ 网络异常: ${e.message}")
            Result.retry() // Retry on network error
        } catch (e: Exception) {
            Config.log(applicationContext, "❌ 未知错误: ${e.message}")
            Result.failure()
        }
    }
}
