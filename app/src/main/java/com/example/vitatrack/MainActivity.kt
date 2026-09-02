package com.example.vitatrack

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.vitatrack.domain.notification.NotificationHelper
import com.example.vitatrack.ui.supplements.SupplementListScreen
import com.example.vitatrack.ui.theme.VitaTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    // Android 13+ için Bildirim İzni isteme penceresini yöneten yapı
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* İzin verildi/reddedildi, şimdilik sadece logluyoruz */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Uygulama açılır açılmaz bildirim kanalını oluşturuyoruz
        notificationHelper.createNotificationChannel()

        // Android 13+ cihazlarda bildirim izni iste
        requestNotificationPermission()

        setContent {
            VitaTrackTheme {
                // Artık test butonu yerine gerçek listemizi gösteriyoruz
                SupplementListScreen()
            }
        }
    }

    /**
     * Android 13 ve üstü bir cihazsa önce bildirim izni var mı diye bakar,
     * yoksa izin ister. Eski bir cihazsa izin istemeye gerek yok.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // İzin penceresini ekranda göster
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}