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

/**
 * AlarmScheduler arayüzünü (interface) uygulayan (implement eden) ana sınıftır.
 * Amacı: İşletim sistemine "Beni şu saatte uyandır" talimatını vermektir.
 */
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    /**
     * Alarmı kurar.
     * @param supplementName Hangi takviye için alarm kurulduğu
     * @param timeInMillis Alarmın çalacağı zaman (Milisaniye cinsinden - Örn: 16900021312)
     */
    override fun schedule(supplementName: String, timeInMillis: Long) {
        // Alarm çaldığında hangi sınıfın uyanacağını (ReminderReceiver) Intent ile belirtiyoruz
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("SUPPLEMENT_NAME", supplementName)
        }
        
        // PendingIntent: Android sistemine emanet ettiğimiz ve "Zamanı gelince bu Intent'i benim yerime çalıştır" dediğimiz yapı.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            supplementName.hashCode(), // Her alarm için benzersiz ID
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // setExactAndAllowWhileIdle: Telefon uyku modunda (Doze Mode) olsa bile, TAM saatinde uyandırıp alarmı çaldırır.
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, // RTC_WAKEUP: Ekran kapalıysa ekranı uyandırır
                timeInMillis,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Alarm scheduled for $supplementName at $timeInMillis")
        } catch (e: SecurityException) {
            // Android 12 ve sonrasında tam saatinde alarm kurmak için özel izin gerekir.
            Log.e("AlarmScheduler", "Exact alarm permission not granted", e)
        }
    }

    /**
     * Kurulu olan bir alarmı iptal eder.
     */
    override fun cancel(supplementName: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("SUPPLEMENT_NAME", supplementName)
        }
        
        // İptal etmek için, kurarken oluşturduğumuz BİREBİR aynı PendingIntent'i oluşturmalıyız
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            supplementName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // İşletim sisteminden bu alarmı silmesini istiyoruz
        alarmManager.cancel(pendingIntent)
        Log.d("AlarmScheduler", "Alarm cancelled for $supplementName")
    }
}
