package com.jairo.calendariotrabajo.ui.home

import com.jairo.calendariotrabajo.domain.calculator.SalaryBreakdown
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomeUiState(
    val year: Int,
    val month: Int,
    val monthLabel: String,
    val hoursThisMonth: Double,
    val expectedMonthlyHours: Double,
    val salaryBreakdown: SalaryBreakdown?,
    val loading: Boolean = false
) {
    companion object {
        fun loading(date: LocalDate) = HomeUiState(
            year = date.year,
            month = date.monthValue,
            monthLabel = formatMonthLabel(date),
            hoursThisMonth = 0.0,
            expectedMonthlyHours = 0.0,
            salaryBreakdown = null,
            loading = true
        )
    }
}

internal fun formatMonthLabel(date: LocalDate): String {
    val locale = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    return date.format(formatter).replaceFirstChar { it.titlecase(locale) }
}
