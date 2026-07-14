package com.jairo.calendariotrabajo.ui.dayDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jairo.calendariotrabajo.data.db.entity.SalaryRatesEntity
import com.jairo.calendariotrabajo.data.db.entity.WorkDayEntity
import com.jairo.calendariotrabajo.data.model.Shift
import com.jairo.calendariotrabajo.data.repository.HolidayRepository
import com.jairo.calendariotrabajo.data.repository.SalaryRatesRepository
import com.jairo.calendariotrabajo.data.repository.ShiftPatternRepository
import com.jairo.calendariotrabajo.data.repository.WorkDayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class DayDetailViewModel(
    private val date: LocalDate,
    private val workDayRepository: WorkDayRepository,
    private val salaryRatesRepository: SalaryRatesRepository,
    private val holidayRepository: HolidayRepository,
    private val shiftPatternRepository: ShiftPatternRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DayDetailUiState.initial(date))
    val uiState: StateFlow<DayDetailUiState> = _uiState.asStateFlow()

    private var currentRates: SalaryRatesEntity? = null

    init {
        loadInitial()
    }

    private fun loadInitial() = viewModelScope.launch {
        val existing = workDayRepository.getByDate(date)
        val rates = salaryRatesRepository.getOrCreateDefault()
        val isHolidayAuto = holidayRepository.isHoliday(date)
        val pattern = shiftPatternRepository.get()
        val defaultShift = pattern?.let {
            shiftPatternRepository.expectedShiftFor(date, it)
        } ?: Shift.MANANA
        currentRates = rates

        _uiState.update { current ->
            current.copy(
                didWork = existing?.didWork ?: true,
                shift = existing?.shift ?: defaultShift,
                hours = existing?.hours ?: rates.standardDayHours.toDouble(),
                isHoliday = existing?.isHoliday ?: isHolidayAuto,
                isFullExtraDay = existing?.isFullExtraDay ?: false,
                autoHoliday = isHolidayAuto,
                existed = existing != null,
                loading = false
            )
        }
        recomputeExtra()
    }

    private fun recomputeExtra() {
        val rates = currentRates ?: return
        val state = _uiState.value
        val extra = computeDayExtra(state, rates)
        _uiState.update { it.copy(dayExtraPay = extra) }
    }

    private fun computeDayExtra(state: DayDetailUiState, rates: SalaryRatesEntity): Double {
        if (!state.didWork) return 0.0
        var pay = 0.0
        if (state.isHoliday || state.isFullExtraDay) {
            pay += state.hours * rates.extraHourPrice
        }
        if (state.shift == Shift.NOCHE) {
            pay += state.hours * rates.nightPlusPerHour
        }
        if (state.date.dayOfWeek == DayOfWeek.SUNDAY) {
            pay += rates.sundayPlus
        }
        return pay
    }

    fun setShift(shift: Shift) {
        _uiState.update { it.copy(shift = shift) }
        recomputeExtra()
    }

    fun changeHours(delta: Double) {
        _uiState.update { it.copy(hours = (it.hours + delta).coerceIn(0.0, 24.0)) }
        recomputeExtra()
    }

    fun setHoliday(value: Boolean) {
        _uiState.update { it.copy(isHoliday = value) }
        recomputeExtra()
    }

    fun setFullExtraDay(value: Boolean) {
        _uiState.update { it.copy(isFullExtraDay = value) }
        recomputeExtra()
    }

    fun save() = viewModelScope.launch {
        val state = _uiState.value
        _uiState.update { it.copy(saving = true) }
        val entity = WorkDayEntity(
            date = date,
            didWork = true,
            shift = state.shift,
            hours = state.hours,
            isHoliday = state.isHoliday,
            isFullExtraDay = state.isFullExtraDay,
            formationHours = 0.0,
            updatedAt = System.currentTimeMillis()
        )
        workDayRepository.save(entity)
        _uiState.update { it.copy(saving = false, savedAndDone = true) }
    }

    fun markNotWorked() = viewModelScope.launch {
        _uiState.update { it.copy(saving = true) }
        val entity = WorkDayEntity(
            date = date,
            didWork = false,
            shift = null,
            hours = 0.0,
            isHoliday = false,
            isFullExtraDay = false,
            formationHours = 0.0,
            updatedAt = System.currentTimeMillis()
        )
        workDayRepository.save(entity)
        _uiState.update { it.copy(saving = false, savedAndDone = true) }
    }

    companion object {
        fun factory(
            date: LocalDate,
            workDayRepository: WorkDayRepository,
            salaryRatesRepository: SalaryRatesRepository,
            holidayRepository: HolidayRepository,
            shiftPatternRepository: ShiftPatternRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DayDetailViewModel(
                    date,
                    workDayRepository,
                    salaryRatesRepository,
                    holidayRepository,
                    shiftPatternRepository
                )
            }
        }
    }
}
