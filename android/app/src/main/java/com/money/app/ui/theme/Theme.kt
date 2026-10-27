package com.money.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalIsDarkMode = compositionLocalOf { false }

private val ModernLightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = PureWhite,
    primaryContainer = SkyBlueSoft,
    onPrimaryContainer = TextDeepNavy,
    background = ModernBackground,
    onBackground = TextDeepNavy,
    surface = CardBackground,
    onSurface = TextDeepNavy,
    surfaceVariant = SkyBlueUltraLight,
    outline = CardBorder
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = DarkTitleBlue,
    onPrimary = DarkAppBackground,
    primaryContainer = DarkCardBackground,
    onPrimaryContainer = DarkTitleBlue,
    background = DarkAppBackground,
    onBackground = DarkTextMain,
    surface = DarkCardBackground,
    onSurface = DarkTextMain,
    surfaceVariant = DarkCardBorder,
    outline = DarkCardBorder
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
