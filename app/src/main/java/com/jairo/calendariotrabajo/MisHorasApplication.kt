package com.jairo.calendariotrabajo

import android.app.Application
import com.jairo.calendariotrabajo.data.db.AppDatabase
import com.jairo.calendariotrabajo.data.repository.HolidayRepository
import com.jairo.calendariotrabajo.data.repository.SalaryRatesRepository
import com.jairo.calendariotrabajo.data.repository.ShiftPatternRepository
import com.jairo.calendariotrabajo.data.repository.WorkDayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//lazamos 3 cosas en la corrutina de arranque -->1.Siembra festicos 2.Crea las tarifas por defecto 3.Elimina registtros viejos. Secuenciales pero todo dentro
    // de una corrutina Dispatches.IO
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val currentYear = LocalDate.now().year
            holidayRepository.ensureSeededFor((currentYear..currentYear + 4).toList())
            salaryRatesRepository.getOrCreateDefault()
            workDayRepository.pruneOlderThan(monthsToKeep = 6)
        }
    }
}
