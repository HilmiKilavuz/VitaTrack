package com.example.vitatrack.domain.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.vitatrack.domain.repository.SupplementRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * Cihaz yeniden başlatıldığında (Boot) Android tarafından otomatik tetiklenen dinleyici (Receiver).
 * AlarmManager alarmları cihaz kapanınca silindiği için, cihaz açıldığında
 * veritabanındaki aktif alarmları tekrar kurmamız gerekir.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var repository: SupplementRepository

    // BroadcastReceiver'lar çok kısa ömürlüdür. Coroutine çalıştırmak için kendi scope'umuzu oluşturuyoruz.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        // Yalnızca Boot tamamlandığında işlem yap
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            
            // Receiver'ın hemen ölmesini engellemek için goAsync çağırıyoruz
            val pendingResult = goAsync()
            
            scope.launch {
                try {
                    // Veritabanındaki listeyi bir kereye mahsus al (first() flow'un anlık durumunu döndürür)
                    val supplements = repository.getAllSupplements().first()
                    
                    supplements.forEach { supplement ->
                        if (supplement.isReminderEnabled) {
                            val parts = supplement.reminderTime.split(":")
                            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
                            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                            val calendar = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                                set(Calendar.SECOND, 0)
                                
                                // Saat geçmişse yarına kur
                                if (timeInMillis <= System.currentTimeMillis()) {
                                    add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }
                            
                            // Alarmı tekrar zamanla
                            alarmScheduler.schedule(supplement.name, calendar.timeInMillis)
                        }
                    }
                } finally {
                    // İşlem bittiğinde Android'e receiver'ın kapatılabileceğini haber ver
                    pendingResult.finish()
                }
            }
        }
    }
}
