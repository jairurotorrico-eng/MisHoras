package com.jairo.calendariotrabajo.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RatesEditScreen(
    viewModel: RatesEditViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedAndDone) {
        if (state.savedAndDone) onBack()
    }

    RatesEditContent(
        state = state,
        onBack = onBack,
        onFieldChange = viewModel::setField,
        onSave = viewModel::save,
        modifier = modifier
    )
}

@Composable
private fun RatesEditContent(
    state: RatesEditUiState,
    onBack: () -> Unit,
    onFieldChange: (RateField, String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Header(onBack = onBack)

        SectionTitle("Fijo mensual")
        RateField(
            label = "Salario base",
            value = state.baseSalary,
            suffix = "€",
            onValueChange = { onFieldChange(RateField.BASE_SALARY, it) }
        )
        RateField(
            label = "Plus Gestión",
            value = state.managementPlus,
            suffix = "€",
            onValueChange = { onFieldChange(RateField.MANAGEMENT_PLUS, it) }
        )
        RateField(
            label = "Complemento mensual",
            value = state.monthlyComplement,
            suffix = "€",
            onValueChange = { onFieldChange(RateField.MONTHLY_COMPLEMENT, it) }
        )

        SectionTitle("Horas extra y suplementos")
        RateField(
            label = "Precio hora extra (incluye festivos)",
            value = state.extraHourPrice,
            suffix = "€/h",
            onValueChange = { onFieldChange(RateField.EXTRA_HOUR_PRICE, it) }
        )
        RateField(
            label = "Plus nocturno (por hora)",
            value = state.nightPlusPerHour,
            suffix = "€/h",
            onValueChange = { onFieldChange(RateField.NIGHT_PLUS, it) }
        )
        RateField(
            label = "Plus domingo (por domingo trabajado)",
            value = state.sundayPlus,
            suffix = "€",
            onValueChange = { onFieldChange(RateField.SUNDAY_PLUS, it) }
        )
        RateField(
            label = "Precio hora formación",
            value = state.formationHourPrice,
            suffix = "€/h",
            onValueChange = { onFieldChange(RateField.FORMATION_PRICE, it) }
        )

        SectionTitle("Deducciones")
        RateField(
            label = "IRPF",
            value = state.irpfPercent,
            suffix = "%",
            onValueChange = { onFieldChange(RateField.IRPF, it) }
        )
        RateField(
            label = "Seguridad Social del trabajador",
            value = state.socialSecurityPercent,
            suffix = "%",
            onValueChange = { onFieldChange(RateField.SOCIAL_SECURITY, it) }
        )

        SectionTitle("Jornada")
        RateField(
            label = "Horas normales por día",
            value = state.standardDayHours,
            suffix = "h",
            keyboardType = KeyboardType.Number,
            onValueChange = { onFieldChange(RateField.STANDARD_DAY_HOURS, it) }
        )
        RateField(
            label = "Tope semanal (a partir de aquí, extras)",
            value = state.maxWeeklyHours,
            suffix = "h",
            keyboardType = KeyboardType.Number,
            onValueChange = { onFieldChange(RateField.MAX_WEEKLY_HOURS, it) }
        )

        Button(
            onClick = onSave,
            enabled = !state.saving,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Guardar cambios",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Column {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Volver"
            )
        }
        Text(
            text = "Tarifas y sueldo",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Toca cualquier valor para editarlo. Se aplican en el momento sobre los cálculos del mes.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 10.dp, bottom = 2.dp)
    )
}

@Composable
private fun RateField(
    label: String,
    value: String,
    suffix: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = { Text(suffix) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
