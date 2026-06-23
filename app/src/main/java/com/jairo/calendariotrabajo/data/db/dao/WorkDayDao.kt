package com.jairo.calendariotrabajo.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WorkDayDao {

    @Query("SELECT * FROM work_days WHERE date = :date")
    suspend fun getByDate(date: LocalDate): WorkDayEntity?

    @Query("SELECT * FROM work_days WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    fun observeRange(start: LocalDate, end: LocalDate): Flow<List<WorkDayEntity>>

    @Query("SELECT * FROM work_days WHERE date BETWEEN :start AND :end ORDER BY date ASC")
    suspend fun getRange(start: LocalDate, end: LocalDate): List<WorkDayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(day: WorkDayEntity)

    @Delete
    suspend fun delete(day: WorkDayEntity)

    @Query("DELETE FROM work_days WHERE date < :before")
    suspend fun deleteBefore(before: LocalDate)
}
