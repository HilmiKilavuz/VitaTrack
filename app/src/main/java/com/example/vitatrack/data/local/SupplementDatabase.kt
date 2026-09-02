package com.example.vitatrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vitatrack.domain.model.Supplement

/**
 * Room veritabanının kendisidir. Tüm tabloları ve DAO'ları burada tanımlarız.
 *
 * @Database anatasyonu Room'a şunu söyler:
 * - entities: Veritabanında hangi tablolar var? (Supplement sınıfı = "supplements" tablosu)
 * - version: Veritabanı şeması değiştiğinde sürümü artırırız (şu an ilk sürüm = 1)
 * - exportSchema: Şema geçmişini dosyaya kaydetmeyi kapatıyoruz (geliştirme kolaylığı)
 */
@Database(
    entities = [Supplement::class],
    version = 1,
    exportSchema = false
)
abstract class SupplementDatabase : RoomDatabase() {

    /**
     * Room bu fonksiyonu görünce DAO'nun gerçek implementasyonunu otomatik üretir.
     * Biz sadece abstract (soyut) olarak tanımlarız.
     */
    abstract fun supplementDao(): SupplementDao
}
