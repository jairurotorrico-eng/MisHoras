package com.jairo.calendariotrabajo.ui.calendar

import com.jairo.calendariotrabajo.data.model.Shift
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CalendarUiState(
    val year: Int,
    val month: Int,
    val monthLabel: String,
    val today: LocalDate,
    val weeks: List<List<CalendarCellData>>,
    val summary: MonthSummary,
    val loading: Boolean = true
) {
    companion object {
        fun loading(ym: YearMonth, today: LocalDate) = CalendarUiState(
            year = ym.year,
            month = ym.monthValue,
            monthLabel = formatYearMonth(ym),
            today = today,
            weeks = emptyList(),
            summary = MonthSummary(0, 0.0, 0.0)
        )
    }
}

data class CalendarCellData(
    val date: LocalDate?,
    val shift: Shift? = null,
    val isHoliday: Boolean = false,
    val isFullExtraDay: Boolean = false,
    val isToday: Boolean = false,
    val didWork: Boolean = false
)

data class MonthSummary(
    val daysWorked: Int,
    val hoursTotal: Double,
    val extrasHoursApprox: Double
)

internal fun formatYearMonth(ym: YearMonth): String {
    val locale = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    return ym.format(formatter).replaceFirstChar { it.titlecase(locale) }
}
