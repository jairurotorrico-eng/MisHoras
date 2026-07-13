package com.jairo.calendariotrabajo.data.holidays

import com.jairo.calendariotrabajo.data.db.entity.HolidayEntity
import com.jairo.calendariotrabajo.data.model.HolidayScope
import java.time.LocalDate

object SpanishHolidays { //Declaramos sin Singleton--> En java sería una clase estática. Una sola instancia no necesitamos más.
                            // val festivos = SpanishHolidays.forYears(listOf(2026, 2027))--> Lo llamaremos así

    fun forYears(years: List<Int>): List<HolidayEntity> = //Esta función le pasas una lista de años y te devuelve los días festivos de esos años
        years.flatMap { holidaysFor(it) } //faltMap--> aplica HolidaysFor(year)) a cada año y concatena los resultados en una sola lista

    private fun holidaysFor(year: Int): List<HolidayEntity> { //private, no necesita ser público, solo se usa dentro de este archivo
        val easterMonday = computeEasterMonday(year)

        return listOf(
            HolidayEntity(LocalDate.of(year, 1, 1), "Año Nuevo", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 1, 6), "Reyes", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 5, 1), "Día del Trabajador", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 8, 15), "Asunción", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 10, 12), "Fiesta Nacional", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 11, 1), "Todos los Santos", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 12, 6), "Día de la Constitución", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 12, 8), "Inmaculada Concepción", HolidayScope.NATIONAL),
            HolidayEntity(LocalDate.of(year, 12, 25), "Navidad", HolidayScope.NATIONAL),

            HolidayEntity(easterMonday.minusDays(4), "Jueves Santo", HolidayScope.NATIONAL),
            HolidayEntity(easterMonday.minusDays(3), "Viernes Santo", HolidayScope.NATIONAL),

            HolidayEntity(easterMonday, "Lunes de Pascua", HolidayScope.REGIONAL),
            HolidayEntity(LocalDate.of(year, 12, 3), "San Francisco Javier", HolidayScope.REGIONAL)
        )
    }

    // Algoritmo de Butcher para calcular el domingo de Pascua de cualquier año.
    private fun computeEasterMonday(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1
        return LocalDate.of(year, month, day).plusDays(1)
    }
}
