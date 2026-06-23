package com.jairo.calendariotrabajo.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jairo.calendariotrabajo.data.model.HolidayScope
import java.time.LocalDate

@Entity(tableName = "holidays")
data class HolidayEntity(
    @PrimaryKey val date: LocalDate,
    val name: String,
    val scope: HolidayScope
)
