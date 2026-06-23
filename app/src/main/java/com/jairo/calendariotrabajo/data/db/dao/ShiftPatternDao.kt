package com.jairo.calendariotrabajo.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jairo.calendariotrabajo.data.db.entity.ShiftPatternEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftPatternDao {

    @Query("SELECT * FROM shift_pattern WHERE id = 1")
    fun observe(): Flow<ShiftPatternEntity?>

    @Query("SELECT * FROM shift_pattern WHERE id = 1")
    suspend fun get(): ShiftPatternEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pattern: ShiftPatternEntity)
}
