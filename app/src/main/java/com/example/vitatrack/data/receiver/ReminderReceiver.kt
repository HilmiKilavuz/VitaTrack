package com.example.vitatrack.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.vitatrack.domain.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "Alarm triggered")
        
        val supplementName = intent.getStringExtra("SUPPLEMENT_NAME") ?: "Your Supplement"
        
        notificationHelper.showNotification(
            title = "Time to take your supplement!",
            message = "It's time to take $supplementName.",
            notificationId = supplementName.hashCode()
        )
    }
}
