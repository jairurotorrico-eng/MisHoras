package com.jairo.calendariotrabajo.notifications

import com.jairo.calendariotrabajo.data.model.Shift
import java.time.LocalDate
import java.time.LocalDateTime

// Un momento concreto en que hay que recordar apuntar, y qué día laboral comprueba.
data class ReminderSlot(
    val time: LocalDateTime,
    val targetDay: LocalDate,
    val shift: Shift
)

// Lógica PURA (sin Android): dado un turno y una fecha, calcula cuándo avisar.
// Al no depender de nada del sistema, es fácil de razonar y de probar.
class ReminderPlanner {

    // Los 2 momentos de aviso para un turno trabajado el día [day].
    // Ambos avisos comprueban el mismo día [day] (el día trabajado).
    fun slotsForWorkDay(day: LocalDate, shift: Shift): List<ReminderSlot> = when (shift) {
        Shift.MANANA -> listOf(
            ReminderSlot(day.atTime(15, 0), day, shift),
            ReminderSlot(day.atTime(22, 0), day, shift)
        )
        Shift.TARDE -> listOf(
            ReminderSlot(day.atTime(22, 30), day, shift),
            ReminderSlot(day.plusDays(1).atTime(9, 0), day, shift)
        )
        Shift.NOCHE -> listOf(
            ReminderSlot(day.plusDays(1).atTime(6, 30), day, shift),
            ReminderSlot(day.plusDays(1).atTime(19, 30), day, shift)
        )
    }

    // El próximo aviso futuro a partir de [now].
    fun nextSlotAfter(now: LocalDateTime, shiftFor: (LocalDate) -> Shift): ReminderSlot? =
        windowDays(now.toLocalDate())
            .flatMap { slotsForWorkDay(it, shiftFor(it)) }
            .filter { it.time.isAfter(now) }
            .minByOrNull { it.time }

    // El aviso que "acaba de vencer": el más reciente en el pasado, dentro de la tolerancia.
    // Sirve para que el worker, al despertarse, sepa qué día tenía que comprobar.
    fun dueSlot(
        now: LocalDateTime,
        toleranceMinutes: Long,
        shiftFor: (LocalDate) -> Shift
    ): ReminderSlot? =
        windowDays(now.toLocalDate())
            .flatMap { slotsForWorkDay(it, shiftFor(it)) }
            .filter { !it.time.isAfter(now) && it.time.isAfter(now.minusMinutes(toleranceMinutes)) }
            .maxByOrNull { it.time }

    // Miramos de 2 días atrás a 1 adelante: cubre los avisos que un turno genera en D+1.
    private fun windowDays(around: LocalDate): List<LocalDate> =
        (-2L..1L).map { around.plusDays(it) }
}
