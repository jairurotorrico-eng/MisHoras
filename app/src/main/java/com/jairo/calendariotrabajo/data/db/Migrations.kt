package com.jairo.calendariotrabajo.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Room necesita saber cómo pasar de una versión de la base de datos a la siguiente
// SIN borrar los datos que el usuario ya tiene guardados.
//
// v1 -> v2: la tabla shift_pattern gana la columna activeShifts, para poder
// elegir qué turnos hace la persona (solo mañanas, mañana+tarde, los tres...).
// A quien ya tuviera datos se le asigna la rotación completa, que es como
// funcionaba la app hasta ahora: así nada cambia para él.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE shift_pattern ADD COLUMN activeShifts TEXT NOT NULL DEFAULT 'MANANA,TARDE,NOCHE'"
        )
    }
}
