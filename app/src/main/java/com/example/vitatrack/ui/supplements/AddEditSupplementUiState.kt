package com.example.vitatrack.ui.supplements

/**
 * Add/Edit ekranının anlık durumunu temsil eden tek bir veri sınıfı.
 * Ekrandaki tüm veriler (form değerleri, hata mesajları vb.) burada tutulur.
 */
data class AddEditSupplementUiState(
    val name: String = "",
    val dose: String = "",
    val reminderTime: String = "08:00",
    val isReminderEnabled: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)
