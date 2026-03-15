package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            if (!Config.isEnabled(context)) {
                Config.log(context, "Received SMS but service disabled")
                return
            }

            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<Any>?
                if (pdus != null) {
                    for (pdu in pdus) {
                        val sms = SmsMessage.createFromPdu(pdu as ByteArray)
                        val sender = sms.displayOriginatingAddress
                        val messageBody = sms.displayMessageBody
                        val timestamp = sms.timestampMillis
                        
                        val title = "SMS from $sender"
                        val content = "Time: ${java.util.Date(timestamp)}\nFrom: $sender\n\n$messageBody"
                        
                        Config.log(context, "Received SMS from $sender")
                        
                        val data = Data.Builder()
                            .putString("title", title)
                            .putString("content", content)
                            .build()

                        val uploadWorkRequest = OneTimeWorkRequestBuilder<PushWorker>()
                            .setInputData(data)
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
        }
    }
}
