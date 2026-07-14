package com.jairo.calendariotrabajo.ui.history

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HistoryUiState(
    val currentMonth: MonthSummaryData?,
    val pastMonths: List<MonthSummaryData>,
    val totals: TotalSummary,
    val loading: Boolean = true
) {
    companion object {
        val Empty = HistoryUiState(
            currentMonth = null,
            pastMonths = emptyList(),
            totals = TotalSummary(0, 0.0, 0.0)
        )
    }
}

data class MonthSummaryData(
    val year: Int,
    val month: Int,
    val monthLabel: String,
    val daysWorked: Int,
    val hoursTotal: Double,
    val grossSalary: Double
)

data class TotalSummary(
    val daysTotal: Int,
    val hoursTotal: Double,
    val grossTotal: Double
)

internal fun formatHistoryYearMonth(ym: YearMonth): String {
    val locale = Locale.forLanguageTag("es-ES")
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
    return ym.format(formatter).replaceFirstChar { it.titlecase(locale) }
}
