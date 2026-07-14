package com.jairo.calendariotrabajo.ui.settings

data class RatesEditUiState(
    val baseSalary: String = "",
    val managementPlus: String = "",
    val monthlyComplement: String = "",
    val extraHourPrice: String = "",
    val nightPlusPerHour: String = "",
    val sundayPlus: String = "",
    val formationHourPrice: String = "",
    val irpfPercent: String = "",
    val socialSecurityPercent: String = "",
    val standardDayHours: String = "",
    val maxWeeklyHours: String = "",
    val loading: Boolean = true,
    val saving: Boolean = false,
    val savedAndDone: Boolean = false
)

enum class RateField {
    BASE_SALARY,
    MANAGEMENT_PLUS,
    MONTHLY_COMPLEMENT,
    EXTRA_HOUR_PRICE,
    NIGHT_PLUS,
    SUNDAY_PLUS,
    FORMATION_PRICE,
    IRPF,
    SOCIAL_SECURITY,
    STANDARD_DAY_HOURS,
    MAX_WEEKLY_HOURS
}
