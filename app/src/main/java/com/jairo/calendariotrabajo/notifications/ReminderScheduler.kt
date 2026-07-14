package com.jairo.calendariotrabajo.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    fun schedule(hour: Int = 20, minute: Int = 0) {
        val delayMs = millisUntilNext(hour, minute)

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun millisUntilNext(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        val target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        val next = if (target.isBefore(now) || target.isEqual(now)) target.plusDays(1) else target
        return Duration.between(now, next).toMillis()
    }

    companion object {
        const val WORK_NAME = "mishoras_daily_reminder"
    }
}
