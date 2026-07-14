package com.jairo.calendariotrabajo.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jairo.calendariotrabajo.data.db.entity.ShiftPatternEntity
import com.jairo.calendariotrabajo.data.model.Shift
import com.jairo.calendariotrabajo.data.repository.ShiftPatternRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class OnboardingViewModel(
    private val shiftPatternRepository: ShiftPatternRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectShift(shift: Shift) {
        _uiState.update { it.copy(selectedShift = shift) }
    }

    fun save() = viewModelScope.launch {
        val shift = _uiState.value.selectedShift ?: return@launch
        _uiState.update { it.copy(saving = true) }
        val monday = LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        shiftPatternRepository.set(
            ShiftPatternEntity(
                anchorWeekMonday = monday,
                anchorShift = shift
            )
        )
        _uiState.update { it.copy(saving = false, savedAndDone = true) }
    }

    companion object {
        fun factory(shiftPatternRepository: ShiftPatternRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    OnboardingViewModel(shiftPatternRepository)
                }
            }
    }
}
