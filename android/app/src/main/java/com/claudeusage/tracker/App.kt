package com.claudeusage.tracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "usage_alerts", "Usage alerts", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Claude usage thresholds, burn-rate, context, and service status" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
