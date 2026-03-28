package com.tuner.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BgBase = Color(0xFF121212)
val BgElevated = Color(0xFF18181B)
val BgSurface = Color(0xFF27272A)
val AccentPrimary = Color(0xFFEC4899)
val AccentSecondary = Color(0xFFF9A8D4)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA1A1AA)

private val DarkColors = darkColorScheme(
    primary = AccentPrimary,
    secondary = AccentSecondary,
    background = BgBase,
    surface = BgSurface,
    onPrimary = TextPrimary,
    onSecondary = BgBase,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun TunerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
