package pl.wluczak.care_me.presentation.dashboard.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import pl.wluczak.care_me.ui.theme.FrostedBlueContainer
import pl.wluczak.care_me.ui.theme.LightMintContainer
import pl.wluczak.care_me.ui.theme.SoftLavenderContainer
import pl.wluczak.care_me.ui.theme.SoftPinkContainer
import pl.wluczak.care_me.ui.theme.SoftRedContainer
import pl.wluczak.care_me.ui.theme.SoftYellowContainer
import pl.wluczak.care_me.ui.theme.TeaGreenContainer
import pl.wluczak.care_me.ui.theme.ThistleContainer
import pl.wluczak.care_me.ui.theme.WarmPeachContainer

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
    "face" to Icons.Default.Face,
    "cut" to Icons.Default.ContentCut,
    "spa" to Icons.Default.Spa,
    "favorite" to Icons.Default.Favorite,
    "water" to Icons.Default.WaterDrop,
    "sunny" to Icons.Default.WbSunny,
    "star" to Icons.Default.Star,
    "fitness" to Icons.Default.FitnessCenter
)

fun getIconByName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "face" -> Icons.Default.Face
        "cut" -> Icons.Default.ContentCut
        "spa" -> Icons.Default.Spa
        "favorite" -> Icons.Default.Favorite
        "water" -> Icons.Default.WaterDrop
        "sunny" -> Icons.Default.WbSunny
        "star" -> Icons.Default.Star
        "fitness" -> Icons.Default.FitnessCenter
        else -> Icons.Default.Face
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
