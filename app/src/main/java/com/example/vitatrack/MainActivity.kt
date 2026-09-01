package com.example.vitatrack

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.vitatrack.domain.alarm.AlarmScheduler
import com.example.vitatrack.domain.notification.NotificationHelper
import com.example.vitatrack.ui.theme.VitaTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt'ten NotificationHelper ve AlarmScheduler'ı istiyoruz
    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    // Android 13+ için kullanıcıdan Bildirim İzni isteme penceresini (dialog) yöneten yapı
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Eğer kullanıcı izin verdiyse alarmı kur
        if (isGranted) {
            scheduleTestAlarm()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Uygulama açılır açılmaz bildirim kanalını oluşturuyoruz
        notificationHelper.createNotificationChannel()

        setContent {
            VitaTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        // Test Butonu
                        Button(onClick = { requestNotificationPermissionAndSchedule() }) {
                            Text("Test Notification (5 seconds)")
                        }
                    }
                }
            }
        }
    }

    /**
     * Android 13 ve üstü bir cihazsa önce bildirim izni var mı diye bakar,
     * yoksa izin ister. Eski bir cihazsa (veya izin zaten varsa) doğrudan alarmı kurar.
     */
    private fun requestNotificationPermissionAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    scheduleTestAlarm() // İzin zaten verilmiş
                }
                else -> {
                    // İzin penceresini ekranda göster
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Android 13'ten eskiyse zaten izin istemeye gerek yok
            scheduleTestAlarm()
        }
    }

    /**
     * Şu anki zamana 5000 milisaniye (5 saniye) ekleyerek sisteme test alarmı kurar.
     */
    private fun scheduleTestAlarm() {
        val time = System.currentTimeMillis() + 5000
        alarmScheduler.schedule("Vitamin D", time)
    }
}