package com.example.vitatrack.data.repository

import com.example.vitatrack.BuildConfig
import com.example.vitatrack.domain.repository.AiChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AiChatRepository'nin Groq API tabanlı implementasyonu.
 *
 * Groq, Llama / Gemma gibi açık kaynak modelleri kendi hızlandırıcı
 * donanımı (LPU) üzerinde ücretsiz çalıştırır.
 * Kayıt: https://console.groq.com — kart bilgisi gerekmez.
 *
 * API, OpenAI uyumlu REST formatı kullanır:
 * POST https://api.groq.com/openai/v1/chat/completions
 */
@Singleton
class AiChatRepositoryImpl @Inject constructor() : AiChatRepository {

    // OkHttp istemcisi: bağlantı & okuma zaman aşımları ayarlandı
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * System Prompt: Modele kim olduğunu ve nasıl davranacağını söylüyoruz.
     * Bu direktif kullanıcıya görünmez, sadece AI'yi yönlendirir.
     */
    private val systemPrompt = """
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
        - Kullanıcı sağlıkla alakasız bir şey sorarsa nazikçe "Yalnızca takviye ve sağlık konularında yardımcı olabilirim" de.
        - Her zaman Türkçe yanıt ver.
    """.trimIndent()

    /**
     * Sohbet geçmişi: Önceki mesajlar burada tutulur.
     * AI, context'i hatırlayarak tutarlı sohbet sürdürebilir.
     */
    private val conversationHistory = mutableListOf<JSONObject>()

    override suspend fun sendMessage(userMessage: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Kullanıcı mesajını geçmişe ekle
                conversationHistory.add(
                    JSONObject().apply {
                        put("role", "user")
                        put("content", userMessage)
                    }
                )

                // İstek gövdesi oluştur (OpenAI uyumlu format)
                val messagesArray = JSONArray().apply {
                    // 1. System prompt (her istekte gönderilir)
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    // 2. Sohbet geçmişi
                    conversationHistory.forEach { put(it) }
                }

                val requestBody = JSONObject().apply {
                    put("model", "llama-3.1-8b-instant")  // Groq'taki hızlı ve ücretsiz model
                    put("messages", messagesArray)
                    put("max_tokens", 1024)
                    put("temperature", 0.7)               // Dengeli yaratıcılık
                }.toString()

                // HTTP isteği
                val request = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBodyString = response.body?.string()
                    ?: return@withContext Result.failure(Exception("Boş yanıt alındı"))

                if (!response.isSuccessful) {
                    // API hata döndürdüyse (ör: geçersiz key) kullanıcıya anlaşılır mesaj ver
                    return@withContext Result.failure(
                        Exception("API Hatası ${response.code}: Lütfen API key'inizi kontrol edin.")
                    )
                }

                // Yanıtı parse et
                val jsonResponse = JSONObject(responseBodyString)
                val aiMessage = jsonResponse
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                // AI yanıtını da geçmişe ekle (sonraki mesajlarda AI bunu hatırlasın)
                conversationHistory.add(
                    JSONObject().apply {
                        put("role", "assistant")
                        put("content", aiMessage)
                    }
                )

                Result.success(aiMessage)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
