package com.jairo.calendariotrabajo.ui.history

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
import java.time.YearMonth

class HistoryViewModel(
    private val workDayRepository: WorkDayRepository,
    private val salaryRatesRepository: SalaryRatesRepository,
    private val calculator: SalaryCalculator = SalaryCalculator()
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val currentYm: YearMonth = YearMonth.from(today)
    private val rangeStart: LocalDate = currentYm.minusMonths(5).atDay(1)
    private val rangeEnd: LocalDate = currentYm.atEndOfMonth()

    val uiState: StateFlow<HistoryUiState> = combine(
        workDayRepository.observeRange(rangeStart, rangeEnd),
        salaryRatesRepository.observe()
    ) { workDays, rates ->
        buildState(workDays, rates)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState.Empty
    )

    private fun buildState(
        workDays: List<WorkDayEntity>,
        rates: SalaryRatesEntity?
    ): HistoryUiState {
        val monthsAll = (0..5).map { offset ->
            val ym = currentYm.minusMonths(offset.toLong())
            val monthDays = workDays.filter {
                it.date.year == ym.year && it.date.monthValue == ym.monthValue
            }
            summaryFor(ym, monthDays, rates)
        }

        val current = monthsAll.first()
        val past = monthsAll.drop(1)

        val totals = TotalSummary(
            daysTotal = monthsAll.sumOf { it.daysWorked },
            hoursTotal = monthsAll.sumOf { it.hoursTotal },
            grossTotal = monthsAll.sumOf { it.grossSalary }
        )

        return HistoryUiState(
            currentMonth = current,
            pastMonths = past,
            totals = totals,
            loading = false
        )
    }

    private fun summaryFor(
        ym: YearMonth,
        monthDays: List<WorkDayEntity>,
        rates: SalaryRatesEntity?
    ): MonthSummaryData {
        val workedDays = monthDays.filter { it.didWork }
        val gross = rates?.let { calculator.calculate(monthDays, it).grossTotal } ?: 0.0
        return MonthSummaryData(
            year = ym.year,
            month = ym.monthValue,
            monthLabel = formatHistoryYearMonth(ym),
            daysWorked = workedDays.size,
            hoursTotal = workedDays.sumOf { it.hours },
            grossSalary = gross
        )
    }

    companion object {
        fun factory(
            workDayRepository: WorkDayRepository,
            salaryRatesRepository: SalaryRatesRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HistoryViewModel(workDayRepository, salaryRatesRepository)
            }
        }
    }
}
