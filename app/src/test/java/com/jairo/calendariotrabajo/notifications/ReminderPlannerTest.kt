package com.jairo.calendariotrabajo.notifications

import com.jairo.calendariotrabajo.data.model.Shift
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ReminderPlannerTest {

    private val planner = ReminderPlanner()

    // 13 de julio de 2026 es lunes. Lo usamos como día de referencia.
    private val monday = LocalDate.of(2026, 7, 13)

    @Test
    fun `manana avisa a las 15 y a las 22 del mismo dia`() {
        val slots = planner.slotsForWorkDay(monday, Shift.MANANA)

        assertEquals(2, slots.size)
        assertEquals(monday.atTime(15, 0), slots[0].time)
        assertEquals(monday.atTime(22, 0), slots[1].time)
        // Los dos avisos comprueban el mismo día trabajado
        assertEquals(monday, slots[0].targetDay)
        assertEquals(monday, slots[1].targetDay)
    }

    @Test
    fun `tarde avisa a las 2230 y a las 9 del dia siguiente`() {
        val slots = planner.slotsForWorkDay(monday, Shift.TARDE)

        assertEquals(monday.atTime(22, 30), slots[0].time)
        assertEquals(monday.plusDays(1).atTime(9, 0), slots[1].time)
        // Aunque el segundo aviso caiga el martes, sigue comprobando el turno del lunes
        assertEquals(monday, slots[1].targetDay)
    }

    @Test
    fun `noche avisa a las 630 y a las 1930 del dia siguiente`() {
        val slots = planner.slotsForWorkDay(monday, Shift.NOCHE)

        assertEquals(monday.plusDays(1).atTime(6, 30), slots[0].time)
        assertEquals(monday.plusDays(1).atTime(19, 30), slots[1].time)
        assertEquals(monday, slots[0].targetDay)
    }

    @Test
    fun `nextSlotAfter devuelve el proximo aviso futuro`() {
        val shiftFor = { _: LocalDate -> Shift.MANANA }
        // Son las 10:00 del lunes → el próximo aviso es a las 15:00
        val now = monday.atTime(10, 0)

        val next = planner.nextSlotAfter(now, shiftFor)

        assertEquals(monday.atTime(15, 0), next?.time)
    }

    @Test
    fun `nextSlotAfter salta al segundo aviso cuando el primero ya paso`() {
        val shiftFor = { _: LocalDate -> Shift.MANANA }
        // Son las 16:00 → el de las 15:00 ya pasó, toca el de las 22:00
        val now = monday.atTime(16, 0)

        val next = planner.nextSlotAfter(now, shiftFor)

        assertEquals(monday.atTime(22, 0), next?.time)
    }

    @Test
    fun `dueSlot detecta un aviso recien vencido dentro de la tolerancia`() {
        val shiftFor = { _: LocalDate -> Shift.MANANA }
        // Son las 15:05, el aviso de las 15:00 venció hace 5 min
        val now = monday.atTime(15, 5)

        val due = planner.dueSlot(now, toleranceMinutes = 180L, shiftFor)

        assertEquals(monday.atTime(15, 0), due?.time)
        assertEquals(monday, due?.targetDay)
    }

    @Test
    fun `dueSlot ignora un aviso demasiado antiguo`() {
        val shiftFor = { _: LocalDate -> Shift.MANANA }
        // Son las 19:00: el aviso de las 15:00 fue hace 4h (fuera de la tolerancia de 3h)
        // y el de las 22:00 aún no ha llegado
        val now = monday.atTime(19, 0)

        val due = planner.dueSlot(now, toleranceMinutes = 180L, shiftFor)

        assertNull(due)
    }
}
