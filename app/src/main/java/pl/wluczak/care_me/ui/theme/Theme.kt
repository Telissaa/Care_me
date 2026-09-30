package pl.wluczak.care_me.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ThistleDarkTheme,
    onPrimary = ThistleDark,
    primaryContainer = ThistleDark,
    onPrimaryContainer = Thistle,

    secondary = FrostedBlueDarkTheme,
    onSecondary = FrostedBlueDark,
    secondaryContainer = FrostedBlueDark,
    onSecondaryContainer = FrostedBlue,

    tertiary = TeaGreenDarkTheme,
    onTertiary = TeaGreenDark,
    tertiaryContainer = TeaGreenDark,
    onTertiaryContainer = TeaGreen,

    background = DarkBackground,
    onBackground = BrightSnow,
    surface = DarkSurface,
    onSurface = BrightSnow,
    error = ErrorRedDark,
    onError = OnErrorDark
)

private val LightColorScheme = lightColorScheme(
    primary = Thistle,
    onPrimary = Black,
    primaryContainer = ThistleContainer,
    onPrimaryContainer = ThistleDark,

    secondary = FrostedBlue,
    onSecondary = Black,
    secondaryContainer = FrostedBlueContainer,
    onSecondaryContainer = FrostedBlueDark,

    tertiary = TeaGreen,
    onTertiary = Black,
    tertiaryContainer = TeaGreenContainer,
    onTertiaryContainer = TeaGreenDark,

    background = BrightSnow,
    onBackground = Black,
    surface = LightSurface,
    onSurface = Black,
    error = ErrorRed,
    onError = White
)

@Composable
fun Care_meTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
