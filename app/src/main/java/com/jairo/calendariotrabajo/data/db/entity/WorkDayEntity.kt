package com.jairo.calendariotrabajo.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jairo.calendariotrabajo.data.model.Shift
import java.time.LocalDate

@Entity(tableName = "work_days")
data class WorkDayEntity(
    @PrimaryKey val date: LocalDate,
    val didWork: Boolean,
    val shift: Shift?,
    val hours: Double,
    val isHoliday: Boolean = false,
    val isFullExtraDay: Boolean = false,
    val formationHours: Double = 0.0,
    val updatedAt: Long
)
