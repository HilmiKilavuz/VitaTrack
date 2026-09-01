package com.example.vitatrack.domain.alarm

interface AlarmScheduler {
    fun schedule(supplementName: String, timeInMillis: Long)
    fun cancel(supplementName: String)
}
