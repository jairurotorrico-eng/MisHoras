package com.jairo.calendariotrabajo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jairo.calendariotrabajo.data.db.dao.HolidayDao
import com.jairo.calendariotrabajo.data.db.dao.SalaryRatesDao
import com.jairo.calendariotrabajo.data.db.dao.ShiftPatternDao
import com.jairo.calendariotrabajo.data.db.dao.WorkDayDao
import com.jairo.calendariotrabajo.data.db.entity.HolidayEntity
import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import com.jairo.calendariotrabajo.data.db.entity.ShiftPatternEntity
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity

//Esta clase es el contenedor, se instacia como singleton en MisHorasApplication

@Database( //Declaramos que entidades forman parte de la base de datos y versión tiene
    entities = [
        WorkDayEntity::class,
        SalaryRatesEntity::class,
        ShiftPatternEntity::class,
        HolidayEntity::class
    ],
    version = 2,
    exportSchema = false
)

//Exponemos las DAOS como funciones abstractas
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workDayDao(): WorkDayDao
    abstract fun salaryRatesDao(): SalaryRatesDao
    abstract fun shiftPatternDao(): ShiftPatternDao
    abstract fun holidayDao(): HolidayDao

    //IMPORTANTE!
    //Abrir una base de datos es pesado, por eso debemos de asegurarnos de abrir una instancia nueva cada vez que lo necesitamos. Una sola instancia compartida para toda la app
    companion object { //patrón companion object con singleton
        @Volatile //asegura que todos los hilos vean siempre el valor mas actualizado de INSTANCE
        private var INSTANCE: AppDatabase? = null  //(evita problemas de concurrencia al leer)

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { //asegura de que si dos hilos piden la base de datos solo se la crea una y la otra esta en espera y reutiliza la misma
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mishoras.db"
                ).addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it } //also es una funcion scope (ámbito) de kotlin, coge el objeto que tiene delante, el cual ajecuta dentro de {} como it y luego devuelve el mismo objeto sin modificarlo
            }                                    //Aquí se usa como un atajo para "de paso que construyo la base de datos, aprovecho para guardarla en INSTANCE, y
                                            // luego sigo devolviendo la base de datos como resultado de la función"
        }
    }
}
