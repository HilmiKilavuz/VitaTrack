package com.example.vitatrack.ui.supplements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.domain.model.Supplement
import com.example.vitatrack.domain.repository.SupplementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SupplementListScreen ekranının "beyin" kısmıdır.
 * UI (ekran) ile veritabanı arasındaki köprü görevi görür.
 *
 * @HiltViewModel: Hilt'in bu ViewModel'i oluşturabilmesi için gereklidir.
 * ViewModel, ekran dönse bile (örn: telefonu yana çevirince) verilerini KAYBETMEZ.
 */
@HiltViewModel
class SupplementListViewModel @Inject constructor(
    private val repository: SupplementRepository
) : ViewModel() {

    /**
     * Veritabanındaki tüm takviyeleri gerçek zamanlı dinleyen StateFlow.
     *
     * StateFlow: Anlık veri akışı. Liste değiştiğinde UI otomatik yenilenir.
     * stateIn: Flow'u StateFlow'a dönüştürür.
     * SharingStarted.WhileSubscribed(5000): Ekrandan 5 saniye sonra gidilirse
     * (ekran kapandıysa) veri akışını durdurur, pili korur.
     * initialValue: Uygulama ilk açıldığında liste boş görünür, veri yüklenince güncellenir.
     */
    val supplements: StateFlow<List<Supplement>> = repository
        .getAllSupplements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Bir takviyeyi silen fonksiyon.
     * launch: Silme işlemini arka planda yapar, UI donmaz.
     */
    fun deleteSupplement(supplement: Supplement) {
        viewModelScope.launch {
            repository.deleteSupplement(supplement)
        }
    }
}
