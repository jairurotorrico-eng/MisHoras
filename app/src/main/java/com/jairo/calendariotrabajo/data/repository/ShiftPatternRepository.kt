package com.jairo.calendariotrabajo.data.repository

import com.jairo.calendariotrabajo.data.db.dao.ShiftPatternDao
import com.jairo.calendariotrabajo.data.db.entity.ShiftPatternEntity
import com.jairo.calendariotrabajo.data.model.Shift
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

//cerebro de rotacion de turnos, matemática que calcula que turno toca basandose en un ancla
class ShiftPatternRepository(private val shiftPatternDao: ShiftPatternDao) {

    fun observe(): Flow<ShiftPatternEntity?> = shiftPatternDao.observe()

    suspend fun get(): ShiftPatternEntity? = shiftPatternDao.get()

    suspend fun set(pattern: ShiftPatternEntity) = shiftPatternDao.upsert(pattern)
//aqui el calculo
    fun expectedShiftFor(date: LocalDate, pattern: ShiftPatternEntity): Shift {
        // El ciclo son SOLO los turnos que la persona hace, en orden canónico
        // (mañana -> tarde -> noche). Si hace uno solo, no hay rotación.
        val rotation = Shift.entries.filter { it in pattern.activeShifts }
        if (rotation.isEmpty()) return pattern.anchorShift
        if (rotation.size == 1) return rotation.first()

        val anchorIdx = rotation.indexOf(pattern.anchorShift).coerceAtLeast(0)
        val targetMonday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weeksElapsed = ChronoUnit.WEEKS.between(pattern.anchorWeekMonday, targetMonday).toInt()
        val targetIdx = ((anchorIdx + weeksElapsed) % rotation.size + rotation.size) % rotation.size
        return rotation[targetIdx]
    }
}
