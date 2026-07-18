package com.jairo.calendariotrabajo.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jairo.calendariotrabajo.data.model.Shift
import com.jairo.calendariotrabajo.domain.calculator.SalaryBreakdown
import com.jairo.calendariotrabajo.ui.common.iconForShift
import com.jairo.calendariotrabajo.ui.common.labelForShift
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDayDetail: (LocalDate) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onApuntarHoy = { onNavigateToDayDetail(LocalDate.now()) },
        onVerCalendario = onNavigateToCalendar,
        onVerHistorial = onNavigateToHistory,
        onAjustes = onNavigateToSettings,
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onApuntarHoy: () -> Unit,
    onVerCalendario: () -> Unit,
    onVerHistorial: () -> Unit,
    onAjustes: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSalary by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HomeHeader(monthLabel = uiState.monthLabel, onSettingsClick = onAjustes)

        HoursCard(
            hours = uiState.hoursThisMonth,
            expected = uiState.expectedMonthlyHours,
            daysWorked = uiState.daysWorked,
            daysByShift = uiState.daysByShift
        )

        SalaryCard(
            breakdown = uiState.salaryBreakdown,
            extrasPay = uiState.extrasPay,
            visible = showSalary,
            onToggleVisible = { showSalary = !showSalary }
        )

        Button(
            onClick = onApuntarHoy,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Apuntar día de hoy",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onVerCalendario,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "  Calendario",
                    fontSize = 14.sp
                )
            }
            OutlinedButton(
                onClick = onVerHistorial,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "  Historial",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(monthLabel: String, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hola Wilma",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = monthLabel,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Ajustes"
            )
        }
    }
}

@Composable
private fun HoursCard(
    hours: Double,
    expected: Double,
    daysWorked: Int,
    daysByShift: Map<Shift, Int>
) {
    val progress = if (expected > 0.0) (hours / expected).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Horas este mes",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = hours.roundToInt().toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "/ ${expected.roundToInt()} h",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(8.dp)
            )
            Text(
                text = if (daysWorked == 1) "1 día trabajado" else "$daysWorked días trabajados",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            ShiftBreakdownRow(
                daysByShift = daysByShift,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun ShiftBreakdownRow(
    daysByShift: Map<Shift, Int>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Shift.entries.forEach { shift ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = iconForShift(shift),
                    contentDescription = labelForShift(shift),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = " ${daysByShift[shift] ?: 0}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SalaryCard(
    breakdown: SalaryBreakdown?,
    extrasPay: Double,
    visible: Boolean,
    onToggleVisible: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Estimación sueldo bruto",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = when {
                        breakdown == null -> "—"
                        !visible -> "•••••"
                        else -> formatCurrency(breakdown.grossTotal)
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (breakdown != null && visible) {
                    Text(
                        text = "+ ${formatCurrency(extrasPay)} en pluses este mes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "≈ ${formatCurrency(breakdown.netTotal)} líquido",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            IconButton(onClick = onToggleVisible) {
                Icon(
                    imageVector = if (visible) Icons.Outlined.RemoveRedEye else Icons.Outlined.VisibilityOff,
                    contentDescription = if (visible) "Ocultar sueldo" else "Mostrar sueldo"
                )
            }
        }
    }
}

private val currencyFormatter = NumberFormat.getInstance(Locale.forLanguageTag("es-ES")).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

private fun formatCurrency(value: Double): String = "${currencyFormatter.format(value)} €"
