/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.composable

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.lib.Colors

private val DarkColorScheme = darkColorScheme(
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    primary = Colors.MAIN_PRIMARY_COLOR,
    primaryContainer = Colors.MAIN_CONTAINER_COLOR,
    secondary = Colors.MAIN_SECONDARY_COLOR,
    tertiary = Colors.MAIN_TERTIARY_COLOR,

    /* Other default colors to override */
    /*onTertiary = Color.Green,
    onSecondary = Color.Red,
    onTertiaryContainer = Color.Blue,
    onSecondaryContainer = Color.Blue,
    surfaceVariant = Color.Blue,
    surfaceContainerLowest = Color.Red,
    onBackground = Color.Red,
    onError = Color.Yellow,
    onErrorContainer = Color.Blue,
    surfaceContainerLow = Color.Green,
    secondaryContainer = Color.Blue,
    tertiaryContainer = Color.Red,
    surfaceContainerHigh = Color.Red,
    surfaceBright = Color.Cyan,
    surfaceTint = Color.Magenta,
    onSurface = Color.DarkGray,
    onSurfaceVariant = Color.Blue,
    scrim = Color.Green,
    surfaceDim = Color.Black,
    inverseOnSurface = Color.Green,
    inverseSurface = Color.Green,
    inversePrimary = Color.Green,
    error = Color.Red,
    errorContainer = Color.Red,
    outline = Color.Red,
    outlineVariant = Color.Red,
    background = Color.Yellow,
    surfaceContainer = Color.Blue,
    surfaceContainerHighest = Color.Yellow,
    surface = Color.Cyan,*/
)

private val LightColorScheme = lightColorScheme(
    background = Colors.MAIN_SCREEN_BACKGROUND_COLOR,
    primary = Colors.MAIN_PRIMARY_COLOR,
    primaryContainer = Colors.MAIN_CONTAINER_COLOR,
    secondary = Colors.MAIN_SECONDARY_COLOR,
    tertiary = Colors.MAIN_TERTIARY_COLOR,
    surface = Colors.MAIN_SURFACE_COLOR,
    surfaceContainer = Colors.MAIN_SURFACE_CONTAINER_COLOR,  // Bottom navigation's default color.
    surfaceContainerHighest = Colors.MAIN_SURFACE_CONTAINER_HIGHEST_COLOR,  // Card's default color.

    /* Other default colors to override */
    /*
    surfaceContainerHigh = Color.Red,
    surfaceContainerLow = Color.Green,
    surfaceContainerLowest = Color.Yellow,
    secondaryContainer = Color.Blue,
    tertiaryContainer = Color.Red,
    surfaceVariant = Color.Yellow,
    surfaceBright = Color.Cyan,
    surfaceTint = Color.Magenta,
    onSurface = Color.DarkGray,
    onSurfaceVariant = Color.Blue,
    scrim = Color.Green,
    surfaceDim = Color.Black,
    inverseOnSurface = Color.Green,
    inverseSurface = Color.Green,
    inversePrimary = Color.Green,
    onBackground = Color.Yellow,
    onError = Color.Yellow,
    onErrorContainer = Color.Yellow,
    onPrimary = Color.Yellow,
    onPrimaryContainer = Color.Yellow,
    onSecondary = Color.Yellow,
    onSecondaryContainer = Color.Yellow,
    onTertiary = Color.Yellow,
    onTertiaryContainer = Color.Yellow,
    error = Color.Red,
    errorContainer = Color.Red,
    outline = Color.Red,
    outlineVariant = Color.Red,
    */
)

// Set of Material typography styles to start with
private val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

/**
 * Dynamic color is available on Android 12+.
 * Must be set to "false" so that we can change the color manually.
 * SOURCE: https://stackoverflow.com/a/75952884
 */
@Composable
fun GKISalatigaAppTheme(
    // Disable dark mode because the coding for dark theme is complicated.
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    /*val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }*/

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}