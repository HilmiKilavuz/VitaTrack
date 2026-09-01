package com.example.vitatrack.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.vitatrack.data.receiver.ReminderReceiver
import com.example.vitatrack.domain.alarm.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(supplementName: String, timeInMillis: Long) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("SUPPLEMENT_NAME", supplementName)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            supplementName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Alarm scheduled for $supplementName at $timeInMillis")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission not granted", e)
        }
    }

    override fun cancel(supplementName: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("SUPPLEMENT_NAME", supplementName)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            supplementName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Alarm cancelled for $supplementName")
    }
}
