package com.jairo.calendariotrabajo.data.db

import androidx.room.TypeConverter
import com.jairo.calendariotrabajo.data.model.HolidayScope
import com.jairo.calendariotrabajo.data.model.Shift
import java.time.LocalDate

//SQL Lite no sabe que son los enum de Kotlin, solo entiende tipos primitivos
//Room solo sabe guardar tipos simples (String, Int, Boolean), para que entienda que queremos guardar datos como un Enum, o un LocalDate, --> clase converters
// es una clase con métodos anotados @TypeConverter que le dicen a room como convertir de ida y vuelta.
class Converters {

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    // Una lista de turnos se guarda como texto: "MANANA,TARDE,NOCHE"
    @TypeConverter
    fun shiftListToString(value: List<Shift>?): String? = value?.joinToString(",") { it.name }

    @TypeConverter
    fun stringToShiftList(value: String?): List<Shift>? =
        value?.split(",")?.filter { it.isNotBlank() }?.map { Shift.valueOf(it) }

    @TypeConverter
    fun shiftToString(value: Shift?): String? = value?.name

    @TypeConverter
    fun stringToShift(value: String?): Shift? = value?.let(Shift::valueOf)

    @TypeConverter
    fun scopeToString(value: HolidayScope?): String? = value?.name

    @TypeConverter
    fun stringToScope(value: String?): HolidayScope? = value?.let(HolidayScope::valueOf)
}
