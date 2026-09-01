package com.example.vitatrack.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.vitatrack.domain.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BroadcastReceiver (Yayın Alıcısı): Android sisteminde arka planda gerçekleşen
 * olayları (örneğin "Alarm çaldı!", "Telefon yeniden başladı") yakalayan dinleyicilerdir.
 * 
 * @AndroidEntryPoint: Hilt'in bu sınıfa bağımlılık (NotificationHelper) enjekte edebilmesi için gereklidir.
 */
@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    /**
     * Alarm vakti geldiğinde Android sistemi otomatik olarak bu metodu tetikler.
     * Uygulama kapalı olsa bile bu metod çalışır!
     */
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "Alarm triggered")
        
        // Intent'in içine daha önceden koyduğumuz ilacın/takviyenin adını alıyoruz
        val supplementName = intent.getStringExtra("SUPPLEMENT_NAME") ?: "Your Supplement"
        
        // NotificationHelper sınıfımızı kullanarak ekrana bildirimi gönderiyoruz
        notificationHelper.showNotification(
            title = "Time to take your supplement!",
            message = "It's time to take $supplementName.",
            notificationId = supplementName.hashCode() // İlacın ismine göre benzersiz bir ID oluşturur
        )
    }
}
