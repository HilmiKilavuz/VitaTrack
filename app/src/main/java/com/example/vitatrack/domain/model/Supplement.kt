package com.example.vitatrack.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room veritabanındaki "supplements" tablosunu temsil eden veri sınıfıdır.
 * @Entity anatasyonu Room'a "Bu bir veritabanı tablosudur" der.
 * tableName ile tablonun SQL içindeki adını belirliyoruz.
 *
 * Her property (alan) tablodaki bir sütuna (column) karşılık gelir.
 */
@Entity(tableName = "supplements")
data class Supplement(
    // @PrimaryKey: Tablodaki her satırı benzersiz şekilde tanımlayan alan.
    // autoGenerate = true: Her yeni kayıt eklendiğinde ID otomatik artar (1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Takviyenin adı (örn: "Vitamin D")
    val name: String,

    // Dozu (örn: "1000 mg")
    val dose: String,

    // Hatırlatma saati — "HH:mm" formatında string olarak tutuyoruz (örn: "09:30")
    val reminderTime: String,

    // Hatırlatma aktif mi? true = açık, false = kapalı
    val isReminderEnabled: Boolean = false
)
