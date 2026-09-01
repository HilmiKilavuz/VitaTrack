package com.example.vitatrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Uygulamanın başladığı ilk noktadır (Application sınıfı).
 * @HiltAndroidApp anatasyonu, Dagger Hilt (Dependency Injection) sistemini başlatır.
 * Hilt, projenin geri kalanındaki tüm bağımlılıkları (ihtiyaç duyulan sınıfları) yönetecektir.
 */
@HiltAndroidApp
class VitaTrackApp : Application()
