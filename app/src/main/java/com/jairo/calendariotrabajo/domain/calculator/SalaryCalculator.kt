package com.jairo.calendariotrabajo.domain.calculator

import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import com.jairo.calendariotrabajo.data.model.Shift
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class SalaryBreakdown(
    val baseSalary: Double,
    val managementPlus: Double,
    val monthlyComplement: Double,
    val extraHoursTotal: Double,
    val extraHoursPay: Double,
    val nightHoursTotal: Double,
    val nightPlusPay: Double,
    val sundaysWorked: Int,
    val sundaysPay: Double,
    val formationHoursTotal: Double,
    val formationPay: Double,
    val grossTotal: Double,
    val irpfDeduction: Double,
    val socialSecurityDeduction: Double,
    val netTotal: Double,
    val daysWorked: Int
)

class SalaryCalculator {

    fun calculate(
        workDays: List<WorkDayEntity>,
        rates: SalaryRatesEntity
    ): SalaryBreakdown {
        val workedDays = workDays.filter { it.didWork }

        var fullDayExtraHours = 0.0
        val regularHoursByWeek = mutableMapOf<LocalDate, Double>()
        var nightHoursTotal = 0.0
        var sundaysWorked = 0
        var formationHoursTotal = 0.0

        for (day in workedDays) {
            val isExtraDay = day.isHoliday || day.isFullExtraDay

            if (isExtraDay) {
                fullDayExtraHours += day.hours
            } else {
                val weekKey = mondayOf(day.date)
                regularHoursByWeek[weekKey] = (regularHoursByWeek[weekKey] ?: 0.0) + day.hours
            }

            if (day.shift == Shift.NOCHE) {
                nightHoursTotal += day.hours
            }

            if (day.date.dayOfWeek == DayOfWeek.SUNDAY) {
                sundaysWorked++
            }

            formationHoursTotal += day.formationHours
        }

        val weeklyOverflow = regularHoursByWeek.values.sumOf { hoursInWeek ->
            (hoursInWeek - rates.maxWeeklyHours).coerceAtLeast(0.0)
        }

        val extraHoursTotal = fullDayExtraHours + weeklyOverflow
        val extraHoursPay = extraHoursTotal * rates.extraHourPrice
        val nightPlusPay = nightHoursTotal * rates.nightPlusPerHour
        val sundaysPay = sundaysWorked * rates.sundayPlus
        val formationPay = formationHoursTotal * rates.formationHourPrice

        val grossTotal = rates.baseSalary +
                rates.managementPlus +
                rates.monthlyComplement +
                extraHoursPay +
                nightPlusPay +
                sundaysPay +
                formationPay

        val irpfDeduction = grossTotal * (rates.irpfPercent / 100.0)
        val socialSecurityDeduction = grossTotal * (rates.socialSecurityPercent / 100.0)
        val netTotal = grossTotal - irpfDeduction - socialSecurityDeduction

        return SalaryBreakdown(
            baseSalary = rates.baseSalary,
            managementPlus = rates.managementPlus,
            monthlyComplement = rates.monthlyComplement,
            extraHoursTotal = extraHoursTotal,
            extraHoursPay = extraHoursPay,
            nightHoursTotal = nightHoursTotal,
            nightPlusPay = nightPlusPay,
            sundaysWorked = sundaysWorked,
            sundaysPay = sundaysPay,
            formationHoursTotal = formationHoursTotal,
            formationPay = formationPay,
            grossTotal = grossTotal,
            irpfDeduction = irpfDeduction,
            socialSecurityDeduction = socialSecurityDeduction,
            netTotal = netTotal,
            daysWorked = workedDays.size
        )
    }

    private fun mondayOf(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}
