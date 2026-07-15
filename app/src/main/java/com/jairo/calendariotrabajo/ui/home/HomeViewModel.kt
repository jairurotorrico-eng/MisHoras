package com.jairo.calendariotrabajo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import com.jairo.calendariotrabajo.data.repository.SalaryRatesRepository
import com.jairo.calendariotrabajo.data.repository.WorkDayRepository
import com.jairo.calendariotrabajo.domain.calculator.SalaryCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class HomeViewModel(
    private val workDayRepository: WorkDayRepository,
    private val salaryRatesRepository: SalaryRatesRepository,
    private val calculator: SalaryCalculator = SalaryCalculator()
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()

    val uiState: StateFlow<HomeUiState> = combine(
        workDayRepository.observeMonth(today.year, today.monthValue),
        salaryRatesRepository.observe()
    ) { workDays, rates ->
        buildUiState(workDays, rates)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.loading(today)
    )

    private fun buildUiState(
        workDays: List<WorkDayEntity>,
        rates: SalaryRatesEntity?
    ): HomeUiState {
        val workedList = workDays.filter { it.didWork }
        val hoursWorked = workedList.sumOf { it.hours }
        val daysWorked = workedList.size
        val breakdown = rates?.let { calculator.calculate(workDays, it) }
        val extrasPay = breakdown?.let {
            it.extraHoursPay + it.nightPlusPay + it.sundaysPay + it.formationPay
        } ?: 0.0
        val expected = rates?.let { (today.lengthOfMonth() / 7.0) * it.maxWeeklyHours } ?: 0.0

        return HomeUiState(
            year = today.year,
            month = today.monthValue,
            monthLabel = formatMonthLabel(today),
            hoursThisMonth = hoursWorked,
            expectedMonthlyHours = expected,
            daysWorked = daysWorked,
            extrasPay = extrasPay,
            salaryBreakdown = breakdown,
            loading = false
        )
    }

    companion object {
        fun factory(
            workDayRepository: WorkDayRepository,
            salaryRatesRepository: SalaryRatesRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(workDayRepository, salaryRatesRepository)
            }
        }
    }
}
