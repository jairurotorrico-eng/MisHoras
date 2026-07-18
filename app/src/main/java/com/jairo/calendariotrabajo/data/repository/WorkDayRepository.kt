package com.jairo.calendariotrabajo.data.repository

import com.jairo.calendariotrabajo.data.db.dao.WorkDayDao
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

//Leer y guardar los días que trabaja. Funciones que necesitaran las pantallas mas adelante
class WorkDayRepository(private val workDayDao: WorkDayDao) {

    //importante la abstracción de mes
    fun observeMonth(year: Int, month: Int): Flow<List<WorkDayEntity>> {
        val start = LocalDate.of(year, month, 1)
        val end = start.plusMonths(1).minusDays(1)
        return workDayDao.observeRange(start, end)
    }

    fun observeRange(start: LocalDate, end: LocalDate): Flow<List<WorkDayEntity>> =
        workDayDao.observeRange(start, end)

    suspend fun getByDate(date: LocalDate): WorkDayEntity? =
        workDayDao.getByDate(date)

    suspend fun getRange(start: LocalDate, end: LocalDate): List<WorkDayEntity> =
        workDayDao.getRange(start, end)

    suspend fun getMonth(year: Int, month: Int): List<WorkDayEntity> {
        val start = LocalDate.of(year, month, 1)
        val end = start.plusMonths(1).minusDays(1)
        return workDayDao.getRange(start, end)
    }

    suspend fun save(day: WorkDayEntity) = workDayDao.upsert(day)

    suspend fun delete(day: WorkDayEntity) = workDayDao.delete(day)

    //Guardamos los últimos 5 meses de historial. Este método borrara todo lo anterior
    suspend fun pruneOlderThan(monthsToKeep: Int) {
        val cutoff = LocalDate.now()
            .withDayOfMonth(1)
            .minusMonths(monthsToKeep.toLong())
        workDayDao.deleteBefore(cutoff)
    }
}
