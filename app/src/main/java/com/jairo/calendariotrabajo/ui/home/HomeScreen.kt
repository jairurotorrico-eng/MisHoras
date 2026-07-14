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
import com.jairo.calendariotrabajo.domain.calculator.SalaryBreakdown
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDayDetail: (LocalDate) -> Unit,
    onNavigateToCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onApuntarHoy = { onNavigateToDayDetail(LocalDate.now()) },
        onVerCalendario = onNavigateToCalendar,
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onApuntarHoy: () -> Unit,
    onVerCalendario: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSalary by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HomeHeader(monthLabel = uiState.monthLabel)

        HoursCard(
            hours = uiState.hoursThisMonth,
            expected = uiState.expectedMonthlyHours
        )

        SalaryCard(
            breakdown = uiState.salaryBreakdown,
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

        OutlinedButton(
            onClick = onVerCalendario,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "  Ver calendario del mes",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun HomeHeader(monthLabel: String) {
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
        IconButton(onClick = { /* pantalla Ajustes — próximo paso */ }) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Ajustes"
            )
        }
    }
}

@Composable
private fun HoursCard(hours: Double, expected: Double) {
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
        }
    }
}

@Composable
private fun SalaryCard(
    breakdown: SalaryBreakdown?,
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
            Column {
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
