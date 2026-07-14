package com.jairo.calendariotrabajo.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToRates: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Header(onBack = onBack)

        SectionTitle("Mi trabajo")
        SettingsCard {
            SettingsRow(
                icon = Icons.Outlined.Repeat,
                title = "Patrón de turnos",
                subtitle = "Próximamente",
                enabled = false
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(
                icon = Icons.Outlined.Payments,
                title = "Tarifas y sueldo",
                subtitle = "Salario base, pluses y horas extra",
                onClick = onNavigateToRates
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(
                icon = Icons.Outlined.CalendarMonth,
                title = "Festivos",
                subtitle = "Nacionales + Navarra (ya cargados)",
                enabled = false
            )
        }

        SectionTitle("Recordatorios")
        SettingsCard {
            SettingsRow(
                icon = Icons.Outlined.Notifications,
                title = "Notificaciones",
                subtitle = "Próximamente",
                enabled = false
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(
                icon = Icons.Outlined.Schedule,
                title = "Horas de aviso",
                subtitle = "Próximamente",
                enabled = false
            )
        }

        SectionTitle("Datos")
        SettingsCard {
            SettingsRow(
                icon = Icons.Outlined.CloudUpload,
                title = "Copia de seguridad",
                subtitle = "Próximamente",
                enabled = false
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(
                icon = Icons.Outlined.FileDownload,
                title = "Exportar a PDF",
                subtitle = "Próximamente",
                enabled = false
            )
        }

        SectionTitle("Información")
        SettingsCard {
            SettingsRow(
                icon = Icons.Outlined.Info,
                title = "Acerca de",
                subtitle = "Versión 1.0",
                enabled = false
            )
        }

        Spacer(Modifier.size(24.dp))
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
            text = "Ajustes",
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
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
        modifier = Modifier.padding(start = 6.dp, top = 14.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp), content = content)
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val baseModifier = Modifier
        .fillMaxWidth()
        .let { if (enabled) it.clickable(onClick = onClick) else it }
        .padding(vertical = 14.dp, horizontal = 4.dp)

    Row(
        modifier = baseModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        if (enabled) {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

