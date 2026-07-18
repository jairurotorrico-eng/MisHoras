package com.jairo.calendariotrabajo.domain.rules

import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import com.jairo.calendariotrabajo.data.model.Shift
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ExtraDayRulesTest {

    // 13 jul 2026 es lunes
    private val monday = LocalDate.of(2026, 7, 13)

    private fun workDay(date: LocalDate, didWork: Boolean = true) = WorkDayEntity(
        date = date,
        didWork = didWork,
        shift = Shift.MANANA,
        hours = 8.0,
        updatedAt = 0L
    )

    @Test
    fun `el sexto dia de la semana es extra completo`() {
        // Lunes a viernes ya trabajados (5 días)
        val week = (0..4).map { workDay(monday.plusDays(it.toLong())) }
        val saturday = monday.plusDays(5)

        assertTrue(
            ExtraDayRules.shouldBeFullExtraDay(saturday, week, standardDaysPerWeek = 5)
        )
    }

    @Test
    fun `el quinto dia todavia no es extra`() {
        // Solo lunes a jueves trabajados (4 días)
        val week = (0..3).map { workDay(monday.plusDays(it.toLong())) }
        val friday = monday.plusDays(4)

        assertFalse(
            ExtraDayRules.shouldBeFullExtraDay(friday, week, standardDaysPerWeek = 5)
        )
    }

    @Test
    fun `los dias marcados como no trabajados no cuentan`() {
        // 5 registros, pero dos de ellos son "no trabajé" -> solo 3 reales
        val week = listOf(
            workDay(monday),
            workDay(monday.plusDays(1)),
            workDay(monday.plusDays(2), didWork = false),
            workDay(monday.plusDays(3), didWork = false),
            workDay(monday.plusDays(4))
        )
        val saturday = monday.plusDays(5)

        assertFalse(
            ExtraDayRules.shouldBeFullExtraDay(saturday, week, standardDaysPerWeek = 5)
        )
    }

    @Test
    fun `el propio dia no se cuenta a si mismo`() {
        // El sábado ya estaba guardado; al reabrirlo no debe contarse
        val week = (0..4).map { workDay(monday.plusDays(it.toLong())) }
        val saturday = monday.plusDays(5)
        val weekIncludingSaturday = week + workDay(saturday)

        // Sigue habiendo 5 días previos -> es extra, pero por los otros, no por sí mismo
        assertTrue(
            ExtraDayRules.shouldBeFullExtraDay(saturday, weekIncludingSaturday, 5)
        )
        // Y con solo 4 previos + él mismo, no debe activarse
        val fourPrevious = (0..3).map { workDay(monday.plusDays(it.toLong())) }
        assertFalse(
            ExtraDayRules.shouldBeFullExtraDay(saturday, fourPrevious + workDay(saturday), 5)
        )
    }

    @Test
    fun `el septimo dia tambien es extra`() {
        val week = (0..5).map { workDay(monday.plusDays(it.toLong())) }
        val sunday = monday.plusDays(6)

        assertTrue(
            ExtraDayRules.shouldBeFullExtraDay(sunday, week, standardDaysPerWeek = 5)
        )
    }

    @Test
    fun `los dias normales por semana salen de la jornada configurada`() {
        assertEquals(5, ExtraDayRules.standardDaysPerWeek(maxWeeklyHours = 40, standardDayHours = 8))
        assertEquals(6, ExtraDayRules.standardDaysPerWeek(maxWeeklyHours = 36, standardDayHours = 6))
        // Nunca puede dar 0 ni dividir por cero
        assertEquals(1, ExtraDayRules.standardDaysPerWeek(maxWeeklyHours = 4, standardDayHours = 8))
        assertEquals(5, ExtraDayRules.standardDaysPerWeek(maxWeeklyHours = 40, standardDayHours = 0))
    }
}
