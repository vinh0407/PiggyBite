package com.piggybite.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkMode = compositionLocalOf { false }

// Chế độ sáng: Nền xám xanh thanh lịch #f4f7fb, các thẻ trắng tinh #ffffff, viền #e5eaf1, điểm nhấn #0e85d4
private val ModernLightColorScheme = lightColorScheme(
    primary = PiggyPrimary,
    onPrimary = PureWhite,
    primaryContainer = PiggyPrimarySoftLight,
    onPrimaryContainer = PiggyPrimary,
    background = PiggyBgLight,
    onBackground = PiggyTextLight,
    surface = PiggySurfaceLight,
    onSurface = PiggyTextLight,
    surfaceVariant = PiggySurfaceSoftLight,
    outline = PiggyBorderLight
)

// Chế độ tối OLED: Nền đen thẫm #050a12, các thẻ xanh đen sâu #0d1522, viền #1e2b3e, điểm nhấn #38bdf8
private val ModernDarkColorScheme = darkColorScheme(
    primary = PiggyPrimary,
    onPrimary = Color.White,
    primaryContainer = PiggyPrimarySoftDark,
    onPrimaryContainer = DarkTitleBlue,
    background = PiggyBgDark,
    onBackground = PiggyTextDark,
    surface = PiggySurfaceDark,
    onSurface = PiggyTextDark,
    surfaceVariant = PiggySurfaceSoftDark,
    outline = PiggyBorderDark
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
