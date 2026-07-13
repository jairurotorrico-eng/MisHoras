package com.jairo.calendariotrabajo.data.repository

import com.jairo.calendariotrabajo.data.db.dao.SalaryRatesDao
import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import kotlinx.coroutines.flow.Flow

//para leer/guardar las tarifas (con "crear por defecto si no existe")
//patrón "get or create." Salary Rates es un sigleton en la base de datos siempre tendrá el id = 1. Cunado arranquemos por primera vez la app no hay ninguna fila
//Este método maneja eso
class SalaryRatesRepository(private val salaryRatesDao: SalaryRatesDao) {

    fun observe(): Flow<SalaryRatesEntity?> = salaryRatesDao.observe()

    suspend fun getOrCreateDefault(): SalaryRatesEntity =
        salaryRatesDao.get() ?: SalaryRatesEntity().also { salaryRatesDao.upsert(it) }

    suspend fun update(rates: SalaryRatesEntity) = salaryRatesDao.upsert(rates)
}
