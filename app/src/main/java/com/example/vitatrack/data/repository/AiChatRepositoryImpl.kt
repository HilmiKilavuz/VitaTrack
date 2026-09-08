package com.example.vitatrack.data.repository

import com.example.vitatrack.BuildConfig
import com.example.vitatrack.domain.repository.AiChatRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AiChatRepository interface'inin gerçek implementasyonudur.
 * Gemini AI ile olan tüm iletişim bu sınıf üzerinden yürür.
 *
 * Hilt @Singleton: Uygulama boyunca tek bir GenerativeModel nesnesi oluşturulur,
 * bu da gereksiz kaynak tüketimini önler.
 */
@Singleton
class AiChatRepositoryImpl @Inject constructor() : AiChatRepository {

    /**
     * System Prompt: Modele kim olduğunu ve nasıl davranacağını söylüyoruz.
     * Bu direktif kullanıcıya görünmez, sadece AI'yi yönlendirir.
     */
    private val systemInstruction = content {
        text(
            """
            Sen VitaTrack uygulamasının sağlık asistanısın. Adın Vita.
            
            Görevin:
            - Kullanıcıların anlattığı şikayetleri, belirtileri veya sağlık hedeflerini dikkatlice dinle.
            - Bu bilgilere dayanarak hangi vitamin, mineral veya takviye maddelerinin faydalı olabileceğini, 
              günlük önerilen dozları ve alım zamanlarını (sabah/akşam, yemek öncesi/sonrası) açıkla.
            - Yanıtlarını sıcak, anlaşılır ve profesyonel bir dilde ver. Jargondan kaçın.
            - Her yanıtının SONUNA MUTLAKA şu uyarıyı ekle:
              "⚠️ Bu bilgiler yalnızca genel sağlık tavsiyesi niteliğindedir. Herhangi bir takviye kullanmadan önce mutlaka doktorunuza danışınız. Acil bir sağlık durumunda vakit kaybetmeden sağlık kuruluşuna başvurunuz."
            
            Sınırlamalar:
            - Kesinlikle ilaç adı önerme ve ilaç dozu verme. Sadece takviye (supplement) öner.
            - Tanı koyma. Yalnızca "Bu belirtiler X eksikliğine işaret edebilir" gibi ihtimalli ifadeler kullan.
            - Kullanıcı sağlıkla alakasız bir şey sorarsa nazikçe "Yalnızca takviye ve sağlık konularında yardımcı olabilirim" de ve konuya geri dön.
            - Her zaman Türkçe yanıt ver.
            """.trimIndent()
        )
    }

    /**
     * Gemini 1.5 Flash modeli: Ücretsiz katmanda hızlı ve yeterince güçlü.
     * system_instruction ile modele kalıcı bir rol veriyoruz.
     */
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = systemInstruction
    )

    /**
     * Sohbet geçmişini tutan nesne.
     * startChat() ile oluşturulan ChatSession, mesaj geçmişini hafızasında tutar,
     * böylece AI önceki mesajları hatırlayarak tutarlı yanıtlar verir.
     */
    private val chat = generativeModel.startChat()

    override suspend fun sendMessage(userMessage: String): Result<String> {
        return try {
            // sendMessage Gemini'ye mesaj gönderir ve yanıtı bekler (suspend fonksiyon)
            val response = chat.sendMessage(userMessage)
            val responseText = response.text ?: "Yanıt alınamadı, lütfen tekrar deneyin."
            Result.success(responseText)
        } catch (e: Exception) {
            // Herhangi bir ağ veya API hatası burada yakalanır
            Result.failure(e)
        }
    }
}
