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
 * Groq, Llama gibi açık kaynak modelleri kendi LPU donanımında ücretsiz çalıştırır.
 * Kayıt: https://console.groq.com — kart bilgisi gerekmez.
 * API, OpenAI uyumlu REST formatı kullanır.
 */
@Singleton
class AiChatRepositoryImpl @Inject constructor() : AiChatRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val systemPrompt = """
        Sen VitaTrack uygulamasının sağlık asistanısın. Adın Vita.
        
        Görevin:
        - Kullanıcıların anlattığı şikayetleri, belirtileri veya sağlık hedeflerini dikkatlice dinle.
        - Bu bilgilere dayanarak hangi vitamin, mineral veya takviye maddelerinin faydalı olabileceğini,
          günlük önerilen dozları ve alım zamanlarını (sabah/akşam, yemek öncesi/sonrası) açıkla.
        - Yanıtlarını sıcak, anlaşılır ve profesyonel bir dilde ver.
        - Her yanıtının SONUNA MUTLAKA şu uyarıyı ekle:
          "⚠️ Bu bilgiler yalnızca genel sağlık tavsiyesi niteliğindedir. Herhangi bir takviye kullanmadan önce mutlaka doktorunuza danışınız. Acil bir sağlık durumunda vakit kaybetmeden sağlık kuruluşuna başvurunuz."
        
        Sınırlamalar:
        - Kesinlikle ilaç adı önerme. Sadece takviye (supplement) öner.
        - Tanı koyma. İhtimalli ifadeler kullan.
        - Sağlıkla alakasız sorularda: "Yalnızca takviye ve sağlık konularında yardımcı olabilirim" de.
        - Her zaman Türkçe yanıt ver.
    """.trimIndent()

    /**
     * Sohbet geçmişi — her sendMessage çağrısında güncellenir.
     * Groq bu geçmişi okuyarak sohbetin bağlamını (context) hatırlar.
     */
    private val conversationHistory = mutableListOf<Pair<String, String>>() // role -> content

    override suspend fun sendMessage(userMessage: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1) Kullanıcı mesajını geçmişe ekle
                conversationHistory.add("user" to userMessage)

                // 2) messages dizisini string olarak elle inşa et (JSONObject Double sorununu önler)
                val messagesBuilder = StringBuilder("[")

                // System prompt — ilk sırada
                messagesBuilder.append(
                    """{"role":"system","content":${JSONObject.quote(systemPrompt)}}"""
                )

                // Geçmişte ki tüm kullanıcı / asistan mesajları
                for ((role, content) in conversationHistory) {
                    messagesBuilder.append(",")
                    messagesBuilder.append("""{"role":"$role","content":${JSONObject.quote(content)}}""")
                }
                messagesBuilder.append("]")

                // 3) İstek gövdesini string olarak oluştur
                val requestBodyJson = """
                    {
                        "model": "groq/compound-mini",
                        "messages": ${messagesBuilder},
                        "max_tokens": 1024,
                        "temperature": 0.7,
                        "stream": false
                    }
                """.trimIndent()

                // 4) HTTP POST isteği
                val request = Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBodyJson.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBodyStr = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    // Hata durumunda API'nin tam yanıtını da göster (debug için faydalı)
                    conversationHistory.removeLastOrNull() // başarısız mesajı geçmişten çıkar
                    return@withContext Result.failure(
                        Exception("HTTP ${response.code} — $responseBodyStr")
                    )
                }

                // 5) Başarılı yanıtı parse et
                val aiMessage = JSONObject(responseBodyStr)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                // 6) AI yanıtını da geçmişe ekle
                conversationHistory.add("assistant" to aiMessage)

                Result.success(aiMessage)

            } catch (e: Exception) {
                conversationHistory.removeLastOrNull()
                Result.failure(e)
            }
        }
    }
}
