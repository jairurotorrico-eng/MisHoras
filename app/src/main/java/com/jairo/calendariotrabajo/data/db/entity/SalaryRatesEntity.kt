package com.jairo.calendariotrabajo.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "salary_rates")
data class SalaryRatesEntity(
    @PrimaryKey val id: Int = 1,
    val baseSalary: Double = 1233.15,
    val managementPlus: Double = 150.0,
    val monthlyComplement: Double = 102.76,
    val extraHourPrice: Double = 14.56,
    val nightPlusPerHour: Double = 1.57,
    val sundayPlus: Double = 30.0,
    val festivoPlusPerHour: Double = 14.56,
    val formationHourPrice: Double = 8.0,
    val irpfPercent: Double = 11.3,
    val socialSecurityPercent: Double = 7.1,
    val standardDayHours: Int = 8,
    val maxWeeklyHours: Int = 40,
    val nightStartHour: Int = 22,
    val nightEndHour: Int = 6
)
