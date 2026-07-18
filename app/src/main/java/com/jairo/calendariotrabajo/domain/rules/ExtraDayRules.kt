package com.jairo.calendariotrabajo.domain.rules

import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import java.time.LocalDate

// Reglas de negocio sobre cuándo un día cuenta entero como extra.
object ExtraDayRules {

    // Si en esa semana ya se han trabajado los días de jornada normal
    // (por defecto 5, es decir 40h / 8h), el siguiente día trabajado
    // supera el tope semanal y se paga entero como hora extra.
    fun shouldBeFullExtraDay(
        date: LocalDate,
        weekWorkDays: List<WorkDayEntity>,
        standardDaysPerWeek: Int
    ): Boolean {
        val alreadyWorked = weekWorkDays.count { it.didWork && it.date != date }
        return alreadyWorked >= standardDaysPerWeek
    }

    // Cuántos días "normales" entran en una semana según la jornada configurada.
    fun standardDaysPerWeek(maxWeeklyHours: Int, standardDayHours: Int): Int =
        if (standardDayHours <= 0) 5
        else (maxWeeklyHours / standardDayHours).coerceAtLeast(1)
}
