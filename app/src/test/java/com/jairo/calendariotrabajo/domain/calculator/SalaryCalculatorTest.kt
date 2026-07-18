package com.jairo.calendariotrabajo.domain.calculator

import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import com.jairo.calendariotrabajo.data.model.Shift
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class SalaryCalculatorTest {

    private val calculator = SalaryCalculator()

    // Tarifas por defecto = las que sacamos de las nóminas reales de Wilma.
    private val rates = SalaryRatesEntity()

    // Fijo mensual = base + gestión + complemento (se cobra trabaje lo que trabaje).
    private val fixedMonthly = 1233.15 + 150.0 + 102.76 // 1485.91

    // Semana de referencia: 13 jul 2026 es lunes, 19 jul es domingo.
    private val monday = LocalDate.of(2026, 7, 13)

    private fun workDay(
        date: LocalDate,
        shift: Shift = Shift.MANANA,
        hours: Double = 8.0,
        isHoliday: Boolean = false,
        isFullExtraDay: Boolean = false,
        formationHours: Double = 0.0,
        didWork: Boolean = true
    ) = WorkDayEntity(
        date = date,
        didWork = didWork,
        shift = shift,
        hours = hours,
        isHoliday = isHoliday,
        isFullExtraDay = isFullExtraDay,
        formationHours = formationHours,
        updatedAt = 0L
    )

    @Test
    fun `mes sin trabajar solo cobra el fijo mensual`() {
        val result = calculator.calculate(emptyList(), rates)

        assertEquals(fixedMonthly, result.grossTotal, 0.001)
        assertEquals(0, result.daysWorked)
        assertEquals(0.0, result.extraHoursTotal, 0.001)
    }

    @Test
    fun `un dia normal de manana no anade nada al fijo`() {
        val days = listOf(workDay(monday, Shift.MANANA, 8.0))

        val result = calculator.calculate(days, rates)

        assertEquals(fixedMonthly, result.grossTotal, 0.001)
        assertEquals(0.0, result.extraHoursTotal, 0.001)
        assertEquals(1, result.daysWorked)
    }

    @Test
    fun `los dias marcados como no trabajados se ignoran`() {
        val days = listOf(
            workDay(monday, didWork = false),
            workDay(monday.plusDays(1), didWork = false)
        )

        val result = calculator.calculate(days, rates)

        assertEquals(0, result.daysWorked)
        assertEquals(fixedMonthly, result.grossTotal, 0.001)
    }

    @Test
    fun `turno de noche suma el plus nocturno por hora`() {
        val days = listOf(workDay(monday, Shift.NOCHE, 8.0))

        val result = calculator.calculate(days, rates)

        assertEquals(8.0, result.nightHoursTotal, 0.001)
        assertEquals(8 * 1.57, result.nightPlusPay, 0.001) // 12.56
        assertEquals(fixedMonthly + 8 * 1.57, result.grossTotal, 0.001)
    }

    @Test
    fun `trabajar un domingo suma el plus de domingo`() {
        val sunday = monday.plusDays(6) // 19 jul 2026
        val days = listOf(workDay(sunday, Shift.MANANA, 8.0))

        val result = calculator.calculate(days, rates)

        assertEquals(1, result.sundaysWorked)
        assertEquals(30.0, result.sundaysPay, 0.001)
        assertEquals(fixedMonthly + 30.0, result.grossTotal, 0.001)
    }

    @Test
    fun `dia completo extra paga todas sus horas como extra`() {
        val days = listOf(workDay(monday, Shift.MANANA, 8.0, isFullExtraDay = true))

        val result = calculator.calculate(days, rates)

        assertEquals(8.0, result.extraHoursTotal, 0.001)
        assertEquals(8 * 14.56, result.extraHoursPay, 0.001) // 116.48
    }

    @Test
    fun `dia festivo trabajado se paga como hora extra`() {
        val days = listOf(workDay(monday, Shift.MANANA, 8.0, isHoliday = true))

        val result = calculator.calculate(days, rates)

        assertEquals(8.0, result.extraHoursTotal, 0.001)
        assertEquals(8 * 14.56, result.extraHoursPay, 0.001)
    }

    @Test
    fun `pasar de 40 horas en una semana genera horas extra`() {
        // Lunes a sábado, 6 días × 8h = 48h regulares → 8h de exceso son extra.
        val days = (0..5).map { workDay(monday.plusDays(it.toLong()), Shift.MANANA, 8.0) }

        val result = calculator.calculate(days, rates)

        assertEquals(8.0, result.extraHoursTotal, 0.001) // 48 - 40
        assertEquals(8 * 14.56, result.extraHoursPay, 0.001)
        assertEquals(6, result.daysWorked)
    }

    @Test
    fun `las deducciones aplican IRPF y Seguridad Social sobre el bruto`() {
        val result = calculator.calculate(emptyList(), rates)

        assertEquals(fixedMonthly * 0.113, result.irpfDeduction, 0.001)
        assertEquals(fixedMonthly * 0.071, result.socialSecurityDeduction, 0.001)
        assertEquals(fixedMonthly * (1 - 0.113 - 0.071), result.netTotal, 0.001)
    }

    @Test
    fun `escenario combinado noche mas exceso semanal se acumulan`() {
        // Semana lun-sáb, 6 noches de 8h = 48h.
        // - Plus nocturno: 48h × 1,57 (todas las horas nocturnas)
        // - Horas extra: 8h de exceso (48 - 40) × 14,56
        val days = (0..5).map { workDay(monday.plusDays(it.toLong()), Shift.NOCHE, 8.0) }

        val result = calculator.calculate(days, rates)

        assertEquals(48.0, result.nightHoursTotal, 0.001)
        assertEquals(48 * 1.57, result.nightPlusPay, 0.001)   // 75.36
        assertEquals(8.0, result.extraHoursTotal, 0.001)
        assertEquals(8 * 14.56, result.extraHoursPay, 0.001)  // 116.48
        assertEquals(fixedMonthly + 48 * 1.57 + 8 * 14.56, result.grossTotal, 0.001)
    }
}
