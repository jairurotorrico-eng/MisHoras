package com.jairo.calendariotrabajo.ui.onboarding

import com.jairo.calendariotrabajo.data.model.Shift

data class OnboardingUiState(
    val selectedShift: Shift? = null,
    val saving: Boolean = false,
    val savedAndDone: Boolean = false
)
