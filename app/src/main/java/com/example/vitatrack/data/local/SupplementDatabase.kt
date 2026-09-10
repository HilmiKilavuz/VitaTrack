package com.example.vitatrack.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.vitatrack.domain.model.Supplement

/**
 * Room veritabanının kendisidir. Tüm tabloları ve DAO'ları burada tanımlarız.
 *
 * @Database anatasyonu Room'a şunu söyler:
 * - entities: Veritabanında hangi tablolar var?
 * - version: Veritabanı şeması değiştiğinde sürümü artırırız.
 * - exportSchema: Şema geçmişini dosyaya kaydetmeyi kapatıyoruz.
 *
 * ── Migration Geçmişi ──────────────────────────────────
 * v1 → v2 : lastTakenDate (TEXT) ve streakCount (INTEGER) kolonları eklendi.
 *           Mevcut satırlar için varsayılan değer olarak '' ve 0 kullanılır.
 */
@Database(
    entities = [Supplement::class],
    version = 2,
    exportSchema = false
)
abstract class SupplementDatabase : RoomDatabase() {

    /**
     * Room bu fonksiyonu görünce DAO'nun gerçek implementasyonunu otomatik üretir.
     */
    abstract fun supplementDao(): SupplementDao

    companion object {
        /**
         * v1 → v2 migrasyonu.
         * Mevcut kullanıcının takviyeleri silinmez; sadece iki yeni sütun eklenir.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE supplements ADD COLUMN lastTakenDate TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE supplements ADD COLUMN streakCount INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
