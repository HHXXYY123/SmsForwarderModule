package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.telephony.SmsMessage
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            if (!Config.isEnabled(context)) {
                Config.log(context, "收到短信，但服务未开启")
                return
            }

            // Acquire WakeLock to ensure CPU stays awake while we process the SMS and enqueue work
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "SmsForwarder::ReceiverWakeLock"
            )
            wakeLock.acquire(10000) // Hold for max 10 seconds

            try {
                val bundle = intent.extras
                if (bundle != null) {
                    val pdus = bundle.get("pdus") as Array<Any>?
                    if (pdus != null) {
                        for (pdu in pdus) {
                            val sms = SmsMessage.createFromPdu(pdu as ByteArray)
                            val sender = sms.displayOriginatingAddress ?: "未知发件人"
                            val messageBody = sms.displayMessageBody ?: ""
                            val timestamp = sms.timestampMillis
                            
                            val timeStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
                            
                            val titleTemplate = Config.getTitleTemplate(context)
                            val contentTemplate = Config.getContentTemplate(context)

                            val title = titleTemplate
                                .replace("{sender}", sender)
                                .replace("{time}", timeStr)
                                .replace("{content}", messageBody)
                                
                            val content = contentTemplate
                                .replace("{sender}", sender)
                                .replace("{time}", timeStr)
                                .replace("{content}", messageBody)
                            
                            Config.log(context, "收到来自 $sender 的短信")
                            
                            val data = Data.Builder()
                                .putString("title", title)
                                .putString("content", content)
                                .build()

                            // Use Expedited job to bypass Doze mode restrictions
                            val uploadWorkRequest = OneTimeWorkRequestBuilder<PushWorker>()
                                .setInputData(data)
                                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                                .setBackoffCriteria(
                                    BackoffPolicy.LINEAR,
                                    WorkRequest.MIN_BACKOFF_MILLIS,
                                    TimeUnit.MILLISECONDS
                                )
                                .build()

                            WorkManager.getInstance(context).enqueue(uploadWorkRequest)
                        }
                    }
                }
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }
        }
    }
}
