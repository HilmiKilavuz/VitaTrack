package com.example.vitatrack.data.repository

import com.example.vitatrack.data.local.SupplementDao
import com.example.vitatrack.domain.model.Supplement
import com.example.vitatrack.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * SupplementRepository arayüzünün gerçek implementasyonudur.
 * İçeride Room DAO'sunu kullanarak asıl veritabanı işlemlerini yapar.
 *
 * ViewModel sadece SupplementRepository interface'ini tanır,
 * bu implementasyonu hiç görmez. Bu da testlerde sahte (fake) bir
 * repository kullanmamızı kolaylaştırır.
 */
class SupplementRepositoryImpl @Inject constructor(
    private val dao: SupplementDao // Hilt bu DAO'yu otomatik sağlar
) : SupplementRepository {

    override fun getAllSupplements(): Flow<List<Supplement>> {
        return dao.getAllSupplements()
    }

    override suspend fun getSupplementById(id: Int): Supplement? {
        return dao.getSupplementById(id)
    }

    override suspend fun insertSupplement(supplement: Supplement) {
        dao.insertSupplement(supplement)
    }

    override suspend fun updateSupplement(supplement: Supplement) {
        dao.updateSupplement(supplement)
    }

    override suspend fun deleteSupplement(supplement: Supplement) {
        dao.deleteSupplement(supplement)
    }
}
