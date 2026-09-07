package com.example.vitatrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Koyu tema: Figma tasarımına uygun lacivert + teal ---
private val DarkColorScheme = darkColorScheme(
    primary = Teal200,
    onPrimary = NavyDark,
    primaryContainer = Teal700,
    onPrimaryContainer = TextOnDark,
    secondary = Teal300,
    onSecondary = NavyDark,
    background = NavyDark,
    onBackground = TextOnDark,
    surface = SurfaceDark,
    onSurface = TextOnDark,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRed
)

// --- Açık tema: Beyaz kart + teal aksan ---
private val LightColorScheme = lightColorScheme(
    primary = Teal200,
    onPrimary = SurfaceLight,
    primaryContainer = Color(0xFFE0F7F1),
    onPrimaryContainer = Teal700,
    secondary = Teal300,
    onSecondary = SurfaceLight,
    background = Color(0xFFF5F7FA),
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    errorContainer = ErrorRedLight,
    onErrorContainer = ErrorRed
)

@Composable
fun VitaTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color kapalı: Figma tasarımının renk dilini koruyalım
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Status bar rengini tema ile senkronize et
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}