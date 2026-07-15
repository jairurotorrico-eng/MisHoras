package com.jairo.calendariotrabajo.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.ui.graphics.vector.ImageVector
import com.jairo.calendariotrabajo.data.model.Shift

fun iconForShift(shift: Shift): ImageVector = when (shift) {
    Shift.MANANA -> Icons.Outlined.WbTwilight
    Shift.TARDE -> Icons.Outlined.WbSunny
    Shift.NOCHE -> Icons.Outlined.Bedtime
}

fun labelForShift(shift: Shift): String = when (shift) {
    Shift.MANANA -> "Mañana"
    Shift.TARDE -> "Tarde"
    Shift.NOCHE -> "Noche"
}
