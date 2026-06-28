package com.claudeusage.tracker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Bg = Color(0xFF0E0E10)
val Panel = Color(0xFF161618)
val Panel2 = Color(0xFF1D1D20)
val Ink = Color(0xFFE7E6E3)
val Dim = Color(0xFF9B9A95)
val Faint = Color(0xFF6C6B66)
val Accent = Color(0xFFD97757)

private val scheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color(0xFF1C0F08),
    background = Bg,
    onBackground = Ink,
    surface = Panel,
    onSurface = Ink,
    surfaceVariant = Panel2,
    onSurfaceVariant = Dim,
)

/** Hex string ("#rrggbb") from the snapshot → Compose Color, with a safe fallback. */
fun hexColor(s: String?, fallback: Color = Dim): Color =
    runCatching { Color(android.graphics.Color.parseColor(s)) }.getOrDefault(fallback)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
