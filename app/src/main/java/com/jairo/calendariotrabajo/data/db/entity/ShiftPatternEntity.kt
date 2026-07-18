package com.jairo.calendariotrabajo.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jairo.calendariotrabajo.data.model.Shift
import java.time.LocalDate

@Entity(tableName = "shift_pattern")
data class ShiftPatternEntity(
    @PrimaryKey val id: Int = 1,
    val anchorWeekMonday: LocalDate,
    val anchorShift: Shift,
    // Turnos que la persona hace. La rotación gira solo entre estos.
    // Si solo hay uno, no rota: siempre ese turno.
    val activeShifts: List<Shift> = listOf(Shift.MANANA, Shift.TARDE, Shift.NOCHE)
)
