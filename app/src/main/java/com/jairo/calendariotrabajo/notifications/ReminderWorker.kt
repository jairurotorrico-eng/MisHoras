package com.jairo.calendariotrabajo.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jairo.calendariotrabajo.MisHorasApplication
import java.time.LocalDate

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as MisHorasApplication
        val today = LocalDate.now()
        val existing = app.workDayRepository.getByDate(today)
        if (existing == null) {
            NotificationHelper.showReminder(applicationContext)
        }
        return Result.success()
    }
}
