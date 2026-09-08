package com.example.vitatrack.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.domain.model.ChatMessage
import com.example.vitatrack.domain.repository.AiChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * AiChatScreen'in ViewModel'i.
 * Ekrandaki tüm durumu (mesaj listesi, yükleniyor durumu, input metni) yönetir.
 *
 * @HiltViewModel ile Hilt bu sınıfı otomatik oluşturur ve AiChatRepository'yi enjekte eder.
 */
@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val repository: AiChatRepository
) : ViewModel() {

    /**
     * Ekranda görünen tüm mesajların listesi.
     * MutableStateFlow: Sadece bu ViewModel içinden değiştirilebilir.
     * UI bunu asStateFlow() üzerinden okur — salt okunur.
     */
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            // Vita'nın karşılama mesajı — uygulama ilk açıldığında görünür
            ChatMessage(
                text = "Merhaba! 👋 Ben Vita, sağlık asistanınım.\n\n" +
                        "Yaşadığınız belirtileri, yorgunluk, uyku sorunları veya sağlık hedeflerinizi " +
                        "bana anlatın; size uygun takviye önerileri sunayım.",
                isFromUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    /**
     * Kullanıcının mesaj kutusuna yazdığı metin.
     */
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    /**
     * AI'nin yanıt ürettiği sırada true olur.
     * Bu sürede gönder butonu devre dışı bırakılır.
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Kullanıcı mesaj kutusuna bir şey yazdığında çağrılır.
     */
    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    /**
     * Kullanıcı "Gönder" butonuna bastığında çağrılır.
     * 1. Kullanıcının mesajını listeye ekler.
     * 2. Yükleniyor balonunu (isLoading) gösterir.
     * 3. Repository üzerinden Gemini'ye soruyu iletir.
     * 4. Yanıtı alınca yükleniyor balonunu kaldırıp AI yanıtını ekler.
     */
    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank() || _isLoading.value) return

        // Input kutusunu hemen temizle
        _inputText.value = ""

        // Kullanıcı mesajını listeye ekle
        _messages.update { current ->
            current + ChatMessage(text = text, isFromUser = true)
        }

        _isLoading.value = true

        viewModelScope.launch {
            // AI yanıt üretirken gösterilen yükleniyor balonu
            _messages.update { current ->
                current + ChatMessage(text = "", isFromUser = false, isLoading = true)
            }

            // Gemini'ye gönder
            val result = repository.sendMessage(text)

            // Yükleniyor balonunu kaldır, gerçek yanıtı ekle
            _messages.update { current ->
                val withoutLoading = current.dropLast(1) // son eleman yükleniyor balonuydu
                withoutLoading + ChatMessage(
                    text = result.getOrElse {
                        "Üzgünüm, bir hata oluştu. İnternet bağlantınızı kontrol edip tekrar deneyin.\n\n" +
                                "Hata: ${it.message}"
                    },
                    isFromUser = false
                )
            }

            _isLoading.value = false
        }
    }
}
