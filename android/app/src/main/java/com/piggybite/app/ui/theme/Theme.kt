package com.piggybite.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkMode = compositionLocalOf { false }

// Chế độ sáng: Nền trắng, chữ đen, các ô chữ trắng, ô nổi bật màu xanh dương nhạt sáng
private val ModernLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = PureWhite,
    primaryContainer = HighlightBlueLightBg,
    onPrimaryContainer = HighlightBlueLightText,
    background = ModernBackground,
    onBackground = TextMain,
    surface = CardBackground,
    onSurface = TextMain,
    surfaceVariant = CardBorderLight,
    outline = CardBorder
)

// Chế độ tối: Nền đen, chữ xám, các ô chữ xám, ô nổi bật màu xanh dương đen
private val ModernDarkColorScheme = darkColorScheme(
    primary = DarkTitleBlue,
    onPrimary = Color.White,
    primaryContainer = DarkHighlightNavyBg,
    onPrimaryContainer = DarkHighlightNavyText,
    background = DarkAppBackground, // Nền đen tuyền #000000
    onBackground = DarkTextMain,    // Chữ xám #EDEDED
    surface = DarkCardBackground,   // Ô xám đen #12161A
    onSurface = DarkTextMain,
    surfaceVariant = DarkButtonBg,  // Ô xám #161B22
    outline = DarkCardBorder        // Viền xám đen #21262D
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
