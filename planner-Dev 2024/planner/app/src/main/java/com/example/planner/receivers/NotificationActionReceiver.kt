package com.example.planner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "COMPLETE" -> {
                // Lógica para marcar a tarefa como concluída
                val taskId = intent.getLongExtra("taskId", -1)
                if (taskId != -1L) {
                    // Atualizar a tarefa no banco de dados
                }
            }
            "SNOOZE" -> {
                // Lógica para adiar a notificação
                val taskId = intent.getLongExtra("taskId", -1)
                if (taskId != -1L) {
                    // Reagendar a notificação para 10 minutos depois
                    val newTime = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        add(Calendar.MINUTE, 10)
                    }
                    // Reagendar a notificação (usar a função scheduleNotification)
                }
            }
        }
    }
}