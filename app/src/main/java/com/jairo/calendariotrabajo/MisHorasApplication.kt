package com.jairo.calendariotrabajo

import android.app.Application
import com.jairo.calendariotrabajo.data.db.AppDatabase
import com.jairo.calendariotrabajo.data.preferences.AppPreferences
import com.jairo.calendariotrabajo.data.repository.HolidayRepository
import com.jairo.calendariotrabajo.data.repository.SalaryRatesRepository
import com.jairo.calendariotrabajo.data.repository.ShiftPatternRepository
import com.jairo.calendariotrabajo.data.repository.WorkDayRepository
import com.jairo.calendariotrabajo.notifications.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

//EntryPoint Que Habre La Base de Datos. Extiende de Application (clase base de Android que se instancia una sola vez antes que cualquier activity)
//Usamos esta clase para crear una única instancia de la base de datos en cuanto la app arranca y tenerla disponible desde el inicio.
//Clase ideal para para inicializar todo lo que quieras que viva mientras la app viva
class MisHorasApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val holidayRepository: HolidayRepository by lazy {
        HolidayRepository(database.holidayDao())
    }

    val workDayRepository: WorkDayRepository by lazy {
        WorkDayRepository(database.workDayDao())
    }

    val salaryRatesRepository: SalaryRatesRepository by lazy {
        SalaryRatesRepository(database.salaryRatesDao())
    }

    val shiftPatternRepository: ShiftPatternRepository by lazy {
        ShiftPatternRepository(database.shiftPatternDao())
    }

    val appPreferences: AppPreferences by lazy { AppPreferences(this) }

    val reminderScheduler: ReminderScheduler by lazy { ReminderScheduler(this) }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val currentYear = LocalDate.now().year
            holidayRepository.ensureSeededFor((currentYear..currentYear + 4).toList())
            salaryRatesRepository.getOrCreateDefault()
            workDayRepository.pruneOlderThan(monthsToKeep = 6)

            val enabled = appPreferences.notificationsEnabled.first()
            val hour = appPreferences.reminderHour.first()
            if (enabled) {
                reminderScheduler.schedule(hour = hour)
            }
        }
    }
}
