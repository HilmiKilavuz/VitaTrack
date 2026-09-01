package com.example.vitatrack.di

import com.example.vitatrack.data.alarm.AlarmSchedulerImpl
import com.example.vitatrack.domain.alarm.AlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt için bağımlılıkların (dependency) nasıl sağlanacağını belirten modül sınıfı.
 * @Module: Bu sınıfın Hilt'e bir şeyler öğreteceğini belirtir.
 * @InstallIn(SingletonComponent::class): Burada sağlanan bağımlılıkların uygulama yaşadığı sürece
 * tek bir kopya (Singleton) olarak yaşamasını sağlar.
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
}
