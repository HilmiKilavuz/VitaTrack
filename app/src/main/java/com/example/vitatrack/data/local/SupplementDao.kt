package com.example.vitatrack.data.local

import androidx.room.*
import com.example.vitatrack.domain.model.Supplement
import kotlinx.coroutines.flow.Flow

/**
 * DAO = Data Access Object (Veri Erişim Nesnesi).
 * Room veritabanı üzerindeki tüm SQL sorgularını bu arayüzde tanımlarız.
 * Room, bu arayüzü görünce bizim için gerçek SQL kodunu otomatik üretir.
 * Biz hiç SQL yazmak zorunda kalmayız!
 */
@Dao
interface SupplementDao {

    /**
     * Tablodaki tüm takviyeleri ada göre sıralı getirir.
     * Flow döndürdüğü için, tabloya yeni kayıt eklenince
     * bu listeyi kullanan her yer otomatik güncellenir.
     */
    @Query("SELECT * FROM supplements ORDER BY name ASC")
    fun getAllSupplements(): Flow<List<Supplement>>

    /**
     * ID'ye göre tek bir takviye getirir.
     * suspend: Bu fonksiyon arka planda (coroutine içinde) çalışır.
     */
    @Query("SELECT * FROM supplements WHERE id = :id")
    suspend fun getSupplementById(id: Int): Supplement?

    /**
     * Yeni takviye ekler. Eğer aynı ID varsa üzerine yazar (REPLACE).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplement(supplement: Supplement)

    /**
     * Mevcut takviyeyi günceller.
     */
    @Update
    suspend fun updateSupplement(supplement: Supplement)

    /**
     * Takviyeyi tablodan siler.
     */
    @Delete
    suspend fun deleteSupplement(supplement: Supplement)
}
