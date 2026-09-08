package com.example.vitatrack.domain.model

/**
 * Sohbet ekranındaki tek bir mesajı temsil eder.
 *
 * @param text      Mesajın içeriği (kullanıcının sorusu ya da AI'nin yanıtı)
 * @param isFromUser true ise sağ tarafta kullanıcı balonu, false ise sol tarafta AI balonu gösterilir
 * @param isLoading  true ise AI henüz yanıt yazıyor demektir — animasyonlu "..." göstermek için kullanılır
 */
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val isLoading: Boolean = false
)
