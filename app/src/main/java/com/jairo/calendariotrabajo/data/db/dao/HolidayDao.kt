package com.jairo.calendariotrabajo.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jairo.calendariotrabajo.data.db.entity.HolidayEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HolidayDao {

    @Query("SELECT * FROM holidays ORDER BY date ASC")
    fun observeAll(): Flow<List<HolidayEntity>>

    @Query("SELECT * FROM holidays WHERE date = :date")
    suspend fun getByDate(date: LocalDate): HolidayEntity?

    @Query("SELECT * FROM holidays WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    suspend fun getRange(start: LocalDate, end: LocalDate): List<HolidayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(holidays: List<HolidayEntity>)

    @Query("DELETE FROM holidays")
    suspend fun clearAll()
}
