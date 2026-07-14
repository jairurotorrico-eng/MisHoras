package com.jairo.calendariotrabajo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jairo.calendariotrabajo.data.repository.SalaryRatesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RatesEditViewModel(
    private val salaryRatesRepository: SalaryRatesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RatesEditUiState())
    val uiState: StateFlow<RatesEditUiState> = _uiState.asStateFlow()

    init {
        loadInitial()
    }

    private fun loadInitial() = viewModelScope.launch {
        val rates = salaryRatesRepository.getOrCreateDefault()
        _uiState.update {
            it.copy(
                baseSalary = rates.baseSalary.toString(),
                managementPlus = rates.managementPlus.toString(),
                monthlyComplement = rates.monthlyComplement.toString(),
                extraHourPrice = rates.extraHourPrice.toString(),
                nightPlusPerHour = rates.nightPlusPerHour.toString(),
                sundayPlus = rates.sundayPlus.toString(),
                formationHourPrice = rates.formationHourPrice.toString(),
                irpfPercent = rates.irpfPercent.toString(),
                socialSecurityPercent = rates.socialSecurityPercent.toString(),
                standardDayHours = rates.standardDayHours.toString(),
                maxWeeklyHours = rates.maxWeeklyHours.toString(),
                loading = false
            )
        }
    }

    fun setField(field: RateField, value: String) {
        _uiState.update { current ->
            when (field) {
                RateField.BASE_SALARY -> current.copy(baseSalary = value)
                RateField.MANAGEMENT_PLUS -> current.copy(managementPlus = value)
                RateField.MONTHLY_COMPLEMENT -> current.copy(monthlyComplement = value)
                RateField.EXTRA_HOUR_PRICE -> current.copy(extraHourPrice = value)
                RateField.NIGHT_PLUS -> current.copy(nightPlusPerHour = value)
                RateField.SUNDAY_PLUS -> current.copy(sundayPlus = value)
                RateField.FORMATION_PRICE -> current.copy(formationHourPrice = value)
                RateField.IRPF -> current.copy(irpfPercent = value)
                RateField.SOCIAL_SECURITY -> current.copy(socialSecurityPercent = value)
                RateField.STANDARD_DAY_HOURS -> current.copy(standardDayHours = value)
                RateField.MAX_WEEKLY_HOURS -> current.copy(maxWeeklyHours = value)
            }
        }
    }

    fun save() = viewModelScope.launch {
        val s = _uiState.value
        _uiState.update { it.copy(saving = true) }
        val current = salaryRatesRepository.getOrCreateDefault()
        val updated = current.copy(
            baseSalary = s.baseSalary.parseDoubleOr(current.baseSalary),
            managementPlus = s.managementPlus.parseDoubleOr(current.managementPlus),
            monthlyComplement = s.monthlyComplement.parseDoubleOr(current.monthlyComplement),
            extraHourPrice = s.extraHourPrice.parseDoubleOr(current.extraHourPrice),
            nightPlusPerHour = s.nightPlusPerHour.parseDoubleOr(current.nightPlusPerHour),
            sundayPlus = s.sundayPlus.parseDoubleOr(current.sundayPlus),
            formationHourPrice = s.formationHourPrice.parseDoubleOr(current.formationHourPrice),
            irpfPercent = s.irpfPercent.parseDoubleOr(current.irpfPercent),
            socialSecurityPercent = s.socialSecurityPercent.parseDoubleOr(current.socialSecurityPercent),
            standardDayHours = s.standardDayHours.parseIntOr(current.standardDayHours),
            maxWeeklyHours = s.maxWeeklyHours.parseIntOr(current.maxWeeklyHours)
        )
        salaryRatesRepository.update(updated)
        _uiState.update { it.copy(saving = false, savedAndDone = true) }
    }

    private fun String.parseDoubleOr(fallback: Double): Double =
        replace(',', '.').toDoubleOrNull() ?: fallback

    private fun String.parseIntOr(fallback: Int): Int =
        toIntOrNull() ?: fallback

    companion object {
        fun factory(salaryRatesRepository: SalaryRatesRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    RatesEditViewModel(salaryRatesRepository)
                }
            }
    }
}
