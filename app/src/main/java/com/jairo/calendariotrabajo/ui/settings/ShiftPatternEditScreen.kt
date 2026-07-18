package com.jairo.calendariotrabajo.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.jairo.calendariotrabajo.ui.common.iconForShift
import com.jairo.calendariotrabajo.ui.common.labelForShift

@Composable
fun ShiftPatternEditScreen(
    viewModel: ShiftPatternEditViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedAndDone) {
        if (state.savedAndDone) onBack()
    }

    ShiftPatternEditContent(
        state = state,
        onToggleShift = viewModel::toggleShift,
        onSelectWeekShift = viewModel::selectCurrentWeekShift,
        onSave = viewModel::save,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun ShiftPatternEditContent(
    state: ShiftPatternEditUiState,
    onToggleShift: (Shift) -> Unit,
    onSelectWeekShift: (Shift) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Volver"
            )
        }

        Text(
            text = "Patrón de turnos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "Marca los turnos que haces. Si haces varios, la app los va rotando cada semana.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 22.dp)
        )

        SectionLabel("¿Qué turnos haces?")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Shift.entries.forEach { shift ->
                ShiftChip(
                    icon = iconForShift(shift),
                    label = labelForShift(shift),
                    selected = shift in state.activeShifts,
                    showCheck = true,
                    onClick = { onToggleShift(shift) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (state.needsWeekChoice) {
            SectionLabel("¿Cuál haces esta semana?", topPadding = 26.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Shift.entries.filter { it in state.activeShifts }.forEach { shift ->
                    ShiftChip(
                        icon = iconForShift(shift),
                        label = labelForShift(shift),
                        selected = state.selectedShift == shift,
                        showCheck = false,
                        onClick = { onSelectWeekShift(shift) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            Text(
                text = "Haces siempre el mismo turno, así que no hay rotación.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Button(
            onClick = onSave,
            enabled = state.canSave,
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Guardar patrón",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = topPadding, bottom = 10.dp)
    )
}

@Composable
private fun ShiftChip(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    showCheck: Boolean,
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
        Box(modifier = Modifier.fillMaxWidth()) {
            if (showCheck && selected) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(14.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
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
}
