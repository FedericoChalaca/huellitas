package com.huellitas.mascotas.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.huellitas.mascotas.data.ThemeMode

// Verde bosque y naranja cálido sobre crema: se ve como una app de mascotas, no como la plantilla por defecto.
private val LightColors = lightColorScheme(
    primary = Color(0xFF2F6F5E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEBDF),
    onPrimaryContainer = Color(0xFF0A3A2D),
    secondary = Color(0xFFA85A12),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDBA),
    onSecondaryContainer = Color(0xFF3A1E00),
    background = Color(0xFFFFF8F0),
    onBackground = Color(0xFF2A2420),
    surface = Color(0xFFFFF8F0),
    onSurface = Color(0xFF2A2420),
    surfaceVariant = Color(0xFFF1E4D6),
    onSurfaceVariant = Color(0xFF52463C),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFBF1E6),
    surfaceContainer = Color(0xFFF7ECDF),
    surfaceContainerHigh = Color(0xFFF1E4D6),
    surfaceContainerHighest = Color(0xFFEBDDCE),
    errorContainer = Color(0xFFFFDAD4),
    onErrorContainer = Color(0xFF410002),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8CD3BC),
    onPrimary = Color(0xFF00382C),
    primaryContainer = Color(0xFF14503F),
    onPrimaryContainer = Color(0xFFCDEBDF),
    secondary = Color(0xFFFFB86B),
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF6B3B00),
    onSecondaryContainer = Color(0xFFFFDDBA),
    background = Color(0xFF14110E),
    onBackground = Color(0xFFEDE0D4),
    surface = Color(0xFF14110E),
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = Color(0xFF2B2520),
    onSurfaceVariant = Color(0xFFD5C4B5),
    surfaceContainerLowest = Color(0xFF0F0D0B),
    surfaceContainerLow = Color(0xFF1B1713),
    surfaceContainer = Color(0xFF211C17),
    surfaceContainerHigh = Color(0xFF2B2520),
    surfaceContainerHighest = Color(0xFF352F29),
)

@Composable
fun HuellitasTheme(mode: ThemeMode, content: @Composable () -> Unit) {
    val dark = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, content = content)
}
