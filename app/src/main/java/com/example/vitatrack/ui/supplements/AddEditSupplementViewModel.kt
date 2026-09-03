package com.example.vitatrack.ui.supplements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.domain.alarm.AlarmScheduler
import com.example.vitatrack.domain.model.Supplement
import com.example.vitatrack.domain.repository.SupplementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * Add/Edit ekranının ViewModel'i.
 * State (durum) yönetimini tek bir UiState sınıfı üzerinden yapar.
 */
@HiltViewModel
class AddEditSupplementViewModel @Inject constructor(
    private val repository: SupplementRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    // Artık tüm durumu (state) tek bir akışta (flow) tutuyoruz
    private val _uiState = MutableStateFlow(AddEditSupplementUiState())
    val uiState: StateFlow<AddEditSupplementUiState> = _uiState.asStateFlow()

    // Düzenleme modunda mevcut takviyenin ID'si saklanır; yeni ekleme = null
    private var editingId: Int? = null

    // State güncelleme fonksiyonları (update() mevcut state'in kopyasını çıkararak güvenli günceller)
    fun onNameChange(value: String) { 
        _uiState.update { it.copy(name = value, errorMessage = null) } 
    }
    
    fun onDoseChange(value: String) { 
        _uiState.update { it.copy(dose = value, errorMessage = null) } 
    }
    
    fun onReminderTimeChange(value: String) { 
        _uiState.update { it.copy(reminderTime = value) } 
    }
    
    fun onReminderEnabledChange(value: Boolean) { 
        _uiState.update { it.copy(isReminderEnabled = value) } 
    }

    /**
     * Düzenleme modunda açılırken mevcut takviyenin verilerini UiState'e doldurur.
     */
    fun loadSupplement(id: Int) {
        viewModelScope.launch {
            val supplement = repository.getSupplementById(id) ?: return@launch
            editingId = id
            _uiState.update { state ->
                state.copy(
                    name = supplement.name,
                    dose = supplement.dose,
                    reminderTime = supplement.reminderTime,
                    isReminderEnabled = supplement.isReminderEnabled
                )
            }
        }
    }

    /**
     * Formu kaydeder.
     */
    fun saveSupplement() {
        // O anki state kopyasını alıyoruz
        val currentState = _uiState.value

        // Basit validasyon
        if (currentState.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Supplement name cannot be empty.") }
            return
        }
        if (currentState.dose.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Dose cannot be empty.") }
            return
        }

        viewModelScope.launch {
            val supplement = Supplement(
                id = editingId ?: 0,
                name = currentState.name.trim(),
                dose = currentState.dose.trim(),
                reminderTime = currentState.reminderTime,
                isReminderEnabled = currentState.isReminderEnabled
            )

            if (editingId != null) {
                repository.updateSupplement(supplement)
            } else {
                repository.insertSupplement(supplement)
            }

            // Alarm yönetimi
            if (supplement.isReminderEnabled) {
                val parts = supplement.reminderTime.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 8
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                alarmScheduler.schedule(supplement.name, calendar.timeInMillis)
            } else {
                alarmScheduler.cancel(supplement.name)
            }

            // Kayıt başarılıysa UI'a sinyal gönder
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
