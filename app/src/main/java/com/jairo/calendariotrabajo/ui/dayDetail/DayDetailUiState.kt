package com.jairo.calendariotrabajo.ui.dayDetail

import com.jairo.calendariotrabajo.data.model.Shift
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DayDetailUiState(
    val date: LocalDate,
    val weekdayLabel: String,
    val dateLabel: String,
    val didWork: Boolean = true,
    val shift: Shift = Shift.MANANA,
    val hours: Double = 8.0,
    val isHoliday: Boolean = false,
    val isFullExtraDay: Boolean = false,
    val autoHoliday: Boolean = false,
    val dayExtraPay: Double = 0.0,
    val existed: Boolean = false,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val savedAndDone: Boolean = false
) {
    companion object {
        fun initial(date: LocalDate) = DayDetailUiState(
            date = date,
            weekdayLabel = weekdayFor(date),
            dateLabel = dateLongFor(date)
        )
    }
}

internal fun weekdayFor(date: LocalDate): String {
    val locale = Locale.forLanguageTag("es-ES")
    return date.format(DateTimeFormatter.ofPattern("EEEE", locale))
        .replaceFirstChar { it.titlecase(locale) }
}

internal fun dateLongFor(date: LocalDate): String {
    val locale = Locale.forLanguageTag("es-ES")
    return date.format(DateTimeFormatter.ofPattern("d 'de' MMMM", locale))
}
