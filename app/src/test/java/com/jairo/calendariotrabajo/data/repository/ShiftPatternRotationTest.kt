package com.jairo.calendariotrabajo.data.repository

import com.jairo.calendariotrabajo.data.db.dao.ShiftPatternDao
import com.jairo.calendariotrabajo.data.db.entity.ShiftPatternEntity
import com.jairo.calendariotrabajo.data.model.Shift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ShiftPatternRotationTest {

    // Un "fake": una implementación de mentira del DAO, solo para poder construir
    // el repositorio en el test. expectedShiftFor no toca la base de datos,
    // así que con esto basta y no necesitamos un móvil ni SQLite.
    private class FakeShiftPatternDao : ShiftPatternDao {
        override fun observe(): Flow<ShiftPatternEntity?> = flowOf(null)
        override suspend fun get(): ShiftPatternEntity? = null
        override suspend fun upsert(pattern: ShiftPatternEntity) = Unit
    }

    private val repo = ShiftPatternRepository(FakeShiftPatternDao())

    // 13 de julio de 2026 es lunes
    private val monday = LocalDate.of(2026, 7, 13)

    private fun pattern(anchor: Shift, active: List<Shift>) = ShiftPatternEntity(
        anchorWeekMonday = monday,
        anchorShift = anchor,
        activeShifts = active
    )

    @Test
    fun `un solo turno no rota nunca`() {
        val p = pattern(Shift.MANANA, listOf(Shift.MANANA))

        assertEquals(Shift.MANANA, repo.expectedShiftFor(monday, p))
        assertEquals(Shift.MANANA, repo.expectedShiftFor(monday.plusWeeks(1), p))
        assertEquals(Shift.MANANA, repo.expectedShiftFor(monday.plusWeeks(7), p))
    }

    @Test
    fun `solo noches se mantiene siempre en noche`() {
        val p = pattern(Shift.NOCHE, listOf(Shift.NOCHE))

        assertEquals(Shift.NOCHE, repo.expectedShiftFor(monday, p))
        assertEquals(Shift.NOCHE, repo.expectedShiftFor(monday.plusWeeks(3), p))
    }

    @Test
    fun `dos turnos alternan cada semana`() {
        val p = pattern(Shift.MANANA, listOf(Shift.MANANA, Shift.TARDE))

        assertEquals(Shift.MANANA, repo.expectedShiftFor(monday, p))
        assertEquals(Shift.TARDE, repo.expectedShiftFor(monday.plusWeeks(1), p))
        assertEquals(Shift.MANANA, repo.expectedShiftFor(monday.plusWeeks(2), p))
        assertEquals(Shift.TARDE, repo.expectedShiftFor(monday.plusWeeks(3), p))
    }

    @Test
    fun `tarde y noche alternan sin pasar por manana`() {
        val p = pattern(Shift.TARDE, listOf(Shift.TARDE, Shift.NOCHE))

        assertEquals(Shift.TARDE, repo.expectedShiftFor(monday, p))
        assertEquals(Shift.NOCHE, repo.expectedShiftFor(monday.plusWeeks(1), p))
        assertEquals(Shift.TARDE, repo.expectedShiftFor(monday.plusWeeks(2), p))
    }

    @Test
    fun `los tres turnos rotan manana tarde noche`() {
        val p = pattern(Shift.MANANA, Shift.entries.toList())

        assertEquals(Shift.MANANA, repo.expectedShiftFor(monday, p))
        assertEquals(Shift.TARDE, repo.expectedShiftFor(monday.plusWeeks(1), p))
        assertEquals(Shift.NOCHE, repo.expectedShiftFor(monday.plusWeeks(2), p))
        assertEquals(Shift.MANANA, repo.expectedShiftFor(monday.plusWeeks(3), p))
    }

    @Test
    fun `todos los dias de una misma semana tienen el mismo turno`() {
        val p = pattern(Shift.TARDE, Shift.entries.toList())

        (0..6).forEach { offset ->
            assertEquals(Shift.TARDE, repo.expectedShiftFor(monday.plusDays(offset.toLong()), p))
        }
    }

    @Test
    fun `las semanas anteriores al ancla tambien se calculan bien`() {
        val p = pattern(Shift.MANANA, Shift.entries.toList())

        // La semana previa a una de mañana es la de noche (ciclo hacia atrás)
        assertEquals(Shift.NOCHE, repo.expectedShiftFor(monday.minusWeeks(1), p))
        assertEquals(Shift.TARDE, repo.expectedShiftFor(monday.minusWeeks(2), p))
    }
}
