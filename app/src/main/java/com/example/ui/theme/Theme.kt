package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = KeyTextAccent,
    onPrimary = Color(0xFF202124),
    primaryContainer = KeyDarkGray,
    onPrimaryContainer = KeyTextAccent,
    secondary = KeyTextSecondary,
    onSecondary = Color(0xFF202124),
    background = CalcBackground,
    onBackground = DisplayTextPrimary,
    surface = CalcSurface,
    onSurface = DisplayTextPrimary,
    surfaceVariant = CalcSurfaceVariant,
    onSurfaceVariant = DisplayTextSecondary,
    error = KeyTextClear,
    onError = Color(0xFF202124)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
