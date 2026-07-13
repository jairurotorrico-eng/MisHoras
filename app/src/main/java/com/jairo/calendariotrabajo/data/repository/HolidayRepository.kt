package com.jairo.calendariotrabajo.data.repository

import com.jairo.calendariotrabajo.data.db.dao.HolidayDao
import com.jairo.calendariotrabajo.data.db.entity.HolidayEntity
import com.jairo.calendariotrabajo.data.holidays.SpanishHolidays
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HolidayRepository(private val holidayDao: HolidayDao) {//Inyección de dependencias

    fun observeAll(): Flow<List<HolidayEntity>> = holidayDao.observeAll()

    suspend fun isHoliday(date: LocalDate): Boolean =
        holidayDao.getByDate(date) != null

    suspend fun getInRange(start: LocalDate, end: LocalDate): List<HolidayEntity> =
        holidayDao.getRange(start, end)

    suspend fun ensureSeededFor(years: List<Int>) {
        val seededYears = holidayDao.getAll().map { it.date.year }.toSet()
        val missing = years.filter { it !in seededYears }
        if (missing.isNotEmpty()) {
            holidayDao.insertAll(SpanishHolidays.forYears(missing))
        }
    }
}
