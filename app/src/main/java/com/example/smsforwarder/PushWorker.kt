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

        if (token.isNullOrEmpty()) {
            Config.log(applicationContext, "Error: Token is empty")
            return Result.failure()
        }

        Config.log(applicationContext, "Sending: $title")

        val client = OkHttpClient()
        val json = JSONObject()
        json.put("token", token)
        json.put("title", title)
        json.put("content", content)
        json.put("template", "html") 

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://www.pushplus.plus/send")
            .post(body)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                Config.log(applicationContext, "Success: Push sent")
                Result.success()
            } else {
                Config.log(applicationContext, "Error: HTTP ${response.code}")
                Result.retry() // Retry on server error
            }
        } catch (e: IOException) {
            Config.log(applicationContext, "Exception: ${e.message}")
            Result.retry() // Retry on network error
        }
    }
}
