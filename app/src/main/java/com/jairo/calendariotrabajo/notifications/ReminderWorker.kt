package com.jairo.calendariotrabajo.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jairo.calendariotrabajo.MisHorasApplication
import com.jairo.calendariotrabajo.data.model.Shift
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as MisHorasApplication

        // Modo prueba: forzar una notificación inmediata (no reprograma la cadena).
        if (inputData.getBoolean(ReminderScheduler.KEY_FORCE, false)) {
            NotificationHelper.showReminder(
                applicationContext,
                "Notificación de prueba: toca para apuntar tus horas."
            )
            return Result.success()
        }

        // Si el usuario apagó las notificaciones, la cadena se detiene (no reprograma).
        if (!app.appPreferences.notificationsEnabled.first()) {
            return Result.success()
        }

        val pattern = app.shiftPatternRepository.get()
        if (pattern != null) {
            val shiftFor = { day: LocalDate ->
                app.shiftPatternRepository.expectedShiftFor(day, pattern)
            }
            val due = ReminderPlanner().dueSlot(LocalDateTime.now(), TOLERANCE_MINUTES, shiftFor)
            if (due != null) {
                val alreadyLogged = app.workDayRepository.getByDate(due.targetDay) != null
                if (!alreadyLogged) {
                    NotificationHelper.showReminder(applicationContext, messageFor(due))
                }
            }
        }

        // Mantiene viva la cadena: programa el siguiente aviso.
        app.reminderScheduler.scheduleNext(app.shiftPatternRepository)
        return Result.success()
    }

    private fun messageFor(slot: ReminderSlot): String {
        val shiftLabel = when (slot.shift) {
            Shift.MANANA -> "de mañana"
            Shift.TARDE -> "de tarde"
            Shift.NOCHE -> "de noche"
        }
        return "¿Apuntaste tu turno $shiftLabel? Toca para hacerlo."
    }

    companion object {
        // Margen para procesar un aviso aunque WorkManager se retrase un poco (Doze, etc.).
        private const val TOLERANCE_MINUTES = 180L
    }
}
