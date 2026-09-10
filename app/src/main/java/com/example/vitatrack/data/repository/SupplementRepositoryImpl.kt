package com.example.vitatrack.data.repository

import com.example.vitatrack.data.local.SupplementDao
import com.example.vitatrack.domain.model.Supplement
import com.example.vitatrack.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * SupplementRepository arayüzünün gerçek implementasyonudur.
 * İçeride Room DAO'sunu kullanarak asıl veritabanı işlemlerini yapar.
 */
class SupplementRepositoryImpl @Inject constructor(
    private val dao: SupplementDao
) : SupplementRepository {

    override fun getAllSupplements(): Flow<List<Supplement>> = dao.getAllSupplements()

    override suspend fun getSupplementById(id: Int): Supplement? = dao.getSupplementById(id)

    override suspend fun insertSupplement(supplement: Supplement) = dao.insertSupplement(supplement)

    override suspend fun updateSupplement(supplement: Supplement) = dao.updateSupplement(supplement)

    override suspend fun deleteSupplement(supplement: Supplement) = dao.deleteSupplement(supplement)

    override suspend fun markAsTaken(supplement: Supplement) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()
        val todayStr = today.format(formatter)

        // Bugün zaten işaretlendiyse hiçbir şey yapma (idempotent)
        if (supplement.lastTakenDate == todayStr) return

        // Streak hesapla
        val newStreak = when {
            supplement.lastTakenDate.isBlank() -> {
                // İlk kez alınıyor
                1
            }
            else -> {
                val lastDate = runCatching {
                    LocalDate.parse(supplement.lastTakenDate, formatter)
                }.getOrNull()

                when {
                    lastDate == null -> 1                          // Parse hatası → sıfırla
                    lastDate == today.minusDays(1) -> supplement.streakCount + 1  // Dün alındı → devam
                    else -> 1                                      // Daha eski → streak koptu
                }
            }
        }

        dao.markAsTaken(
            id = supplement.id,
            lastTakenDate = todayStr,
            streakCount = newStreak
        )
    }
}
