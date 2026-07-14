package com.jairo.calendariotrabajo.ui.dayDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jairo.calendariotrabajo.data.model.Shift
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DayDetailScreen(
    viewModel: DayDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.savedAndDone) {
        if (uiState.savedAndDone) onBack()
    }

    DayDetailContent(
        state = uiState,
        onBack = onBack,
        onSetShift = viewModel::setShift,
        onChangeHours = viewModel::changeHours,
        onSetHoliday = viewModel::setHoliday,
        onSetFullExtraDay = viewModel::setFullExtraDay,
        onSave = viewModel::save,
        onMarkNotWorked = viewModel::markNotWorked,
        modifier = modifier
    )
}

@Composable
private fun DayDetailContent(
    state: DayDetailUiState,
    onBack: () -> Unit,
    onSetShift: (Shift) -> Unit,
    onChangeHours: (Double) -> Unit,
    onSetHoliday: (Boolean) -> Unit,
    onSetFullExtraDay: (Boolean) -> Unit,
    onSave: () -> Unit,
    onMarkNotWorked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DayHeader(
            weekday = state.weekdayLabel,
            date = state.dateLabel,
            existed = state.existed,
            onBack = onBack
        )

        SectionTitle("Turno realizado")
        ShiftSelector(shift = state.shift, onSetShift = onSetShift)

        SectionTitle("Horas trabajadas")
        HoursStepper(hours = state.hours, onChange = onChangeHours)

        SectionTitle("Marcar este día como")
        SpecialFlagsCard(
            isHoliday = state.isHoliday,
            isFullExtraDay = state.isFullExtraDay,
            autoHoliday = state.autoHoliday,
            onSetHoliday = onSetHoliday,
            onSetFullExtraDay = onSetFullExtraDay
        )

        EstimationCard(dayExtraPay = state.dayExtraPay)

        Button(
            onClick = onSave,
            enabled = !state.saving,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(
                text = if (state.existed) "Actualizar día" else "Guardar día",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        TextButton(
            onClick = onMarkNotWorked,
            enabled = !state.saving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("No trabajé hoy", fontSize = 14.sp)
        }
    }
}

@Composable
private fun DayHeader(
    weekday: String,
    date: String,
    existed: Boolean,
    onBack: () -> Unit
) {
    Column {
        IconButton(onClick = onBack, modifier = Modifier.padding(bottom = 4.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Volver"
            )
        }
        Text(
            text = weekday,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = date,
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
        )
        StatusChip(existed = existed)
    }
}

@Composable
private fun StatusChip(existed: Boolean) {
    val bg: androidx.compose.ui.graphics.Color
    val fg: androidx.compose.ui.graphics.Color
    val label: String
    if (existed) {
        bg = MaterialTheme.colorScheme.tertiaryContainer
        fg = MaterialTheme.colorScheme.onTertiaryContainer
        label = "Apuntado"
    } else {
        bg = MaterialTheme.colorScheme.secondaryContainer
        fg = MaterialTheme.colorScheme.onSecondaryContainer
        label = "Sin apuntar"
    }
    Surface(color = bg, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = label,
            color = fg,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ShiftSelector(shift: Shift, onSetShift: (Shift) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShiftChip(
            icon = Icons.Outlined.WbSunny,
            label = "Mañana",
            selected = shift == Shift.MANANA,
            onClick = { onSetShift(Shift.MANANA) },
            modifier = Modifier.weight(1f)
        )
        ShiftChip(
            icon = Icons.Outlined.LightMode,
            label = "Tarde",
            selected = shift == Shift.TARDE,
            onClick = { onSetShift(Shift.TARDE) },
            modifier = Modifier.weight(1f)
        )
        ShiftChip(
            icon = Icons.Outlined.DarkMode,
            label = "Noche",
            selected = shift == Shift.NOCHE,
            onClick = { onSetShift(Shift.NOCHE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ShiftChip(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor)
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

@Composable
private fun HoursStepper(hours: Double, onChange: (Double) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilledIconButton(
                onClick = { onChange(-1.0) },
                enabled = hours > 0.0
            ) {
                Text("−", fontSize = 22.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${hours.roundToInt()} h",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Por defecto: 8 h",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilledIconButton(
                onClick = { onChange(1.0) },
                enabled = hours < 24.0
            ) {
                Text("+", fontSize = 22.sp)
            }
        }
    }
}

@Composable
private fun SpecialFlagsCard(
    isHoliday: Boolean,
    isFullExtraDay: Boolean,
    autoHoliday: Boolean,
    onSetHoliday: (Boolean) -> Unit,
    onSetFullExtraDay: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SwitchRow(
                icon = Icons.Outlined.CalendarMonth,
                title = "Día festivo",
                subtitle = if (autoHoliday) "Detectado automáticamente" else "Suma el plus de festivo",
                checked = isHoliday,
                onCheckedChange = onSetHoliday
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SwitchRow(
                icon = Icons.Outlined.Star,
                title = "Día completo extra",
                subtitle = "Todas las horas como extra",
                checked = isFullExtraDay,
                onCheckedChange = onSetFullExtraDay
            )
        }
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun EstimationCard(dayExtraPay: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Este día suma extra",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCurrency(dayExtraPay),
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Se añade a la estimación mensual del Home",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

private val currencyFormatter = NumberFormat.getInstance(Locale.forLanguageTag("es-ES")).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

private fun formatCurrency(value: Double): String = "${currencyFormatter.format(value)} €"
