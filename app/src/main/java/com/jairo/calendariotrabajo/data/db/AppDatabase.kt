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

@Database(
    entities = [
        WorkDayEntity::class,
        SalaryRatesEntity::class,
        ShiftPatternEntity::class,
        HolidayEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workDayDao(): WorkDayDao
    abstract fun salaryRatesDao(): SalaryRatesDao
    abstract fun shiftPatternDao(): ShiftPatternDao
    abstract fun holidayDao(): HolidayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mishoras.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
