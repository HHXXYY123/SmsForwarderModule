package com.example.smsforwarder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data

class MainActivity : AppCompatActivity() {

    private lateinit var etToken: EditText
    private lateinit var switchService: Switch
    private lateinit var tvStatus: TextView
    private lateinit var tvLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etToken = findViewById(R.id.et_token)
        switchService = findViewById(R.id.switch_service)
        tvStatus = findViewById(R.id.tv_status)
        tvLog = findViewById(R.id.tv_log)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnTest = findViewById<Button>(R.id.btn_test)
        val btnBattery = findViewById<Button>(R.id.btn_battery)

        // Load config
        etToken.setText(Config.getToken(this))
        val isEnabled = Config.isEnabled(this)
        switchService.isChecked = isEnabled
        updateServiceStatus(isEnabled)
        tvLog.text = Config.getLastLog(this)

        // Check permissions
        checkPermissions()

        btnSave.setOnClickListener {
            val token = etToken.text.toString().trim()
            if (token.isEmpty()) {
                Toast.makeText(this, "Token cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Config.setToken(this, token)
            Toast.makeText(this, "Config Saved", Toast.LENGTH_SHORT).show()
        }

        switchService.setOnCheckedChangeListener { _, isChecked ->
            Config.setEnabled(this, isChecked)
            if (isChecked) {
                startMonitorService()
            } else {
                stopMonitorService()
            }
            updateServiceStatus(isChecked)
        }

        btnTest.setOnClickListener {
            val token = Config.getToken(this)
            if (token.isNullOrEmpty()) {
                Toast.makeText(this, "Save token first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val data = Data.Builder()
                .putString("title", "Test Message")
                .putString("content", "This is a test message from SMS Forwarder")
                .build()

            val request = OneTimeWorkRequestBuilder<PushWorker>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(this).enqueue(request)
            Toast.makeText(this, "Test Push Sent", Toast.LENGTH_SHORT).show()
        }

        btnBattery.setOnClickListener {
            requestBatteryOptimization()
        }
    }

    private fun updateServiceStatus(isRunning: Boolean) {
        if (isRunning) {
            tvStatus.text = getString(R.string.service_status_running)
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.teal_700))
        } else {
            tvStatus.text = getString(R.string.service_status_stopped)
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    private fun startMonitorService() {
        val intent = Intent(this, MonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopMonitorService() {
        val intent = Intent(this, MonitorService::class.java)
        stopService(intent)
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
        }
    }

    private fun requestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Already ignoring battery optimizations", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        tvLog.text = Config.getLastLog(this)
    }
}
