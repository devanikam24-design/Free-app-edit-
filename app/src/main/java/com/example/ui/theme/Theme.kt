package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EgoDarkColorScheme = darkColorScheme(
    primary = EgoGold,
    onPrimary = EgoBgPrimary,
    primaryContainer = EgoGoldDark,
    onPrimaryContainer = EgoGoldLight,
    inversePrimary = EgoGoldBright,

    secondary = EgoGoldBright,
    onSecondary = EgoBgPrimary,
    secondaryContainer = EgoBgElevated,
    onSecondaryContainer = EgoGoldLight,

    tertiary = EgoInfo,
    onTertiary = EgoBgPrimary,

    background = EgoBgPrimary,
    onBackground = EgoTextPrimary,

    surface = EgoBgPanel,
    onSurface = EgoTextPrimary,
    surfaceVariant = EgoBgElevated,
    onSurfaceVariant = EgoTextSecondary,

    outline = EgoBorder,
    outlineVariant = EgoBorderHighlight,

    error = EgoError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force consistent royal dark theme across devices
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EgoDarkColorScheme,
        typography = Typography,
        content = content
    )
}
