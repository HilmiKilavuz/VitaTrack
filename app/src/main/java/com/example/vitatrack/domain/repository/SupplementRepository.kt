package com.example.vitatrack.domain.repository

import com.example.vitatrack.domain.model.Supplement
import kotlinx.coroutines.flow.Flow

/**
 * Takviye verilerine nasıl erişileceğini tanımlayan sözleşme (arayüz/interface).
 *
 * ViewModel bu interface'i kullanır; gerçekte Room'a mı yoksa bir sunucuya mı
 * gideceğini bilmek zorunda değildir. Bu sayede katmanlar birbirinden bağımsız kalır.
 *
 * Flow: Verinin anlık (reactive) olarak takip edilmesini sağlar.
 * Veritabanına yeni kayıt eklendiğinde Flow otomatik olarak güncellenir,
 * UI'ı yeniden çizmek için ekstra kod yazmamıza gerek kalmaz.
 */
interface SupplementRepository {

    // Tüm takviyeleri gerçek zamanlı olarak dinler
    fun getAllSupplements(): Flow<List<Supplement>>

    // ID'ye göre tek bir takviye getirir
    suspend fun getSupplementById(id: Int): Supplement?

    // Yeni takviye ekler (suspend: arka planda çalışır, UI'ı dondurmaz)
    suspend fun insertSupplement(supplement: Supplement)

    // Mevcut takviyeyi günceller
    suspend fun updateSupplement(supplement: Supplement)

    // Takviyeyi siler
    suspend fun deleteSupplement(supplement: Supplement)
}
