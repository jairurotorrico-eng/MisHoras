package com.jairo.calendariotrabajo.data.db

import androidx.room.TypeConverter
import com.jairo.calendariotrabajo.data.model.HolidayScope
import com.jairo.calendariotrabajo.data.model.Shift
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun shiftToString(value: Shift?): String? = value?.name

    @TypeConverter
    fun stringToShift(value: String?): Shift? = value?.let(Shift::valueOf)

    @TypeConverter
    fun scopeToString(value: HolidayScope?): String? = value?.name

    @TypeConverter
    fun stringToScope(value: String?): HolidayScope? = value?.let(HolidayScope::valueOf)
}
