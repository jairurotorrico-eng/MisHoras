package com.jairo.calendariotrabajo.ui.navigation

import java.time.LocalDate

//Nombre de las rutas
object Routes {
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val RATES = "settings/rates"

    const val DAY_DETAIL_PATTERN = "day_detail/{date}"
    const val DAY_DETAIL_ARG = "date"

    fun dayDetail(date: LocalDate): String = "day_detail/$date"
}
