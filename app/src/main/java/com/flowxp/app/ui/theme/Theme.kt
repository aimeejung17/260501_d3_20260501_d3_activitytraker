package com.flowxp.app.ui.theme

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

private val FlowPurple = Color(0xFF6750A4)
private val FlowPurpleLight = Color(0xFFE8DEF8)

private val LightColors = lightColorScheme(
    primary = FlowPurple,
    onPrimary = Color.White,
    primaryContainer = FlowPurpleLight,
    onPrimaryContainer = Color(0xFF21005E),
    secondary = Color(0xFF625B71),
    surface = Color(0xFFFFFBFE),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = FlowPurpleLight,
    secondary = Color(0xFFCCC2DC),
    surface = Color(0xFF1C1B1F),
)

@Composable
fun FlowXpTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val context = LocalContext.current
    val colors = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        dark -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
