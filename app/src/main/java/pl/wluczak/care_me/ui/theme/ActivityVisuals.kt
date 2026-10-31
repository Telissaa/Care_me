package pl.wluczak.care_me.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Face3
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector

val AVAILABLE_COLORS = listOf(
    FrostedBlueContainer,
    TeaGreenContainer,
    ThistleContainer,
    WarmPeachContainer,
    SoftPinkContainer,
    SoftLavenderContainer,
    LightMintContainer,
    SoftYellowContainer,
    SoftRedContainer
)

val AVAILABLE_ICONS = listOf(
    "favourite" to Icons.Default.Favorite,
    "star" to Icons.Default.Star,
    "fitness" to Icons.Default.FitnessCenter,
    "home" to Icons.Default.Home,
    "work" to Icons.Default.Work,
    "directions_car" to Icons.Default.DirectionsCar,
    "health_cross" to Icons.Default.LocalHospital,
    "pill" to Icons.Default.Medication,
    "potted_plant" to Icons.Default.LocalFlorist,
    "child_care" to Icons.Default.ChildCare,
    "pets" to Icons.Default.Pets,
    "description" to Icons.Default.Description,
    "payments" to Icons.Default.Payments,
    "person" to Icons.Default.Person,
    "mindfulness" to Icons.Default.SelfImprovement,
    "face" to Icons.Default.Face,
    "face_3" to Icons.Default.Face3,
    "cut" to Icons.Default.ContentCut,
    "kitchen" to Icons.Default.Kitchen,
    "local_cafe" to Icons.Default.LocalCafe,
    "travel" to Icons.Default.Flight,
    "calendar_month" to Icons.Default.CalendarMonth,
    "chat_bubble" to Icons.Default.ChatBubble
)

fun getIconByName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "favourite" -> Icons.Default.Favorite
        "star" -> Icons.Default.Star
        "fitness" -> Icons.Default.FitnessCenter
        "home" -> Icons.Default.Home
        "work" -> Icons.Default.Work
        "directions_car" -> Icons.Default.DirectionsCar
        "health_cross" -> Icons.Default.LocalHospital
        "pill" -> Icons.Default.Medication
        "potted_plant" -> Icons.Default.LocalFlorist
        "child_care" -> Icons.Default.ChildCare
        "pets" -> Icons.Default.Pets
        "description" -> Icons.Default.Description
        "payments" -> Icons.Default.Payments
        "person" -> Icons.Default.Person
        "mindfulness" -> Icons.Default.SelfImprovement
        "face" -> Icons.Default.Face
        "face_3" -> Icons.Default.Face3
        "cut" -> Icons.Default.ContentCut
        "kitchen" -> Icons.Default.Kitchen
        "local_cafe" -> Icons.Default.LocalCafe
        "travel" -> Icons.Default.Flight
        "calendar_month" -> Icons.Default.CalendarMonth
        "chat_bubble" -> Icons.Default.ChatBubble
        else -> Icons.Default.Favorite
    }
}

fun parseHexColor(colorHex: String): Color {
    return try {
        val cleaned = colorHex.removePrefix("#")
        val colorLong = cleaned.toLong(16)
        if (cleaned.length == 6) {
            Color(0xFF000000 or colorLong)
        } else {
            Color(colorLong)
        }
    } catch (_: Exception) {
        FrostedBlueContainer
    }
}

fun Color.toHex(): String {
    return String.format("#%06X", 0xFFFFFF and this.toArgb())
}
