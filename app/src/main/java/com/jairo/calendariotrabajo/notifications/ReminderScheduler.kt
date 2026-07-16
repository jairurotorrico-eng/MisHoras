package com.jairo.calendariotrabajo.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.jairo.calendariotrabajo.data.repository.ShiftPatternRepository
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    // Calcula el próximo aviso según el turno y encola el worker para esa hora.
    // Se llama al activar notificaciones, al arrancar la app y al final de cada worker (cadena).
    suspend fun scheduleNext(shiftPatternRepository: ShiftPatternRepository) {
        val pattern = shiftPatternRepository.get()
        val now = LocalDateTime.now()

        val delayMs = if (pattern == null) {
            // Sin patrón aún: revisamos en 12h por si se configura entre medias.
            Duration.ofHours(12).toMillis()
        } else {
            val next = ReminderPlanner().nextSlotAfter(now) { day ->
                shiftPatternRepository.expectedShiftFor(day, pattern)
            }
            next?.let { Duration.between(now, it.time).toMillis() }
                ?: Duration.ofHours(12).toMillis()
        }

        enqueue(delayMs.coerceAtLeast(0))
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    // Para probar sin esperar a una hora concreta: dispara el worker en 10s forzando la notificación.
    fun scheduleDebugFireNow() {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(workDataOf(KEY_FORCE to true))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private fun enqueue(delayMs: Long) {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    companion object {
        const val WORK_NAME = "mishoras_daily_reminder"
        const val KEY_FORCE = "force_fire"
    }
}
