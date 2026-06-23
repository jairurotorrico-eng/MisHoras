package com.jairo.calendariotrabajo.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalaryRatesDao {

    @Query("SELECT * FROM salary_rates WHERE id = 1")
    fun observe(): Flow<SalaryRatesEntity?>

    @Query("SELECT * FROM salary_rates WHERE id = 1")
    suspend fun get(): SalaryRatesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rates: SalaryRatesEntity)
}
