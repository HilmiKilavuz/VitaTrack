package com.example.vitatrack.domain.repository

/**
 * Yapay zeka sohbet özelliğinin domain katmanındaki sözleşmesidir.
 * ViewModel bu interface'e bağımlıdır; gerçek implementasyona değil.
 * Bu sayede ilerleyen dönemde farklı bir AI sağlayıcısına geçmek kolaylaşır.
 */
interface AiChatRepository {
    /**
     * Kullanıcının mesajını AI'ye gönderir ve yanıtı döndürür.
     * suspend: Ağ çağrısı olduğu için coroutine içinde çalışır.
     * Result<String>: Hem başarılı yanıtı hem de hatayı güvenle taşır.
     */
    suspend fun sendMessage(userMessage: String): Result<String>
}
