package com.example.vitatrack.domain.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Uygulama genelinde bildirim işlemlerini yöneten yardımcı sınıf.
 * 
 * @Inject constructor: Dagger Hilt'in bu sınıfı otomatik olarak üretebilmesi ve
 * ApplicationContext'i (uygulamanın bağlamını) içeriye verebilmesi (inject etmesi) içindir.
 */
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "vitatrack_reminder_channel"
        const val CHANNEL_NAME = "Supplement Reminders"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Android 8.0 (Oreo) ve sonrasında, bildirim gönderebilmek için mutlaka 
     * bir "Bildirim Kanalı (Notification Channel)" oluşturmamız gerekir.
     * Bu fonksiyon, uygulamanın başlangıcında çağrılarak kanalı hazırlar.
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for your supplements"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Bildirimi hazırlayıp ekranda gösteren fonksiyondur.
     * 
     * @param title Bildirimin başlığı
     * @param message Bildirimin içindeki açıklama yazısı
     * @param notificationId Aynı anda birden fazla bildirim geldiğinde üst üste binmemesi için benzersiz bir ID.
     */
    fun showNotification(title: String, message: String, notificationId: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder) // Geçici Android ikonu
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Bildirimin üste çıkmasını ve ses çıkarmasını sağlar
            .setAutoCancel(true) // Kullanıcı bildirime tıklayınca bildirimi siler

        // Bildirimi fırlat!
        notificationManager.notify(notificationId, builder.build())
    }
}
