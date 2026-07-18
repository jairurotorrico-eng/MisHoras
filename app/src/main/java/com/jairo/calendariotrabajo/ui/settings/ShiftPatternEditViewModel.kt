package com.jairo.calendariotrabajo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jairo.calendariotrabajo.data.db.entity.ShiftPatternEntity
import com.jairo.calendariotrabajo.data.model.Shift
import com.jairo.calendariotrabajo.data.preferences.AppPreferences
import com.jairo.calendariotrabajo.data.repository.ShiftPatternRepository
import com.jairo.calendariotrabajo.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class ShiftPatternEditViewModel(
    private val shiftPatternRepository: ShiftPatternRepository,
    private val reminderScheduler: ReminderScheduler,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShiftPatternEditUiState())
    val uiState: StateFlow<ShiftPatternEditUiState> = _uiState.asStateFlow()

    init {
        loadCurrent()
    }

    // Carga la configuración guardada y preselecciona el turno de esta semana.
    private fun loadCurrent() = viewModelScope.launch {
        val pattern = shiftPatternRepository.get()
        val active = pattern?.activeShifts?.toSet()?.takeIf { it.isNotEmpty() }
            ?: Shift.entries.toSet()
        val current = pattern
            ?.let { shiftPatternRepository.expectedShiftFor(LocalDate.now(), it) }
            ?: Shift.MANANA

        _uiState.update {
            it.copy(
                activeShifts = active,
                selectedShift = if (current in active) current else firstOf(active),
                loading = false
            )
        }
    }

    // Activa o desactiva un turno. Nunca se permite quedarse sin ninguno.
    fun toggleShift(shift: Shift) {
        _uiState.update { state ->
            val newActive = if (shift in state.activeShifts) {
                state.activeShifts - shift
            } else {
                state.activeShifts + shift
            }
            if (newActive.isEmpty()) return@update state

            val newSelected = if (state.selectedShift in newActive) {
                state.selectedShift
            } else {
                firstOf(newActive)
            }
            state.copy(activeShifts = newActive, selectedShift = newSelected)
        }
    }

    fun selectCurrentWeekShift(shift: Shift) {
        _uiState.update { it.copy(selectedShift = shift) }
    }

    fun save() = viewModelScope.launch {
        val state = _uiState.value
        val shift = state.selectedShift ?: return@launch
        if (state.activeShifts.isEmpty()) return@launch

        _uiState.update { it.copy(saving = true) }

        val monday = LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        shiftPatternRepository.set(
            ShiftPatternEntity(
                anchorWeekMonday = monday,
                anchorShift = shift,
                // Guardamos siempre en orden canónico mañana -> tarde -> noche
                activeShifts = Shift.entries.filter { it in state.activeShifts }
            )
        )

        // Si las notificaciones están activas, recalcula el próximo aviso.
        if (appPreferences.notificationsEnabled.first()) {
            reminderScheduler.scheduleNext(shiftPatternRepository)
        }

        _uiState.update { it.copy(saving = false, savedAndDone = true) }
    }

    // El primero en orden canónico, para no depender del orden de un Set.
    private fun firstOf(shifts: Set<Shift>): Shift =
        Shift.entries.first { it in shifts }

    companion object {
        fun factory(
            shiftPatternRepository: ShiftPatternRepository,
            reminderScheduler: ReminderScheduler,
            appPreferences: AppPreferences
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ShiftPatternEditViewModel(shiftPatternRepository, reminderScheduler, appPreferences)
            }
        }
    }
}
