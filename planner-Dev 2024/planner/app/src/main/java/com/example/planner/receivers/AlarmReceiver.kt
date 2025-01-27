package com.example.planner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.planner.services.NotificationService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, NotificationService::class.java).apply {
            putExtra("title", intent.getStringExtra("title"))
            putExtra("message", intent.getStringExtra("message"))
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}