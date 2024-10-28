package org.gkisalatiga.plus.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Brown2,
    secondary = Brown3,
    tertiary = Brown4
)

private val LightColorScheme = lightColorScheme(
    primary = Brown4,
    secondary = Brown3,
    tertiary = Brown2,
    primaryContainer = Brown1,

    /* Overriding default values. */
    /*surface = Brown1,
    onPrimary = Brown1,
    onSecondary = Brown1,
    onTertiary = Brown1,
    onBackground = Brown1,
    onSurface = Brown1,*/
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun GKISalatigaPlusTheme(
    // Disable dark mode because the coding for dark theme is complicated.
    darkTheme: Boolean = false,

    // Dynamic color is available on Android 12+.
    // Must be set to "false" so that we can change the color manually.
    // SOURCE: https://stackoverflow.com/a/75952884
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