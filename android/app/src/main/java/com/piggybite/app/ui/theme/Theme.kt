package com.piggybite.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkMode = compositionLocalOf { false }

private val ModernLightColorScheme = lightColorScheme(
    primary = NepGreenPrimary,
    onPrimary = PureWhite,
    primaryContainer = NepGreenSoft,
    onPrimaryContainer = NepGreenPrimary,
    background = ModernBackground,
    onBackground = TextDeepNavy,
    surface = CardBackground,
    onSurface = TextDeepNavy,
    surfaceVariant = CardBorderLight,
    outline = CardBorder
)

// Dark Theme: Nền đen tuyền, surface & card màu xanh đen, chữ xám sáng
private val ModernDarkColorScheme = darkColorScheme(
    primary = NepGreenAccent,
    onPrimary = Color.White,
    primaryContainer = DarkCardBackground,
    onPrimaryContainer = DarkTextMain,
    background = DarkAppBackground, // Nền hoàn toàn màu đen
    onBackground = DarkTextMain,    // Chữ xám sáng
    surface = DarkCardBackground,   // Thẻ & card màu xanh đen
    onSurface = DarkTextMain,
    surfaceVariant = DarkButtonBg,  // Nút & ô nhập màu xanh đen
    outline = DarkCardBorder        // Viền xanh đen
)

@Composable
fun PiggyBiteTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ModernDarkColorScheme else ModernLightColorScheme

    CompositionLocalProvider(LocalIsDarkMode provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
