package com.example.vitatrack.di

import android.content.Context
import androidx.room.Room
import com.example.vitatrack.data.alarm.AlarmSchedulerImpl
import com.example.vitatrack.data.local.SupplementDao
import com.example.vitatrack.data.local.SupplementDatabase
import com.example.vitatrack.data.repository.SupplementRepositoryImpl
import com.example.vitatrack.domain.alarm.AlarmScheduler
import com.example.vitatrack.domain.repository.SupplementRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt için bağımlılıkların nasıl sağlanacağını belirten modül sınıfı.
 * @Module: Bu sınıfın Hilt'e bir şeyler öğreteceğini belirtir.
 * @InstallIn(SingletonComponent::class): Burada sağlanan bağımlılıkların uygulama
 * yaşadığı sürece tek bir kopya (Singleton) olarak yaşamasını sağlar.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Interface'leri bağlamak için @Binds kullanılır.
     * Hilt, ne zaman birisi 'AlarmScheduler' isterse ona 'AlarmSchedulerImpl' verecektir.
     */
    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        alarmSchedulerImpl: AlarmSchedulerImpl
    ): AlarmScheduler

    /**
     * SupplementRepository interface'i istendiğinde
     * SupplementRepositoryImpl gerçek implementasyonu verilir.
     */
    @Binds
    @Singleton
    abstract fun bindSupplementRepository(
        supplementRepositoryImpl: SupplementRepositoryImpl
    ): SupplementRepository

    companion object {

        /**
         * @Provides: Sınıf oluşturmayı biz yönetmek istediğimizde kullanılır.
         * Room veritabanı nesnesi oluşturmak için özel bir Builder gerektiğinden
         * @Provides ile elle oluşturuyoruz.
         */
        @Provides
        @Singleton
        fun provideSupplementDatabase(
            @ApplicationContext context: Context
        ): SupplementDatabase {
            return Room.databaseBuilder(
                context,
                SupplementDatabase::class.java,
                "vitatrack_db" // Veritabanının dosya adı (telefonda bu isimle saklanır)
            ).build()
        }

        /**
         * DAO'yu sağlamak için önce Database'e ihtiyacımız var.
         * Hilt yukarıdaki fonksiyonla Database'i oluşturduktan sonra
         * buraya geçirip DAO'yu üretir.
         */
        @Provides
        @Singleton
        fun provideSupplementDao(database: SupplementDatabase): SupplementDao {
            return database.supplementDao()
        }
    }
}
