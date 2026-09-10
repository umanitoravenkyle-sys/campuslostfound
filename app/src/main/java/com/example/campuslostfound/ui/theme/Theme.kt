package com.example.campuslostfound.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PLMUNGreen,
    secondary = Color(0xFF81C784),
    tertiary = Color(0xFF4DB6AC),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color(0xFF333333),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD)
)

private val LightColorScheme = lightColorScheme(
    primary = PLMUNGreen,
    secondary = AppTextSecondary,
    tertiary = Pink40,
    background = AppBackground,
    surface = AppBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = AppTextPrimary,
    onSurface = AppTextPrimary,
    outline = AppDividerColor,
    primaryContainer = PLMUNGreenLight,
    onPrimaryContainer = PLMUNGreen,
    surfaceVariant = AppSearchFieldBg,
    onSurfaceVariant = AppTextSecondary
)

@Composable
fun CampusLostFoundTheme(
    darkTheme: Boolean = false,
    // Dynamic color is disabled to maintain brand identity and fix visibility issues
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
        content = content
    )
}
