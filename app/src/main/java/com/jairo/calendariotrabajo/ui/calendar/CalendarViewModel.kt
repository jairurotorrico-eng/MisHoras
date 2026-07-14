package com.jairo.calendariotrabajo.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jairo.calendariotrabajo.data.db.entity.HolidayEntity
import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import com.jairo.calendariotrabajo.data.repository.HolidayRepository
import com.jairo.calendariotrabajo.data.repository.SalaryRatesRepository
import com.jairo.calendariotrabajo.data.repository.WorkDayRepository
import com.jairo.calendariotrabajo.domain.calculator.SalaryCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val workDayRepository: WorkDayRepository,
    private val holidayRepository: HolidayRepository,
    private val salaryRatesRepository: SalaryRatesRepository,
    private val calculator: SalaryCalculator = SalaryCalculator()
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val _currentMonth = MutableStateFlow(YearMonth.from(today))

    val uiState: StateFlow<CalendarUiState> = _currentMonth
        .flatMapLatest { ym ->
            combine(
                workDayRepository.observeMonth(ym.year, ym.monthValue),
                salaryRatesRepository.observe(),
                holidayRepository.observeAll()
            ) { workDays, rates, allHolidays ->
                buildState(ym, workDays, rates, allHolidays)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CalendarUiState.loading(YearMonth.from(today), today)
        )

    fun goToPreviousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }

    fun goToNextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }

    private fun buildState(
        ym: YearMonth,
        workDays: List<WorkDayEntity>,
        rates: SalaryRatesEntity?,
        allHolidays: List<HolidayEntity>
    ): CalendarUiState {
        val holidayDates = allHolidays
            .asSequence()
            .filter { it.date.year == ym.year && it.date.monthValue == ym.monthValue }
            .map { it.date }
            .toSet()

        val workDayMap = workDays.associateBy { it.date }
        val cells = generateCells(ym, workDayMap, holidayDates)
        val weeks = cells.chunked(7)

        val hoursWorked = workDays.filter { it.didWork }.sumOf { it.hours }
        val daysWorked = workDays.count { it.didWork }
        val extrasHours = rates?.let { calculator.calculate(workDays, it).extraHoursTotal } ?: 0.0

        return CalendarUiState(
            year = ym.year,
            month = ym.monthValue,
            monthLabel = formatYearMonth(ym),
            today = today,
            weeks = weeks,
            summary = MonthSummary(
                daysWorked = daysWorked,
                hoursTotal = hoursWorked,
                extrasHoursApprox = extrasHours
            ),
            loading = false
        )
    }

    private fun generateCells(
        ym: YearMonth,
        workDayMap: Map<LocalDate, WorkDayEntity>,
        holidayDates: Set<LocalDate>
    ): List<CalendarCellData> {
        val firstDay = ym.atDay(1)
        val paddingBefore = firstDay.dayOfWeek.value - 1
        val daysInMonth = ym.lengthOfMonth()
        val cells = mutableListOf<CalendarCellData>()

        repeat(paddingBefore) { cells.add(CalendarCellData(date = null)) }

        for (i in 1..daysInMonth) {
            val date = ym.atDay(i)
            val workDay = workDayMap[date]
            cells.add(
                CalendarCellData(
                    date = date,
                    shift = if (workDay?.didWork == true) workDay.shift else null,
                    isHoliday = date in holidayDates,
                    isFullExtraDay = workDay?.isFullExtraDay ?: false,
                    isToday = date == today,
                    didWork = workDay?.didWork ?: false
                )
            )
        }

        val remaining = (7 - cells.size % 7) % 7
        repeat(remaining) { cells.add(CalendarCellData(date = null)) }

        return cells
    }

    companion object {
        fun factory(
            workDayRepository: WorkDayRepository,
            holidayRepository: HolidayRepository,
            salaryRatesRepository: SalaryRatesRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CalendarViewModel(workDayRepository, holidayRepository, salaryRatesRepository)
            }
        }
    }
}
