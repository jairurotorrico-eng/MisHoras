package com.jairo.calendariotrabajo.ui.settings

import com.jairo.calendariotrabajo.data.model.Shift

data class ShiftPatternEditUiState(
    // Turnos que la persona hace (puede ser uno solo)
    val activeShifts: Set<Shift> = emptySet(),
    // Cuál de ellos hace esta semana (ancla de la rotación)
    val selectedShift: Shift? = null,
    val loading: Boolean = true,
    val saving: Boolean = false,
    val savedAndDone: Boolean = false
) {
    // Solo tiene sentido preguntar "¿cuál esta semana?" si rota entre varios
    val needsWeekChoice: Boolean get() = activeShifts.size > 1

    val canSave: Boolean get() = activeShifts.isNotEmpty() && selectedShift != null && !saving
}
